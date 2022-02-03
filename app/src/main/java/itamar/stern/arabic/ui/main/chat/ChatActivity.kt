package itamar.stern.arabic.ui.main.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.adapters.MessagesAdapter
import itamar.stern.arabic.databinding.ActivityChatBinding
import itamar.stern.arabic.models.HelpingMessage
import itamar.stern.arabic.models.Message
import itamar.stern.arabic.utils.hideKeyboard
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: ActivityChatBinding
    private val messageField get() = binding.content.editTextMessage.text

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val elseUid = ArabicApplication.currentChatUserId
        binding.content.buttonSend.setOnClickListener {
            hideKeyboard(this)
            //don't send empty comment:
            if (messageField.toString() == "") return@setOnClickListener
            val day: Int
            val month: Int
            val year: Int
            val time: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                day = LocalDate.now().dayOfMonth
                month = LocalDate.now().monthValue
                year = LocalDate.now().year
                time =
                    LocalTime.now().hour.toString() + ":" + (if (LocalTime.now().minute.toString().length == 1) "0" else "") + LocalTime.now().minute.toString()
            } else {
                day = Calendar.getInstance().time.date
                month = Calendar.getInstance().time.month + 1
                year = Calendar.getInstance().time.year + 1900
                val minutes = Calendar.getInstance().time.minutes
                time =
                    "${Calendar.getInstance().time.hours}:${if (minutes > 9) minutes else "0$minutes"}"
            }
            val dialogKey = if (uid!! < elseUid) "$uid$elseUid" else "$elseUid$uid"
            ArabicApplication.fireDBRef.child("dialogs")
                .child(dialogKey)
                .push()
                .setValue(
                    Message(
                        System.currentTimeMillis(),
                        uid,
                        elseUid,
                        messageField.toString(),
                        "${day}/${month}/${year}",
                        time
                    )
                )
                .addOnSuccessListener {
                    messageField.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Something went wrong, try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        //zero the number of unread messages - for remove the yellow alert of new messages in the dialogs fragment:
        //todo: remove redundant uid and elseuid assignments:
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val elseUid = ArabicApplication.currentChatUserId
        val dialogKey = if (uid!! < elseUid) "$uid$elseUid" else "$elseUid$uid"
        ArabicApplication.roomDB.dialogsDao().setReadDialog(dialogKey, uid)


        val currentUserId = ArabicApplication.currentChatUserId
        val currentUserName = ArabicApplication.currentChatUserName
        binding.textViewCurrentChatTitle.text = currentUserName
        val bitmap = ArabicApplication.bitmapUsersImages[currentUserId]
        if (bitmap != null) {
            binding.imageViewCurrentChatImage.setImageBitmap(bitmap)
        }


        binding.content.recyclerViewMessages.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        viewModel.messages.observe(this) {
            binding.content.recyclerViewMessages.adapter = MessagesAdapter(it)
            binding.content.recyclerViewMessages.scrollToPosition(it.size - 1)
        }

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        //update that the user read the new messages
        ArabicApplication.roomDB.messagesDao().setWasRead(
            ArabicApplication.currentChatUserId,
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        super.onBackPressed()
    }
}