package dev.maruffirdaus.geopocket.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import dev.maruffirdaus.geopocket.ui.common.extension.alignHorizontalSpace
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

        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = innerPadding + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(SettingItem.Group.entries) { group ->
                val items = SettingItem.entriesByGroup[group] ?: return@items

                SettingsGroup(
                    title = group.title,
                    items = items.map { item ->
                        when (item) {
                            is SettingItem.Action -> SettingsGroupItem.Action(
                                title = item.title,
                                description = item.description,
                                icon = item.toIcon(),
                                onClick = {
                                    when (item) {
                                        SettingItem.ResetProgress -> {
                                            isResetProgressDialogOpen = true
                                        }

                                        SettingItem.Licenses -> {
                                            navHandler.push(AppNavKey.Licenses)
                                        }
                                    }
                                }
                            )

                            is SettingItem.Switch -> SettingsGroupItem.Switch(
                                title = item.title,
                                description = item.description,
                                icon = item.toIcon(),
                                checked = uiState.checked[item] ?: item.default,
                                onCheckedChange = { checked ->
                                    onEvent(SettingsEvent.OnSwitchChanged(item, checked))
                                }
                            )
                        }
                    },
                    modifier = Modifier.alignHorizontalSpace()
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