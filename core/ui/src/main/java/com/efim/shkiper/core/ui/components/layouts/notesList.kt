package com.jobik.shkiper.ui.components.layouts

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import com.jobik.shkiper.database.models.Note
import com.jobik.shkiper.database.models.Reminder
import com.jobik.shkiper.ui.components.cards.NoteCard
import com.jobik.shkiper.ui.helpers.rememberNextReminder
import org.mongodb.kbson.ObjectId

fun LazyStaggeredGridScope.notesList(
    notes: List<Note>,
    reminders: List<Reminder> = emptyList(),
    selected: Set<ObjectId> = emptySet(),
    marker: String? = null,
    onClick: (Note) -> Unit,
    onLongClick: ((Note) -> Unit)? = null,
) {
    items(items = notes, key = { it._id.toHexString() }) { note ->
        NoteCard(
            header = note.header,
            text = note.body,
            reminder = rememberNextReminder(reminders = reminders.filter { it.noteId == note._id }),
            markedText = marker,
            selected = note._id in selected,
            onClick = { onClick(note) },
            onLongClick = {
                if (onLongClick != null) {
                    onLongClick(note)
                }
            },
        )
    }
}