package com.jobik.shkiper.database.data.reminder

import android.content.Context
import android.util.Log
import com.jobik.shkiper.R
import com.jobik.shkiper.database.data.note.NoteMongoRepositoryImpl
import com.jobik.shkiper.database.models.Note
import com.jobik.shkiper.database.models.Reminder
import com.jobik.shkiper.database.models.RepeatMode
import com.jobik.shkiper.helpers.DateHelper
import com.jobik.shkiper.helpers.TextHelper
import com.jobik.shkiper.services.notification_service.NotificationData
import com.jobik.shkiper.services.notification_service.NotificationScheduler
import com.jobik.shkiper.services.statistics_service.StatisticsService
import com.mohamedrejeb.richeditor.model.RichTextState
import dagger.hilt.android.qualifiers.ApplicationContext
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.OffsetDateTime

class ReminderMongoRepositoryImpl(val realm: Realm, @ApplicationContext val context: Context) :
    ReminderMongoRepository {
    override fun getReminders(): Flow<List<Reminder>> {
        return realm.query<Reminder>()
            .sort("_id", Sort.DESCENDING)
            .asFlow()
            .map { it.list }
    }

    override fun getAllReminders(): List<Reminder> {
        return realm.query<Reminder>().find()
    }

    override fun getReminder(id: ObjectId): Reminder? {
        return realm.query<Reminder>(query = "_id == $0", id).first().find()
    }

    override fun getReminderForNote(noteId: ObjectId): Reminder? {
        return realm.query<Reminder>(query = "noteId == $0", noteId).first().find()
    }

    override suspend fun insertReminder(reminder: Reminder) {
        realm.write { copyToRealm(reminder) }
        val note = NoteMongoRepositoryImpl(realm, context).getNote(reminder.noteId) ?: return
        scheduleNotification(reminder, note)

        updateStatisticsCreatedRemindersCount()
    }

    override suspend fun insertOrUpdateReminders(reminders: List<Reminder>, updateStatistics: Boolean) {
        val noteMongoRepository = NoteMongoRepositoryImpl(realm, context)
        val notes = noteMongoRepository.getNotesFlow(reminders.map { it.noteId }.distinct())
        realm.write {
            for (note in notes) {
                val reminder = reminders.first { it.noteId == note._id }
                copyToRealm(reminder, UpdatePolicy.ALL)
                scheduleNotification(reminder, note)
            }
        }
        if (updateStatistics)
            updateStatisticsCreatedRemindersCount(notes.size)
    }

    private fun updateStatisticsCreatedRemindersCount(count: Int = 1) {
        // Statistics update
        val statisticsService = StatisticsService(context)
        statisticsService.appStatistics.apply {
            repeat(count) {
                createdRemindersCount.increment()
            }
        }
        statisticsService.saveStatistics()
    }

    override suspend fun updateReminder(id: ObjectId, updateParams: (Reminder) -> Unit) {
        getReminder(id)?.also { currentReminder ->
            realm.writeBlocking {
                val queriedReminder = findLatest(currentReminder) ?: return@writeBlocking
                queriedReminder.apply {
                    updateParams(this)
                }
                updateNotification(id, queriedReminder)
            }
        }
    }

    override suspend fun updateOrCreateReminderForNotes(
        notes: List<Note>,
        updateParams: (Reminder) -> Unit
    ) {
        realm.writeBlocking {
            for (note in notes) {
                try {
                    var reminder = getReminderForNote(note._id)
                    if (reminder != null) {
                        val latest = findLatest(reminder) ?: continue
                        latest.let(updateParams)
                        reminder = latest
                    } else {
                        reminder = Reminder()
                        reminder.noteId = note._id
                        reminder.let(updateParams)
                        copyToRealm(reminder)
                        updateStatisticsCreatedRemindersCount()
                    }
                    scheduleNotification(reminder, note)
                } catch (e: Exception) {
                    Log.d("ReminderMongoRepositoryImpl", "${e.message}")
                }
            }
        }
    }

    override suspend fun deleteReminder(id: ObjectId) {
        realm.write {
            val reminder = getReminder(id) ?: return@write
            try {
                findLatest(reminder)?.let { delete(it) }
                deleteNotification(id)
            } catch (e: Exception) {
                Log.d("ReminderMongoRepositoryImpl", "${e.message}")
            }
        }
    }

    override suspend fun deleteReminder(ids: List<ObjectId>) {
        realm.writeBlocking {
            for (id in ids) {
                val reminder = getReminder(id) ?: continue
                try {
                    findLatest(reminder)
                        ?.let { delete(it) }
                    deleteNotification(id)
                } catch (e: Exception) {
                    Log.d("ReminderMongoRepositoryImpl", "${e.message}")
                }
            }
        }
    }

    override suspend fun deleteReminderForNote(noteId: ObjectId) {
        realm.write {
            val reminder = getReminderForNote(noteId) ?: return@write
            try {
                findLatest(reminder)?.let { delete(it) }
                deleteNotification(reminder._id)
            } catch (e: Exception) {
                Log.d("ReminderMongoRepositoryImpl", "${e.message}")
            }
        }
    }

    private fun deleteNotification(reminderId: ObjectId) {
        val notificationScheduler = NotificationScheduler(context)
        notificationScheduler.deleteNotification(reminderId.timestamp)
    }

    private fun updateNotification(
        reminderId: ObjectId,
        queriedReminder: Reminder
    ) {
        // Update notification
        val notificationScheduler = NotificationScheduler(context)
        notificationScheduler.updateNotificationTime(
            reminderId.timestamp,
            queriedReminder.date,
            queriedReminder.time,
            queriedReminder.repeat
        )
    }

    private fun scheduleNotification(
        reminder: Reminder,
        note: Note,
    ) {
        val notificationScheduler = NotificationScheduler(context)
        notificationScheduler.createNotificationChannel(
            NotificationScheduler.Companion.NotificationChannels.NOTECHANNEL,
            context
        )
        var reminderDateTime = LocalDateTime.of(reminder.date, reminder.time)
        if (LocalDateTime.now().isAfter(reminderDateTime)) {
            if (reminder.repeat == RepeatMode.NONE) return
            else
                reminderDateTime = DateHelper.nextDateWithRepeating(reminder.date, reminder.time, reminder.repeat)
        }
        val notificationData = NotificationData(
            noteId = note._id.toHexString(),
            notificationId = reminder._id.timestamp,
            title = note.header,
            message = note.body,
            icon = R.drawable.ic_notification,
            repeatMode = reminder.repeat,
            requestCode = reminder._id.timestamp,
            trigger = reminderDateTime.toInstant(OffsetDateTime.now().offset).toEpochMilli()
        )
        notificationScheduler.scheduleNotification(notificationData)
    }
}