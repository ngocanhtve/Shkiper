package com.jobik.shkiper.screens.layout.NavigationBar

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jobik.shkiper.ui.theme.AppTheme

data class CustomBottomNavigationItem(
    val icon: ImageVector,
    @StringRes
    val description: Int,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

data class DefaultNavigationValues(
    val containerHeight: Dp = 58.dp
)

@Composable
fun CustomBottomNavigationItem(properties: CustomBottomNavigationItem) {
    val (contentColor, backgroundColor) = with(AppTheme.colors) {
        val contentColor = animateColorAsState(
            targetValue = if (properties.isSelected) onPrimary else onSecondaryContainer,
            label = "contentColor"
        )

        val backgroundColor = animateColorAsState(
            targetValue = if (properties.isSelected) primary else secondaryContainer,
            label = "backgroundColor",
        )

        contentColor to backgroundColor
    }

    IconButton(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = contentColor.value,
            containerColor = backgroundColor.value
        ),
        onClick = {
            properties.onClick()
        }
    ) {
        Icon(
            imageVector = properties.icon,
            contentDescription = stringResource(properties.description),
            tint = contentColor.value,
        )
    }
}

@Composable
fun CustomBottomNavigation(items: List<CustomBottomNavigationItem>) {
    Surface(
        shape = CircleShape,
        shadowElevation = 1.dp,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .clickable(enabled = false) {}
                .height(DefaultNavigationValues().containerHeight)
                .clip(shape = CircleShape)
                .background(AppTheme.colors.secondaryContainer)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach {
                CustomBottomNavigationItem(properties = it)
            }
        }
    }
}