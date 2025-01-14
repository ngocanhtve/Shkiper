package com.jobik.shkiper.screens.settings

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jobik.shkiper.NotepadApplication
import com.jobik.shkiper.R
import com.jobik.shkiper.navigation.NavigationHelpers.Companion.navigateToSecondary
import com.jobik.shkiper.navigation.Screen
import com.jobik.shkiper.services.backup.BackupService
import com.jobik.shkiper.ui.components.buttons.CustomSwitch
import com.jobik.shkiper.ui.components.buttons.DropDownButton
import com.jobik.shkiper.ui.components.buttons.DropDownButtonSizeMode
import com.jobik.shkiper.ui.components.buttons.DropDownItem
import com.jobik.shkiper.ui.components.cards.SettingsItem
import com.jobik.shkiper.ui.components.cards.ThemePreview
import com.jobik.shkiper.ui.components.layouts.SettingsGroup
import com.jobik.shkiper.ui.components.modals.onboarding.OnboardingDialog
import com.jobik.shkiper.ui.helpers.allWindowInsetsPadding
import com.jobik.shkiper.ui.modifiers.circularRotation
import com.jobik.shkiper.ui.theme.AppTheme
import com.jobik.shkiper.ui.theme.CustomThemeColors
import com.jobik.shkiper.ui.theme.CustomThemeStyle
import com.jobik.shkiper.ui.theme.getDynamicColors
import com.jobik.shkiper.util.settings.NightMode
import com.jobik.shkiper.util.settings.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.enums.EnumEntries

@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 10.dp)
            .allWindowInsetsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(75.dp))
        ProgramSettings(navController = navController, settingsViewModel = settingsViewModel)
        BackupSettings(settingsViewModel = settingsViewModel)
        OtherSettings(navController = navController)
        DevSupportSettings(settingsViewModel = settingsViewModel, navController = navController)
        InformationSettings()
        Spacer(Modifier.height(55.dp))
    }
}

@Composable
private fun InformationSettings() {
    SettingsGroup(header = stringResource(R.string.Information)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 8.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.Info),
                tint = AppTheme.colors.textSecondary
            )
            Spacer(Modifier.width(20.dp))
            Text(
                text = stringResource(R.string.AppDataPolitics),
                color = AppTheme.colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DevSupportSettings(
    settingsViewModel: SettingsViewModel,
    navController: NavController
) {
    SettingsGroup(header = stringResource(R.string.Support), accent = true) {
        SettingsItem(
            modifier = Modifier.heightIn(min = 50.dp),
            icon = Icons.Outlined.Stars,
            title = stringResource(R.string.RateTheApp),
            onClick = { settingsViewModel.rateTheApp() }
        )
        SettingsItem(
            modifier = Modifier.heightIn(min = 50.dp),
            icon = Icons.Rounded.RocketLaunch,
            title = stringResource(R.string.SupportDevelopment),
            onClick = { navController.navigateToSecondary(Screen.Purchases) }
        )
    }
}

@Composable
private fun OtherSettings(navController: NavController) {
    SettingsGroup(header = stringResource(R.string.Other)) {
        SettingsItem(
            icon = Icons.Outlined.Info,
            title = stringResource(R.string.AboutNotepad),
            onClick = { navController.navigateToSecondary(Screen.AboutNotepad) }
        )
        SettingsItem(
            icon = Icons.Rounded.DataUsage,
            title = stringResource(R.string.StatisticsPage),
            onClick = { navController.navigateToSecondary(Screen.Statistics) }
        )
        var isOnboarding by rememberSaveable { mutableStateOf(false) }

        SettingsItem(
            icon = Icons.Outlined.ViewCarousel,
            title = stringResource(R.string.OnboardingPage),
            onClick = { isOnboarding = true }
        )
        BackHandler(isOnboarding) {
            isOnboarding = isOnboarding.not()
        }
        OnboardingDialog(isVisible = isOnboarding) {
            isOnboarding = false
        }
    }
}

@Composable
private fun BackupSettings(
    settingsViewModel: SettingsViewModel,
) {
    val selectBackup =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                settingsViewModel.uploadLocalBackup(uri)
            }
        }

    val createBackup =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(
                BackupService.BackupType
            )
        ) { uri ->
            if (uri != null) {
                settingsViewModel.saveLocalBackup(uri)
            }
        }

    SettingsGroup(header = stringResource(R.string.Backup)) {
        val isLocalBackupSaving = settingsViewModel.settingsScreenState.value.isLocalBackupSaving

        SettingsItem(
            modifier = Modifier.heightIn(min = 50.dp),
            icon = Icons.Rounded.Download,
            isEnabled = !settingsViewModel.isBackupHandling(),
            isActive = isLocalBackupSaving,
            title = if (isLocalBackupSaving) stringResource(R.string.Saving) else stringResource(R.string.Save),
            onClick = { createBackup.launch(BackupService.getFileName()) }
        ) {
            val isSaving = rememberSaveable { mutableStateOf(false) }
            DelayedStateChange(incoming = isLocalBackupSaving, outgoing = isSaving)

            AnimatedContent(
                targetState = isLocalBackupSaving || isSaving.value,
                label = ""
            ) { processing ->
                if (processing) {
                    AnimatedContent(
                        targetState = isLocalBackupSaving && isSaving.value, label = ""
                    ) { loading ->
                        if (loading) {
                            Icon(
                                imageVector = Icons.Outlined.Loop,
                                contentDescription = null,
                                tint = AppTheme.colors.onPrimary,
                                modifier = Modifier.circularRotation()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Done,
                                contentDescription = null,
                                tint = AppTheme.colors.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
        }

        val isLocalBackupUploading =
            settingsViewModel.settingsScreenState.value.isLocalBackupUploading

        SettingsItem(
            modifier = Modifier.heightIn(min = 50.dp),
            icon = Icons.Rounded.Upload,
            isEnabled = !settingsViewModel.isBackupHandling(),
            isActive = isLocalBackupUploading,
            title = if (isLocalBackupUploading) stringResource(R.string.Loading) else stringResource(
                R.string.Upload
            ),
            onClick = { selectBackup.launch(arrayOf(BackupService.BackupType)) }
        ) {
            val isUploading = rememberSaveable { mutableStateOf(false) }
            DelayedStateChange(incoming = isLocalBackupUploading, outgoing = isUploading)

            AnimatedContent(
                targetState = isLocalBackupUploading || isUploading.value,
                label = ""
            ) { processing ->
                if (processing) {
                    AnimatedContent(
                        targetState = isLocalBackupUploading && isUploading.value, label = ""
                    ) { loading ->
                        if (loading) {
                            Icon(
                                imageVector = Icons.Outlined.Loop,
                                contentDescription = null,
                                tint = AppTheme.colors.onPrimary,
                                modifier = Modifier.circularRotation()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Done,
                                contentDescription = null,
                                tint = AppTheme.colors.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DelayedStateChange(
    incoming: Boolean,
    outgoing: MutableState<Boolean>,
    delay: Long = 5000L
) {
    LaunchedEffect(incoming) {
        if (incoming) {
            outgoing.value = true
        } else {
            delay(delay)
            outgoing.value = false
        }
    }
}

@Composable
private fun ProgramSettings(navController: NavController, settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val systemNightMode = isSystemInDarkTheme()

    SettingsGroup(header = stringResource(R.string.Application)) {
        SettingsItem(
            icon = Icons.Rounded.Contrast,
            title = stringResource(R.string.ApplicationTheme),
            onClick = { toggleNightMode(context = context, systemNightMode = systemNightMode) }
        ) {
            CustomSwitch(
                active = when (SettingsManager.settings.nightMode) {
                    NightMode.Light -> false
                    NightMode.Dark -> true
                    else -> isSystemInDarkTheme()
                },
                onClick = { toggleNightMode(context = context, systemNightMode = systemNightMode) })
        }
        SettingsColorThemePicker(settingsViewModel)
        Spacer(Modifier.height(4.dp))
        SettingsItemSelectLanguage(settingsViewModel = settingsViewModel)
        SettingsItem(
            icon = Icons.Rounded.Tune,
            title = stringResource(R.string.advanced),
            onClick = { navController.navigateToSecondary(Screen.AdvancedSettings) }
        )
    }
}

private fun toggleNightMode(context: Context, systemNightMode: Boolean) {
    SettingsManager.update(
        context = context,
        settings = SettingsManager.settings.copy(
            nightMode =
            when (SettingsManager.settings.nightMode) {
                NightMode.Light -> NightMode.Dark
                NightMode.Dark -> NightMode.Light
                else -> if (systemNightMode) NightMode.Light else NightMode.Dark
            }
        )
    )
}

@Composable
private fun SettingsColorThemePicker(settingsViewModel: SettingsViewModel) {
    val isDarkMode = when (SettingsManager.settings.nightMode) {
        NightMode.Light -> false
        NightMode.Dark -> true
        else -> isSystemInDarkTheme()
    }
    val colorValues =
        if (isDarkMode) CustomThemeStyle.entries.map { it.dark } else CustomThemeStyle.entries.map { it.light }
    val colorValuesName = CustomThemeStyle.entries
    val selectedThemeName = SettingsManager.settings.theme.name
    val context = LocalContext.current

    Column(
        Modifier.padding(vertical = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        SettingsItem(
            title = stringResource(R.string.ApplicationColors),
            icon = Icons.Outlined.Palette
        )
        LazyRow(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(colorValues.size) { theme ->
                val colors = remember {
                    getColors(
                        context = context,
                        colorValuesName,
                        theme,
                        isDarkMode,
                        colorValues
                    )
                }

                ThemePreview(
                    colors = colors,
                    selected = colorValuesName[theme].name == selectedThemeName
                ) {
                    SettingsManager.update(
                        context = context,
                        settings = SettingsManager.settings.copy(
                            theme = colorValuesName[theme]
                        )
                    )
                }
            }
        }
    }
}

private fun getColors(
    context: Context,
    colorValuesName: EnumEntries<CustomThemeStyle>,
    theme: Int,
    isDarkMode: Boolean?,
    colorValues: List<CustomThemeColors>
) = when {
    colorValuesName[theme] == CustomThemeStyle.MaterialDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
        getDynamicColors(darkTheme = isDarkMode == true, context = context)

    else -> colorValues[theme]
}


@Composable
private fun SettingsItemSelectLanguage(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentLanguage = NotepadApplication.currentLanguage
    val dropDownItems =
        remember { settingsViewModel.getLocalizationList(context).map { DropDownItem(text = it) } }
    val isExpanded = remember { mutableStateOf(false) }

    SettingsItem(
        icon = Icons.Outlined.Language,
        title = stringResource(R.string.ChoseLocalization),
        onClick = { isExpanded.value = true }
    ) {
        DropDownButton(
            items = dropDownItems,
            selectedIndex = currentLanguage.ordinal,
            expanded = isExpanded,
            stretchMode = DropDownButtonSizeMode.STRERCHBYCONTENT,
            onChangedSelection = {
                settingsViewModel.selectLocalization(it)
                recreateActivity(context, coroutineScope)
            }) {
            Text(
                text = currentLanguage.getLocalizedValue(context),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = AppTheme.colors.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun recreateActivity(context: Context, scope: CoroutineScope) {
    /*******************
     * If you remove the delay, an error will be generated
     * ANR in com.example.notepadapp (com.example.notepadapp/.activity.MainActivity)
     * PID: 16898
     * Reason: Input dispatching timed out (Waiting because no window has focus but there is a focused application that may eventually add a window when it finishes starting up.)
     * Load: 0.85 / 0.24 / 0.12
     * possible problem drop down layout
     *******************/

    scope.launch {
        delay(150)
        (context as? Activity)?.recreate()
    }
}