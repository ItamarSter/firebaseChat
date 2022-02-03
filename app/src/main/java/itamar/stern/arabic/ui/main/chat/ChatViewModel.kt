package itamar.stern.arabic.ui.main.chat



import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.models.HelpingMessage
import itamar.stern.arabic.models.RoomMessage

const val TAG = "myLog"

class ChatViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val elseUid = ArabicApplication.currentChatUserId

    val messages = ArabicApplication.roomDB.messagesDao().getMessages(uid, elseUid)


    init {
        readFromDBIncremental()
    }

    private fun readFromDBIncremental() {
        val ref = ArabicApplication.fireDBRef.child("dialogs").child(if (uid < elseUid) "$uid$elseUid" else "$elseUid$uid")
        //send the helping messages:
        //The use of "updateChildren" is designed to prevent unwanted change of value "hasNewMessages":
        ref.child("helpingMessage")
            .setValue(HelpingMessage(
                System.currentTimeMillis(),
                "helping message for start to listen to changes"
            ))

        //Save all messages of this dialog from firebase in room, and set listener to added messages:
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //don't save the helpingMessages:
                if(snapshot.key.toString() != "helpingMessage"){
                    val message = RoomMessage(
                        snapshot.child("timestamp").value as Long,
                        snapshot.child("senderId").value.toString(),
                        snapshot.child("receiverId").value.toString(),
                        snapshot.child("message").value.toString(),
                        snapshot.child("date").value.toString(),
                        snapshot.child("time").value.toString()
                    )
                    ArabicApplication.roomDB.messagesDao().addMessage(message)
                }
                //להשתמש ב mapnutnull
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
            }
        })


    }

}