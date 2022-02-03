package itamar.stern.arabic.ui.settings

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.databinding.ActivitySettingsBinding
import java.io.File
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val ref = ArabicApplication.firebaseStorageRef
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { file ->
                binding.content.imageViewUserSettings.setImageURI(uri)
                val myImageRef = ref.child("${FirebaseAuth.getInstance().currentUser!!.uid}/image")
                ArabicApplication.prefs.edit().putString(
                    "myImageUriString#${FirebaseAuth.getInstance().currentUser!!.uid}",
                    uri.toString()
                ).apply()
                myImageRef.putFile(file)
            }
        }

    override fun onResume() {
        super.onResume()
        val imageUriString = ArabicApplication.prefs.getString(
            "myImageUriString#${FirebaseAuth.getInstance().currentUser!!.uid}",
            null
        )
        if (imageUriString != null) {
            Glide
                .with(this)
                .load(Uri.parse(imageUriString))
                .thumbnail(0.1f)
                .into(binding.content.imageViewUserSettings)
        }
        binding.content.buttonChangeImage.setOnClickListener {
            selectImageFromGalleryResult.launch("image/*")
        }
    }

}