package dev.maruffirdaus.geopocket.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.settings.component.SettingsGroup
import dev.maruffirdaus.geopocket.ui.settings.model.SettingItem
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    SettingsScreenContent(
        onEvent = viewModel::onEvent,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
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
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            var isResetProgressDialogOpen by remember { mutableStateOf(false) }
            val resetProgressItem = SettingItem(
                title = "Reset progres",
                description = "Semua progres akan dihapus dan tidak dapat dikembalikan",
                onClick = {
                    isResetProgressDialogOpen = true
                }
            )
            val items = remember {
                listOf(
                    resetProgressItem,
                    SettingItem(
                        title = "Lisensi open source",
                        description = "Lihat lisensi pustaka pihak ketiga",
                        onClick = {
                            navHandler.push(AppNavKey.Licenses)
                        }
                    )
                )
            }

            if (isResetProgressDialogOpen)
                AlertDialog(
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
                        Text(resetProgressItem.title)
                    },
                    text = {
                        Text(resetProgressItem.description)
                    }
                )

            SettingsGroup(items)
        }
    }
}

@Composable
@Preview
private fun SettingsScreenPreview() {
    GeoPocketTheme {
        SettingsScreenContent(
            onEvent = {},
            navHandler = NavHandler()
        )
    }
}