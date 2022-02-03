package itamar.stern.arabic.ui.main.users

import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.adapters.UsersAdapter
import itamar.stern.arabic.databinding.UsersFragmentBinding
import itamar.stern.arabic.ui.main.chat.ChatActivity
import android.widget.RelativeLayout
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.arabic.utils.dp


class UsersFragment : Fragment() {

    private var _binding: UsersFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        }
        _binding = UsersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewUsers.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        if (FirebaseAuth.getInstance().currentUser != null) {
            viewModel.users.observe(viewLifecycleOwner) {
                binding.recyclerViewUsers.adapter = UsersAdapter(it) { userId, userName, layout, imageView, newMessagesView, textViewNewMessages, iconNewMessages ->
                    layout.setOnClickListener {
                        ArabicApplication.currentChatUserId = userId
                        ArabicApplication.currentChatUserName = userName
                        startActivity(Intent(requireActivity(), ChatActivity::class.java))
                        newMessagesView.visibility = View.INVISIBLE
                        textViewNewMessages.visibility = View.INVISIBLE
                        iconNewMessages.visibility = View.INVISIBLE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}