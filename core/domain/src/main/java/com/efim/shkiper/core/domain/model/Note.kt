package com.efim.shkiper.core.domain.model

import androidx.annotation.Keep
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime

@Keep
enum class NotePosition {
    MAIN,
    ARCHIVE,
    DELETE,
}

class Note : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()

    @Index
    var header: String = ""

    @Index
    var body: String = ""
    var hashtags: RealmSet<String> = realmSetOf()
    var creationDateString: String = ""
    var updateDateString: String = ""
    var deletionDateString: String? = null
    var photos: RealmList<String> = realmListOf()

    @Index
    var isPinned: Boolean = false
    var isTaskList: Boolean = false
    var linkPreviewEnabled: Boolean = true

    @Index
    var positionString: String = NotePosition.MAIN.name

    var position: NotePosition
        get() = NotePosition.valueOf(positionString)
        set(value) {
            positionString = value.name
        }

    var creationDate: LocalDateTime
        get() {
            return try {
                LocalDateTime.parse(creationDateString)
            } catch (e: Exception) {
                LocalDateTime.now()
            }
        }
        set(value) {
            creationDateString = value.toString()
        }

    var updateDate: LocalDateTime
        get() {
            return try {
                LocalDateTime.parse(updateDateString)
            } catch (e: Exception) {
                LocalDateTime.now()
            }
        }
        set(value) {
            updateDateString = value.toString()
        }

    var deletionDate: LocalDateTime?
        get() {
            return try {
                LocalDateTime.parse(deletionDateString)
            } catch (e: Exception) {
                null
            }
        }
        set(value) {
            deletionDateString = value.toString()
        }
}