package itamar.stern.arabic.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import itamar.stern.arabic.ui.main.users.UsersFragment
import itamar.stern.arabic.ui.main.my_chats.MyChatsFragment
import itamar.stern.arabic.ui.main.words.WordsFragment

class SectionsPagerAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                UsersFragment()
            }
            1 -> {
                MyChatsFragment()
            }
            2 -> {
                WordsFragment()
            }
            else -> throw IllegalArgumentException("No such fragment")
        }
    }
}