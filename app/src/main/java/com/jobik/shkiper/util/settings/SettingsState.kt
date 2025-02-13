package com.jobik.shkiper.util.settings

import androidx.annotation.Keep
import com.jobik.shkiper.database.models.NotificationColor
import com.jobik.shkiper.database.models.NotificationIcon
import com.jobik.shkiper.ui.theme.CustomThemeStyle

@Keep
enum class NightMode {
    Light,
    Dark,
    System,
}

@Keep
data class SettingsState(
    val fontScale: Float = 1f,
    val checkUpdates: Boolean = true,
    val secureMode: Boolean = false,
    val nightMode: NightMode = NightMode.System,
    val theme: CustomThemeStyle = CustomThemeStyle.MaterialDynamicColors,
    val defaultNotificationIcon: NotificationIcon = NotificationIcon.EVENT,
    val defaultNotificationColor: NotificationColor = NotificationColor.MATERIAL
)