package itamar.stern.arabic.adapters

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Environment
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
import itamar.stern.arabic.models.AppUser
import com.bumptech.glide.request.RequestOptions
import itamar.stern.arabic.models.RoomUser
import itamar.stern.arabic.utils.ImageUtil
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.R
import android.R.attr

import android.graphics.BitmapFactory

import android.R.attr.path
import android.graphics.Color
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.collection.LLRBNode
import itamar.stern.arabic.network.NetworkStatusChecker
import kotlinx.coroutines.coroutineScope


class UsersAdapter(
    private val users: List<RoomUser>,
    val callback: (String, String, ConstraintLayout, ImageView, CardView, TextView, ImageView) -> Unit,
) : RecyclerView.Adapter<UsersAdapter.VH>() {
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
        holder.binding.textViewUserName.text = users[position].name
        callback(users[position].userId, users[position].name!!, holder.binding.userItemLayout, holder.binding.imageView, holder.binding.newMessages, holder.binding.textViewNewMessages, holder.binding.iconNewMessages)

        //load the users profile image from
        val imageRef = ArabicApplication.firebaseStorageRef.child("${users[position].userId}/image")
        try {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide
                    .with(holder.binding.root.context)
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
                            holder.binding.progressBarUserImage.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap,
                            model: Any?,
                            target: Target<Bitmap>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.binding.progressBarUserImage.visibility = View.GONE
                            ArabicApplication.bitmapUsersImages[users[position].userId] = resource
                            return false
                        }
                    })
                    .into(holder.binding.imageView)
            }.addOnFailureListener {
                holder.binding.progressBarUserImage.visibility = View.GONE
            }
        } catch (e: Exception) {

        }


    }

    override fun getItemCount() = users.size

}