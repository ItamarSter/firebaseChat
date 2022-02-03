package itamar.stern.arabic.adapters

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.databinding.UserItemBinding
import com.bumptech.glide.request.RequestOptions
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.arabic.models.Dialog

const val TAG = "myLog"
class DialogsAdapter(
    private val dialogs: List<Dialog>,
    val callback: (String, String, ConstraintLayout, ImageView, CardView, TextView, ImageView) -> Unit,
) : RecyclerView.Adapter<DialogsAdapter.VH>() {
    class VH(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val userId: String
        val userName: String
        //find the else uid and name:
        if(FirebaseAuth.getInstance().currentUser!!.uid == dialogs[position].user1Id){
            userId = dialogs[position].user2Id!!
            userName = dialogs[position].user2name!!
        } else {
            userId = dialogs[position].user1Id!!
            userName = dialogs[position].user1name!!
        }
        with(holder.binding){
            textViewUserName.text = userName
            callback(userId, userName, userItemLayout, imageView, newMessages, textViewNewMessages, iconNewMessages)
            //show yellow alert on dialog when has new messages:
            //val countNewMessages = ArabicApplication.roomDB.messagesDao().checkForNewMessages(userId, FirebaseAuth.getInstance().currentUser!!.uid)
            val countNewMessages = if(FirebaseAuth.getInstance().currentUser!!.uid == dialogs[position].user1Id){
                dialogs[position].numOfNewMessagesForUser1!!
            } else {
                dialogs[position].numOfNewMessagesForUser2!!
            }
            if(countNewMessages > 0){
                newMessages.visibility = View.VISIBLE
                textViewNewMessages.visibility = View.VISIBLE
                iconNewMessages.visibility = View.VISIBLE
                textViewNewMessages.text = "$countNewMessages"
            }
            //load the users profile image from
            val imageRef = ArabicApplication.firebaseStorageRef.child("${userId}/image")
            try {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide
                        .with(root.context)
                        .asBitmap()
                        .load(uri)
                        .apply(
                            RequestOptions()
                                .fitCenter()
                                .format(DecodeFormat.PREFER_ARGB_8888)
                                .override(Target.SIZE_ORIGINAL)
                        )
                        .listener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBarUserImage.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBarUserImage.visibility = View.GONE
                                ArabicApplication.bitmapUsersImages[userId] = resource
                                return false
                            }
                        })
                        .into(imageView)
                }.addOnFailureListener {
                    progressBarUserImage.visibility = View.GONE
                }
            } catch (e: Exception) {

            }
        }



    }

    override fun getItemCount() = dialogs.size

}