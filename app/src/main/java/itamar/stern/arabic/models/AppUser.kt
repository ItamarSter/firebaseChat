package itamar.stern.arabic.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.DataSnapshot


data class AppUser(
    val userId: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val color: Int? = null
)

@Entity(tableName = "Users")
data class RoomUser(
    @PrimaryKey
    val userId: String,
    val email: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val color: Int? = null
)