package com.jobik.shkiper.ui.components.layouts

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jobik.shkiper.ui.theme.AppTheme

@Composable
fun ScreenContentIfNoData(modifier: Modifier = Modifier, @StringRes title: Int, icon: ImageVector) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppTheme.colors.primary,
            modifier = Modifier.size(90.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = AppTheme.colors.text
        )
    }
}