package dev.maruffirdaus.geopocket.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.domain.settings.SettingItem
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.settings.component.SettingsGroup
import dev.maruffirdaus.geopocket.ui.settings.extension.toIcon
import dev.maruffirdaus.geopocket.ui.settings.model.SettingsGroupItem
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    navHandler: NavHandler
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text("Pengaturan")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHandler.pop<AppNavKey>()
                        }
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.ArrowLeft,
                            contentDescription = "Kembali"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var isResetProgressDialogOpen by remember { mutableStateOf(false) }

            if (isResetProgressDialogOpen) AlertDialog(
                onDismissRequest = { isResetProgressDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvent(SettingsEvent.OnResetProgress)
                            isResetProgressDialogOpen = false
                        }
                    ) {
                        Text("Konfirmasi")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { isResetProgressDialogOpen = false }
                    ) {
                        Text("Batal")
                    }
                },
                title = {
                    Text(SettingItem.ResetProgress.title)
                },
                text = {
                    Text(SettingItem.ResetProgress.description)
                }
            )

            SettingItem.entriesByGroup.forEach { (group, items) ->
                SettingsGroup(
                    title = group.title,
                    items = items.map {
                        when (it) {
                            is SettingItem.Action -> SettingsGroupItem.Action(
                                title = it.title,
                                description = it.description,
                                icon = it.toIcon(),
                                onClick = {
                                    when (it) {
                                        SettingItem.ResetProgress -> {
                                            isResetProgressDialogOpen = true
                                        }

                                        SettingItem.OpenSourceLicenses -> {
                                            navHandler.push(AppNavKey.Licenses)
                                        }
                                    }
                                }
                            )

                            is SettingItem.Switch -> SettingsGroupItem.Switch(
                                title = it.title,
                                description = it.description,
                                icon = it.toIcon(),
                                checked = uiState.checked[it] ?: it.default,
                                onCheckedChange = { checked ->
                                    onEvent(SettingsEvent.OnSwitchChanged(it, checked))
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview
private fun SettingsScreenPreview() {
    GeoPocketTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(),
            onEvent = {},
            navHandler = NavHandler()
        )
    }
}