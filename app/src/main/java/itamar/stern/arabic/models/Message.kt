package itamar.stern.arabic.models

import androidx.room.Entity
import androidx.room.PrimaryKey



data class Message (
    val timestamp: Long? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val message: String? = null,
    val date: String? = null,
    val time: String? = null,
    val wasRead: Boolean? = null
)


@Entity(tableName = "Messages")
data class RoomMessage(
    @PrimaryKey
    val timestamp: Long,
    val senderId: String? = null,
    val receiverId: String? = null,
    val message: String? = null,
    val date: String? = null,
    val time: String? = null,
    var wasRead: Boolean = false
)

data class HelpingMessage(
    val timestamp: Long? = null,
    val message: String? = null,
    val hasNewMessages: Boolean? = false
)