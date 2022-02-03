package itamar.stern.arabic.roomDatabase.dao



import androidx.lifecycle.LiveData
import androidx.room.*
import itamar.stern.arabic.models.Dialog
import itamar.stern.arabic.models.RoomMessage
import itamar.stern.arabic.models.RoomUser
import java.sql.Timestamp


@Dao
interface DialogsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDialog(dialog: Dialog)

    @Query("SELECT COUNT(1) FROM Dialogs WHERE dialogKey=:dialogKey")
    fun checkIfDialogExists(dialogKey: String): Int

    @Query("UPDATE Dialogs SET priorityUser1=:priority, numOfNewMessagesForUser1=:numOfNewMessagesForUser1 WHERE dialogKey=:dialogKey")
    fun updateDialogForUser1(dialogKey: String, priority: Long, numOfNewMessagesForUser1: Long)

    @Query("UPDATE Dialogs SET priorityUser2=:priority, numOfNewMessagesForUser2=:numOfNewMessagesForUser1 WHERE dialogKey=:dialogKey")
    fun updateDialogForUser2(dialogKey: String, priority: Long, numOfNewMessagesForUser1: Long)

    @Query("SELECT * FROM Dialogs WHERE user1Id=:currentUserId OR user2Id=:currentUserId ORDER BY CASE WHEN user1Id=:currentUserId THEN priorityUser1 ELSE priorityUser2 END DESC")
    fun getMyDialogs(currentUserId: String): LiveData<List<Dialog>>

    @Query("SELECT MAX(CASE WHEN user1Id=:currentUserId THEN priorityUser1 ELSE priorityUser2 END) FROM Dialogs WHERE user1Id=:currentUserId OR user2Id=:currentUserId")
    fun findHighestPriority(currentUserId: String): Long

    @Query("SELECT CASE WHEN user1Id=:currentUserId THEN priorityUser1 ELSE priorityUser2 END FROM Dialogs WHERE dialogKey=:dialogKey")
    fun getDialogPriority(dialogKey: String, currentUserId: String): Long

    @Query("UPDATE Dialogs SET numOfNewMessagesForUser1 = CASE WHEN user1Id=:currentUserId THEN 0 ELSE numOfNewMessagesForUser1 END, numOfNewMessagesForUser2 = CASE WHEN user2Id=:currentUserId THEN 0 ELSE numOfNewMessagesForUser2 END WHERE dialogKey=:dialogKey")
    fun setReadDialog(dialogKey: String, currentUserId: String)

//    @Query("UPDATE Dialogs SET priority=:priority WHERE dialogKey=:dialogKey")
//    fun moveDialogUp(dialogKey: String, priority: Long)
//





//    @Query("UPDATE Messages SET wasRead = 1 WHERE senderId=:senderId AND receiverId=:receiverId")
//    fun setWasRead(senderId: String, receiverId: String)
//
//    @Query("SELECT COUNT(*) FROM Messages WHERE senderId=:senderId AND receiverId=:receiverId AND wasRead = 0")
//    fun checkForNewMessages(senderId: String, receiverId: String): Int
//
//    @Query("SELECT * FROM Messages WHERE senderId IN (:senderId, :receiverId) AND receiverId IN (:senderId, :receiverId)")
//    fun getMessages(senderId: String, receiverId: String): LiveData<List<RoomMessage>>

}