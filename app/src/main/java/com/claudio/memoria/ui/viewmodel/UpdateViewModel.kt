package com.claudio.memoria.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudio.memoria.data.remote.UpdateManifest
import com.claudio.memoria.update.DownloadResult
import com.claudio.memoria.update.UpdateCheckResult
import com.claudio.memoria.update.UpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UpdateUiState {
    data object Idle : UpdateUiState
    data object Checking : UpdateUiState
    data object UpToDate : UpdateUiState
    data object Failed : UpdateUiState
    data class Available(val manifest: UpdateManifest) : UpdateUiState
}

sealed interface DownloadUiState {
    data object Idle : DownloadUiState
    data class Downloading(val progress: Float) : DownloadUiState
    data object PermissionRequired : DownloadUiState
    data object HashMismatch : DownloadUiState
    data class Error(val message: String) : DownloadUiState
    data object Ready : DownloadUiState
}

class UpdateViewModel(private val updateManager: UpdateManager) : ViewModel() {

    private val _updateState = MutableStateFlow<UpdateUiState>(UpdateUiState.Idle)
    val updateState: StateFlow<UpdateUiState> = _updateState.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadUiState>(DownloadUiState.Idle)
    val downloadState: StateFlow<DownloadUiState> = _downloadState.asStateFlow()

    fun check() {
        viewModelScope.launch {
            _updateState.value = UpdateUiState.Checking
            _updateState.value = when (val result = updateManager.checkForUpdate(notifyIfNew = false)) {
                is UpdateCheckResult.Available -> UpdateUiState.Available(result.manifest)
                UpdateCheckResult.UpToDate -> UpdateUiState.UpToDate
                UpdateCheckResult.Failed -> UpdateUiState.Failed
            }
        }
    }

    fun download(manifest: UpdateManifest) {
        viewModelScope.launch {
            _downloadState.value = DownloadUiState.Downloading(0f)
            val result = updateManager.downloadAndInstall(manifest) { progress ->
                _downloadState.value = DownloadUiState.Downloading(progress)
            }
            _downloadState.value = when (result) {
                is DownloadResult.Ready -> DownloadUiState.Ready
                DownloadResult.HashMismatch -> DownloadUiState.HashMismatch
                DownloadResult.PermissionRequired -> DownloadUiState.PermissionRequired
                is DownloadResult.Failed -> DownloadUiState.Error(result.message)
            }
        }
    }

    fun openInstallPermission() = updateManager.openInstallPermission()

    fun resetDownloadState() {
        _downloadState.value = DownloadUiState.Idle
    }
}
