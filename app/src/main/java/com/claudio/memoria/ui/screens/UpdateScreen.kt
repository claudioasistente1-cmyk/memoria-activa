package com.claudio.memoria.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.claudio.memoria.BuildConfig
import com.claudio.memoria.ui.components.BigButton
import com.claudio.memoria.ui.components.SectionCard
import com.claudio.memoria.ui.viewmodel.DownloadUiState
import com.claudio.memoria.ui.viewmodel.UpdateUiState
import com.claudio.memoria.ui.viewmodel.UpdateViewModel

@Composable
fun UpdateScreen(viewModel: UpdateViewModel, onBack: () -> Unit) {
    val updateState by viewModel.updateState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (updateState is UpdateUiState.Idle) viewModel.check()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(56.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(
                "Actualizaciones",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            "Versión instalada: ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (val state = updateState) {
            UpdateUiState.Idle, UpdateUiState.Checking -> {
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(34.dp))
                        Spacer(Modifier.size(16.dp))
                        Text("Buscando actualizaciones...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            UpdateUiState.UpToDate -> {
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.size(14.dp))
                        Text(
                            "Ya tenés la última versión.",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                BigButton(
                    text = "Volver",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            UpdateUiState.Failed -> {
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.size(14.dp))
                        Text(
                            "No se pudo comprobar. Revisá tu conexión a internet.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                BigButton(
                    text = "Reintentar",
                    onClick = viewModel::check,
                    modifier = Modifier.fillMaxWidth()
                )
                BigButton(
                    text = "Volver",
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    outlined = true
                )
            }

            is UpdateUiState.Available -> {
                val manifest = state.manifest
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.size(14.dp))
                        Column {
                            Text(
                                "Nueva versión disponible",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Versión ${manifest.versionName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (manifest.releaseNotes.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Text("Qué cambia", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            manifest.releaseNotes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                when (val download = downloadState) {
                    DownloadUiState.Idle -> {
                        BigButton(
                            text = "Actualizar",
                            onClick = { viewModel.download(manifest) },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Filled.CloudDownload
                        )
                        BigButton(
                            text = "Más tarde",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            outlined = true
                        )
                    }

                    is DownloadUiState.Downloading -> {
                        SectionCard {
                            Text(
                                "Descargando... ${(download.progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { download.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp),
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "No cierres la aplicación.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DownloadUiState.PermissionRequired -> {
                        SectionCard {
                            Text(
                                "Para instalar la actualización, Android necesita tu permiso.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "Tocá el botón, activá \"Permitir desde esta fuente\" y volvé a intentar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        BigButton(
                            text = "Permitir instalación",
                            onClick = viewModel::openInstallPermission,
                            modifier = Modifier.fillMaxWidth()
                        )
                        BigButton(
                            text = "Ya lo permití, descargar",
                            onClick = { viewModel.download(manifest) },
                            modifier = Modifier.fillMaxWidth(),
                            outlined = true
                        )
                    }

                    DownloadUiState.HashMismatch -> {
                        ErrorCard("El archivo descargado no es válido. Por seguridad no se instaló.")
                        BigButton(
                            text = "Reintentar",
                            onClick = {
                                viewModel.resetDownloadState()
                                viewModel.download(manifest)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        BigButton(
                            text = "Más tarde",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            outlined = true
                        )
                    }

                    is DownloadUiState.Error -> {
                        ErrorCard("No se pudo descargar: ${download.message}")
                        BigButton(
                            text = "Reintentar",
                            onClick = {
                                viewModel.resetDownloadState()
                                viewModel.download(manifest)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        BigButton(
                            text = "Más tarde",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            outlined = true
                        )
                    }

                    DownloadUiState.Ready -> {
                        SectionCard {
                            Text(
                                "El instalador de Android se abrió. Seguí las instrucciones en pantalla " +
                                    "para terminar la actualización. Tu progreso no se borra.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        BigButton(
                            text = "Volver",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(34.dp)
            )
            Spacer(Modifier.size(14.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
