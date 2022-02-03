package itamar.stern.arabic.ui.main.my_chats

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.adapters.DialogsAdapter
import itamar.stern.arabic.databinding.MyChatsFragmentBinding
import itamar.stern.arabic.ui.main.chat.ChatActivity
import itamar.stern.arabic.utils.dp

class MyChatsFragment : Fragment() {
    private lateinit var viewModel: MyChatsViewModel
    private var _binding: MyChatsFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel = ViewModelProvider(this).get(MyChatsViewModel::class.java)
        }
        _binding = MyChatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewWords.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel.dialogs.observe(viewLifecycleOwner) {
                binding.recyclerViewWords.adapter = DialogsAdapter(it) { userId, userName, layout, imageView, newMessagesView, textViewNewMessages, iconNewMessages ->
                    layout.setOnClickListener {
                        ArabicApplication.currentChatUserId = userId
                        ArabicApplication.currentChatUserName = userName
                        newMessagesView.visibility = View.INVISIBLE
                        textViewNewMessages.visibility = View.INVISIBLE
                        iconNewMessages.visibility = View.INVISIBLE
                        startActivity(Intent(requireActivity(), ChatActivity::class.java))
                    }
                    imageView.setOnClickListener {
                        showImage(userId)
                    }
                }
            }
        }
    }

    private fun showImage(userId: String) {
        val builder = Dialog(requireContext())
        //builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        val imageView = ImageView(requireContext())
        imageView.setImageBitmap(ArabicApplication.bitmapUsersImages[userId])
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        builder.addContentView(
            imageView,
            RelativeLayout.LayoutParams(300.dp().toInt(), 300.dp().toInt())
        )
        builder.show()
    }
}