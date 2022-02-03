package itamar.stern.arabic.roomDatabase.dao



import androidx.lifecycle.LiveData
import androidx.room.*
import itamar.stern.arabic.models.RoomMessage
import java.sql.Timestamp


@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMessage(message: RoomMessage)

    @Query("UPDATE Messages SET wasRead = 1 WHERE senderId=:senderId AND receiverId=:receiverId")
    fun setWasRead(senderId: String, receiverId: String)

    @Query("SELECT COUNT(*) FROM Messages WHERE senderId=:senderId AND receiverId=:receiverId AND wasRead = 0")
    fun checkForNewMessages(senderId: String, receiverId: String): Long

    @Query("SELECT * FROM Messages WHERE senderId IN (:senderId, :receiverId) AND receiverId IN (:senderId, :receiverId)")
    fun getMessages(senderId: String, receiverId: String): LiveData<List<RoomMessage>>

}