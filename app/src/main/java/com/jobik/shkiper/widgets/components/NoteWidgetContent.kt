package com.jobik.shkiper.widgets.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.*
import com.jobik.shkiper.helpers.DateHelper
import com.jobik.shkiper.util.ThemePreferenceUtil
import com.jobik.shkiper.util.ThemeUtil
import com.jobik.shkiper.widgets.WidgetKeys.Prefs.noteBody
import com.jobik.shkiper.widgets.WidgetKeys.Prefs.noteHeader
import com.jobik.shkiper.widgets.WidgetKeys.Prefs.noteId
import com.jobik.shkiper.widgets.WidgetKeys.Prefs.noteLastUpdate
import java.time.LocalDateTime

@Composable
fun NoteWidgetContent(prefs: Preferences) {
    ThemeUtil.theme = ThemePreferenceUtil(LocalContext.current).getSavedUserTheme()
    val theme = ThemeUtil.themeColors

    val noteId = prefs[noteId].orEmpty()
    val noteHeader = prefs[noteHeader].orEmpty()
    val noteBody = prefs[noteBody].orEmpty()
    val updatedAt = prefs[noteLastUpdate].orEmpty()
    val updatedDateString = getLocalizedDateTime(updatedAt)

    Box(
        modifier = GlanceModifier.background(theme.mainBackground).cornerRadius(15.dp).appWidgetBackground()
    ) {
        LazyColumn {
            item {
                Spacer(modifier = GlanceModifier.height(16.dp))
            }
            if (noteHeader.isNotEmpty()) item {
                Text(
                    text = noteHeader, modifier = GlanceModifier.padding(horizontal = 16.dp), style = TextStyle(
                        color = ColorProvider(day = theme.text, night = theme.text),
                        fontFamily = FontFamily("Roboto"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            if (noteBody.isNotEmpty() && noteHeader.isNotEmpty()) item {
                Spacer(modifier = GlanceModifier.height(3.dp))
            }
            if (noteBody.isNotEmpty()) item {
                Text(
                    text = noteBody, modifier = GlanceModifier.padding(horizontal = 16.dp), style = TextStyle(
                        color = ColorProvider(day = theme.text, night = theme.text),
                        fontFamily = FontFamily("Roboto"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            if (noteBody.isNotEmpty() && updatedDateString != null) item {
                Text(
                    text = updatedDateString.toString(),
                    modifier = GlanceModifier.padding(end = 16.dp).padding(top = 4.dp).fillMaxWidth(),
                    style = TextStyle(
                        color = ColorProvider(day = theme.textSecondary, night = theme.textSecondary),
                        fontFamily = FontFamily("Roboto"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Right
                    )
                )
            }
            item {
                Spacer(modifier = GlanceModifier.height(16.dp))
            }
        }
    }
}

private fun getLocalizedDateTime(dateTime: String): String? {
    return try {
        DateHelper.getLocalizedDate(LocalDateTime.parse(dateTime))
    } catch (e: Exception) {
        null
    }
}