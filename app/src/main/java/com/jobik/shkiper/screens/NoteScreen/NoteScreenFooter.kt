package com.jobik.shkiper.screens.NoteScreen

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jobik.shkiper.R
import com.jobik.shkiper.database.models.NotePosition
import com.jobik.shkiper.ui.animation.AnimateVerticalSwitch
import com.jobik.shkiper.ui.components.buttons.DropDownButton
import com.jobik.shkiper.ui.components.buttons.DropDownButtonSizeMode
import com.jobik.shkiper.ui.components.buttons.DropDownItem
import com.jobik.shkiper.ui.components.layouts.RichTextBottomToolBar
import com.jobik.shkiper.ui.helpers.horizontalWindowInsetsPadding
import com.jobik.shkiper.ui.theme.CustomTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NoteScreenFooter(navController: NavController, noteViewModel: NoteViewModel, richTextState: RichTextState) {
    val backgroundColor by animateColorAsState(
        if (noteViewModel.screenState.value.isBottomAppBarHover || noteViewModel.screenState.value.isStyling) CustomTheme.colors.secondaryContainer else CustomTheme.colors.background,
        label = "backgroundColor",
    )

    val contentColorValue =
        if (noteViewModel.screenState.value.isBottomAppBarHover || noteViewModel.screenState.value.isStyling) CustomTheme.colors.onSecondaryContainer else CustomTheme.colors.textSecondary

    val contentColor by animateColorAsState(
        contentColorValue, label = "contentColor",
    )

    val shadowElevation = animateDpAsState(
        targetValue = if (noteViewModel.screenState.value.isBottomAppBarHover) 8.dp else 0.dp,
        label = "shadowElevation"
    )

    val barHeight = 54.dp

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shadowElevation = shadowElevation.value
    ) {
        AnimateVerticalSwitch(
            modifier = Modifier,
            directionUp = true,
            state = noteViewModel.screenState.value.isStyling,
            topComponent = {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                        .horizontalWindowInsetsPadding()
                        .height(barHeight)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row {
                        IconButton(
                            onClick = { noteViewModel.switchStyling() },
                            modifier = Modifier
                                .alpha(if (noteViewModel.screenState.value.isStylingEnabled) 1f else .5f),
                            enabled = noteViewModel.screenState.value.isStylingEnabled
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.TextFormat,
                                contentDescription = "",
                                tint = if (noteViewModel.screenState.value.isStyling && noteViewModel.screenState.value.isStylingEnabled) CustomTheme.colors.text else contentColor,
                            )
                        }
                    }
                    if (noteViewModel.screenState.value.intermediateStates.size < 2) {
                        Text(
                            text = "${stringResource(R.string.ChangedAt)} ${getLastUpdatedNoteTime(noteViewModel)}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 10.dp)
                                .basicMarquee(),
                            style = MaterialTheme.typography.body1.copy(fontSize = 14.sp),
                            color = contentColor
                        )
                    } else {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                        ) {
                            IconButton(
                                onClick = {
                                    noteViewModel.noteStateGoBack()
                                    richTextState.setHtml(noteViewModel.screenState.value.intermediateStates[noteViewModel.screenState.value.currentIntermediateIndex].body)
                                },
                                enabled = noteViewModel.screenState.value.currentIntermediateIndex > 0
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.undo_fill0_wght400_grad0_opsz24),
                                    contentDescription = stringResource(R.string.GoBack),
                                    tint = if (noteViewModel.screenState.value.currentIntermediateIndex > 0) CustomTheme.colors.text else contentColor,
                                )
                            }
                            IconButton(
                                onClick = {
                                    noteViewModel.noteStateGoNext()
                                    richTextState.setHtml(noteViewModel.screenState.value.intermediateStates[noteViewModel.screenState.value.currentIntermediateIndex].body)
                                },
                                enabled = noteViewModel.screenState.value.currentIntermediateIndex < noteViewModel.screenState.value.intermediateStates.size - 1
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.redo_fill0_wght400_grad0_opsz24),
                                    contentDescription = stringResource(R.string.GoForward),
                                    tint = if (noteViewModel.screenState.value.currentIntermediateIndex < noteViewModel.screenState.value.intermediateStates.size - 1) CustomTheme.colors.text else contentColor,
                                )
                            }
                        }
                    }
                    Row {
                        val dropDownItems =
                            if (noteViewModel.screenState.value.notePosition == NotePosition.DELETE)
                                listOf(
                                    DropDownItem(
                                        text = stringResource(R.string.Delete),
                                        icon = Icons.Outlined.DeleteForever
                                    )
                                ) else
                                listOf(
                                    DropDownItem(
                                        text = stringResource(R.string.ShareNote),
                                        icon = Icons.Outlined.Share
                                    ),
                                    DropDownItem(
                                        text = stringResource(R.string.CreateWidget),
                                        icon = Icons.Outlined.Widgets
                                    ),
                                    DropDownItem(
                                        text = stringResource(if (noteViewModel.screenState.value.linkPreviewEnabled) R.string.DisableLinkPreviews else R.string.EnableLinkPreviews),
                                        icon = if (noteViewModel.screenState.value.linkPreviewEnabled) Icons.Outlined.LinkOff else Icons.Outlined.Link
                                    ),
                                    DropDownItem(
                                        text = stringResource(R.string.Delete),
                                        icon = if (noteViewModel.screenState.value.notePosition == NotePosition.DELETE) Icons.Outlined.DeleteForever else Icons.Outlined.Delete
                                    )
                                )
                        val isExpanded = remember { mutableStateOf(false) }
                        DropDownButton(
                            items = dropDownItems,
                            selectedIndex = 0,
                            expanded = isExpanded,
                            modifier = Modifier,
                            stretchMode = DropDownButtonSizeMode.STRERCHBYCONTENT,
                            onChangedSelection = { index ->
                                if (noteViewModel.screenState.value.notePosition == NotePosition.DELETE)
                                    when (index) {
                                        0 -> noteViewModel.switchDeleteDialogShow()
                                    }
                                else
                                    when (index) {
                                        0 -> noteViewModel.switchShowShareDialog()
                                        1 -> noteViewModel.createWidget()
                                        2 -> noteViewModel.switchLinkPreviewEnabled()
                                        3 -> {
                                            if (noteViewModel.screenState.value.notePosition == NotePosition.DELETE)
                                                noteViewModel.deleteNote()
                                            else
                                                noteViewModel.moveToBasket()
                                        }
                                    }
                            }
                        ) {
                            IconButton(
                                onClick = { it() },
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = stringResource(R.string.Open),
                                    tint = contentColor
                                )
                            }
                        }
                    }
                }
            },
            bottomComponent = {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
                        .height(barHeight),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    RichTextBottomToolBar(state = richTextState, onClose = { noteViewModel.switchStyling(false) })
                }
            }
        )
    }
}

private fun getLastUpdatedNoteTime(noteViewModel: NoteViewModel): String {
    val duration = Duration.between(noteViewModel.screenState.value.updatedDate, LocalDateTime.now())
    return if (duration.toDays() > 0)
        noteViewModel.screenState.value.updatedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm"))
    else
        noteViewModel.screenState.value.updatedDate.format(DateTimeFormatter.ofPattern("HH:mm"))
}