package itamar.stern.arabic

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import itamar.stern.arabic.models.AppUser
import itamar.stern.arabic.models.RoomUser
import itamar.stern.arabic.roomDatabase.ArabicRoomDB


class ArabicApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        lateinit var instance: ArabicApplication

        val prefs: SharedPreferences by lazy {
            instance.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }
        val fireDBRef: DatabaseReference by lazy {
            FirebaseDatabase.getInstance().reference
        }
        val firebaseStorageRef by lazy {
            Firebase.storage.reference
        }
        val roomDB: ArabicRoomDB by lazy {
            ArabicRoomDB.create(instance)
        }
        lateinit var currentChatUserId: String
        lateinit var currentChatUserName: String

        val bitmapUsersImages = mutableMapOf<String, Bitmap>()
    }
}