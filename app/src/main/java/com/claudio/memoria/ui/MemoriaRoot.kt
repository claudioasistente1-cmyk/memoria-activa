package com.claudio.memoria.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.claudio.memoria.data.prefs.AppSettings
import com.claudio.memoria.di.AppContainer
import com.claudio.memoria.ui.game.PlayScreen
import com.claudio.memoria.ui.screens.DailyScreen
import com.claudio.memoria.ui.screens.GamesScreen
import com.claudio.memoria.ui.screens.HomeScreen
import com.claudio.memoria.ui.screens.ProgressScreen
import com.claudio.memoria.ui.screens.SettingsScreen
import com.claudio.memoria.ui.screens.UpdateScreen
import com.claudio.memoria.ui.viewmodel.DailyViewModel
import com.claudio.memoria.ui.viewmodel.GamesViewModel
import com.claudio.memoria.ui.viewmodel.HomeViewModel
import com.claudio.memoria.ui.viewmodel.ProgressViewModel
import com.claudio.memoria.ui.viewmodel.SettingsViewModel
import com.claudio.memoria.ui.viewmodel.UpdateViewModel
import kotlinx.coroutines.flow.StateFlow

data class IntentDestination(val route: String, val token: Long)

private data class BottomItem(val route: String, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Inicio", Icons.Filled.Home),
    BottomItem(Routes.GAMES, "Juegos", Icons.Filled.SportsEsports),
    BottomItem(Routes.DAILY, "Hoy", Icons.Filled.Today),
    BottomItem(Routes.PROGRESS, "Progreso", Icons.Filled.Insights),
    BottomItem(Routes.SETTINGS, "Ajustes", Icons.Filled.Settings)
)

@Composable
fun MemoriaRoot(container: AppContainer, destination: StateFlow<IntentDestination?>) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val factory = remember(container) { appViewModelFactory(container) }
    val context = LocalContext.current
    val settings by container.settingsRepository.settings
        .collectAsStateWithLifecycle(initialValue = AppSettings())

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(navController) {
        destination.collect { dest ->
            if (dest != null) {
                navController.navigate(dest.route) { launchSingleTop = true }
            }
        }
    }

    val showBottomBar = currentRoute in bottomItems.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label, Modifier.padding(2.dp)) },
                            label = {
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    textAlign = TextAlign.Center
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                val vm: HomeViewModel = viewModel(factory = factory)
                HomeScreen(
                    viewModel = vm,
                    onPlayDaily = { navController.navigate(Routes.DAILY) { launchSingleTop = true } },
                    onOpenGames = { navController.navigate(Routes.GAMES) { launchSingleTop = true } },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) { launchSingleTop = true } }
                )
            }
            composable(Routes.GAMES) {
                val vm: GamesViewModel = viewModel(factory = factory)
                GamesScreen(
                    viewModel = vm,
                    onPlay = { type, level -> navController.navigate(Routes.play(type.id, level)) }
                )
            }
            composable(Routes.DAILY) {
                val vm: DailyViewModel = viewModel(factory = factory)
                DailyScreen(
                    viewModel = vm,
                    onPlay = { type, level -> navController.navigate(Routes.play(type.id, level)) }
                )
            }
            composable(Routes.PROGRESS) {
                val vm: ProgressViewModel = viewModel(factory = factory)
                ProgressScreen(viewModel = vm)
            }
            composable(Routes.SETTINGS) {
                val vm: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(
                    viewModel = vm,
                    onOpenUpdate = { navController.navigate(Routes.UPDATE) { launchSingleTop = true } }
                )
            }
            composable(Routes.UPDATE) {
                val vm: UpdateViewModel = viewModel(factory = factory)
                UpdateScreen(viewModel = vm, onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.PLAY,
                arguments = listOf(
                    navArgument("gameId") { type = NavType.StringType },
                    navArgument("level") { type = NavType.IntType; defaultValue = 1 }
                )
            ) { entry ->
                val gameId = entry.arguments?.getString("gameId") ?: ""
                val level = entry.arguments?.getInt("level") ?: 1
                PlayScreen(
                    container = container,
                    gameId = gameId,
                    level = level,
                    soundEnabled = settings.soundEnabled,
                    onExit = { navController.popBackStack() }
                )
            }
        }
    }
}
