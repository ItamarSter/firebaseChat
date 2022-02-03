package itamar.stern.arabic.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import itamar.stern.arabic.models.Dialog
import itamar.stern.arabic.models.RoomMessage
import itamar.stern.arabic.models.RoomUser
import itamar.stern.arabic.roomDatabase.dao.DialogsDao
import itamar.stern.arabic.roomDatabase.dao.MessagesDao
import itamar.stern.arabic.roomDatabase.dao.UsersDao


const val DB_VERSION = 3
const val DB_NAME = "ArabicDatabase"

@Database(entities = [RoomUser::class, RoomMessage::class, Dialog::class], version = DB_VERSION)
abstract class ArabicRoomDB : RoomDatabase() {
    companion object {
        fun create(context: Context): ArabicRoomDB {
            return Room.databaseBuilder(context, ArabicRoomDB::class.java, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    abstract fun messagesDao(): MessagesDao
    abstract fun usersDao(): UsersDao
    abstract fun dialogsDao(): DialogsDao
}