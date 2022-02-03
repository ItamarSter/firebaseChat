package itamar.stern.arabic.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Dialogs")
data class Dialog(
    @PrimaryKey
    val dialogKey: String,
    val user1Id: String? = null,
    val user2Id: String? = null,
    val user1name: String? = null,
    val user2name: String? = null,
    var priorityUser1: Long? = null,
    var priorityUser2: Long? = null,
    var numOfNewMessagesForUser1: Long? = null,
    var numOfNewMessagesForUser2: Long? = null
)