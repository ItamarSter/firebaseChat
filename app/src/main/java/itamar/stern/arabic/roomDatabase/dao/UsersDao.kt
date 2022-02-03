package itamar.stern.arabic.roomDatabase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import itamar.stern.arabic.models.RoomUser


@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(user: RoomUser)

    @Update
    fun update(user: RoomUser)

//    @Query("REPLACE INTO Users (userId, email, name, phone, color) VALUES (:userId, :email, :name, :phone, :color)")
//    fun update(userId: String?, email: String?, name: String?, phone: String?, color: Int?)

    @Query("SELECT * FROM Users WHERE NOT userId=:currentUserId")
    fun getAllUsers(currentUserId: String): LiveData<List<RoomUser>>

}