package itamar.stern.arabic.ui.main.users

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.models.Dialog
import itamar.stern.arabic.models.RoomUser
const val TAG = "myLog"
class UsersViewModel(application: Application) : AndroidViewModel(application) {
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    val users = ArabicApplication.roomDB.usersDao().getAllUsers(FirebaseAuth.getInstance().currentUser!!.uid)

    init {
        readUsersFromDBIncremental()
        readDialogsFromDBIncremental()
    }

    private fun readUsersFromDBIncremental() {
        val ref = ArabicApplication.fireDBRef.child("users")
        //for the progress bar:
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                done.value = true
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = RoomUser(snapshot.child("userId").value.toString(), snapshot.child("email").value.toString(), snapshot.child("name").value.toString(), snapshot.child("phone").value.toString(), Integer.parseInt(snapshot.child("color").value.toString()))
                if (user.userId != FirebaseAuth.getInstance().currentUser?.uid) {
                    ArabicApplication.roomDB.usersDao().add(user)
                }
                //save the names of the users for save them afterwords to the dialogs:
                ArabicApplication.prefs.edit().putString("${user.userId}#name", "${user.name}").apply()

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
            }
        })
    }

    private fun readDialogsFromDBIncremental() {
        val ref = ArabicApplication.fireDBRef.child("dialogs")
        //open helping dialog for set the first listening: (when there is no child "dialog",
        //so otherwise ref won't listen until start new listening.)
        ref.child("helpingDialog")
            .setValue("helpingDialog")
        //for the progress bar:
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                done.value = true
//            }
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //when the user pressed on new user, before he send message we send helpingMessage
                //for starting the dialog listener. because of that, when he start send messages the "onChildAdded"
                //will not working, because the dialog already added when we sent the helpingMessage. so we need
                //to listen to child changing.
                val amountOfHelpMessages = 1
                //if checks if the dialog contains the logged in user, and contains messages other than the helping messages.
                if (snapshot.key.toString().contains(uid) && snapshot.childrenCount > amountOfHelpMessages){
                    //Finding the uid of the other user:
                    val elseUid = snapshot.key!!.replace(uid, "")

                    //myUserIsFirst helps my to save the users details on RoomDialog in alphabetical order.
                    val myUserIsFirst = uid < elseUid
                    val name1 = ArabicApplication.prefs.getString("${if (myUserIsFirst) uid else elseUid }#name", null)
                    val name2 = ArabicApplication.prefs.getString("${if (myUserIsFirst) elseUid else uid }#name", null)

                    //check the number of new messages in this dialog for this user:
                    val countNewMessages = ArabicApplication.roomDB.messagesDao().checkForNewMessages(elseUid, uid)

                    //set the new/previous priority - depending on whether there are new messages or not:

                    //if the dialog already exists - just update. not exists - add all the details of a new dialog.
                    // -- if not exists:
                    if(ArabicApplication.roomDB.dialogsDao().checkIfDialogExists(snapshot.key.toString()) == 0){
                        if(myUserIsFirst){
                            ArabicApplication.roomDB.dialogsDao().addDialog(Dialog(snapshot.key.toString(), uid, elseUid, name1, name2, 0, 0, numOfNewMessagesForUser1 = countNewMessages))
                        } else {
                            ArabicApplication.roomDB.dialogsDao().addDialog(Dialog(snapshot.key.toString(), elseUid, uid, name1, name2, 0, 0, numOfNewMessagesForUser2 = countNewMessages))
                        }
                    }
                    // -- if exists:
                    //I want to update the dialog in room, and change there the "hasNewMessagesForUser1/2" for my user,
                    //without changing the value of the other user.
                    //so i created two dao methods, each one changes other "hasNewMessagesForUser1/2".
                    if(myUserIsFirst){
                        ArabicApplication.roomDB.dialogsDao().updateDialogForUser1(snapshot.key.toString(), 0, countNewMessages)
                    } else {
                        ArabicApplication.roomDB.dialogsDao().updateDialogForUser2(snapshot.key.toString(), 0, countNewMessages)
                    }
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
            }
        })
    }
}