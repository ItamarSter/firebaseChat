package itamar.stern.arabic.ui.main.my_chats

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.models.Dialog

const val TAG = "myLog"
class MyChatsViewModel : ViewModel() {
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    val dialogs = ArabicApplication.roomDB.dialogsDao().getMyDialogs(uid)
    init {

    }


}