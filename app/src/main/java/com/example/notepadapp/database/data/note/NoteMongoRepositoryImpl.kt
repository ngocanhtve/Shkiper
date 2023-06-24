package com.example.notepadapp.database.data.note

import android.content.Context
import android.util.Log
import com.example.notepadapp.database.data.reminder.ReminderMongoRepositoryImpl
import com.example.notepadapp.database.models.Note
import com.example.notepadapp.database.models.Reminder
import com.example.notepadapp.notification_service.NotificationScheduler
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class NoteMongoRepositoryImpl(val realm: Realm) : NoteMongoRepository {
    override fun getAllNotes(): Flow<List<Note>> {
        return realm.query<Note>()
            .sort("_id", Sort.DESCENDING)
            .asFlow()
            .map { it.list }
    }

    override fun getNotes(pinned: Boolean): Flow<List<Note>> {
        return realm.query<Note>(query = "isPinned == $0", pinned)
            .sort("_id", Sort.DESCENDING)
            .asFlow()
            .map { it.list }
    }

    override fun getNotes(ids: List<ObjectId>): List<Note> {
        return realm.query<Note>().find().filter { ids.any { id -> id == it._id } }
    }

    override fun getNote(id: ObjectId): Note? {
        return realm.query<Note>(query = "_id == $0", id).first().find()
    }

    override fun filterNotesByContains(text: String): Flow<List<Note>> {
        return realm.query<Note>(query = "header CONTAINS[c] $0 OR body CONTAINS[c] $0", text).asFlow().map { it.list }
    }

    override suspend fun insertNote(note: Note) {
        realm.write { copyToRealm(note) }
    }

    override suspend fun updateNote(id: ObjectId, context: Context, updateParams: (Note) -> Unit) {
        getNote(id)?.also { currentNote ->
            realm.writeBlocking {
                val queriedNote = findLatest(currentNote) ?: return@writeBlocking
                queriedNote.apply {
                    updateParams(this)
                }
                updateNotification(context, queriedNote)
            }
        }
    }

    override suspend fun updateNote(
        ids: List<ObjectId>,
        context: Context,
        updateParams: (Note) -> Unit
    ) {
        realm.writeBlocking {
            for (id in ids) {
                val note = getNote(id) ?: continue
                try {
                    val latest = findLatest(note) ?: continue
                    latest.let(updateParams)
                    updateNotification(context, latest)
                } catch (e: Exception) {
                    Log.d("NoteMongoRepositoryImpl", "${e.message}")
                }
            }
        }
    }

    override suspend fun deleteNote(id: ObjectId, context: Context) {
        realm.write {
            val note = getNote(id) ?: return@write
            try {
                val latest = findLatest(note) ?: return@write
                delete(latest)
            } catch (e: Exception) {
                Log.d("NoteMongoRepositoryImpl", "${e.message}")
            }
        }
        ReminderMongoRepositoryImpl(realm).deleteReminderForNote(id, context)
    }

    override suspend fun deleteNote(ids: List<ObjectId>, context: Context) {
        realm.writeBlocking {
            for (id in ids) {
                val note = getNote(id) ?: continue
                try {
                    findLatest(note)
                        ?.let { delete(it) }
                } catch (e: Exception) {
                    Log.d("NoteMongoRepositoryImpl", "${e.message}")
                }
            }
        }
        for (id in ids)
            ReminderMongoRepositoryImpl(realm).deleteReminderForNote(id, context)
    }

    private fun updateNotification(
        context: Context,
        newNote: Note
    ) {
        // Update notification
        val notificationScheduler = NotificationScheduler(context)
        notificationScheduler.updateNotificationData(
            newNote._id.toHexString(),
            newNote.header,
            newNote.body
        )
    }
}