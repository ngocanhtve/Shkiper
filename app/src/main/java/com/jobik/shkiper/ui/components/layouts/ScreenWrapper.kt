package com.jobik.shkiper.ui.components.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jobik.shkiper.ui.theme.AppTheme

val ScreenWrapperContentMaxWidth = 500.dp

@Composable
fun ScreenWrapper(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier.widthIn(max = ScreenWrapperContentMaxWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}