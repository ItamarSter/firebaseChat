package itamar.stern.arabic

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import itamar.stern.arabic.ui.main.SectionsPagerAdapter
import itamar.stern.arabic.databinding.ActivityMainBinding
import itamar.stern.arabic.ui.auth.AuthActivity
import itamar.stern.arabic.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        val tabsNames = arrayOf("משתמשים", "שיחות", "רשימת מילים")
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = tabsNames[position]
        }.attach()
    }

    override fun onResume() {
        super.onResume()
        //Check if the user signed up but didn't verify the mail:
//        if (FirebaseAuth.getInstance().currentUser != null && !FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
//            FirebaseAuth.getInstance().signOut()
//            Toast.makeText(this, "נא ללחוץ על הקישור שנשלח אליך במייל ולהתחבר מחדש", Toast.LENGTH_LONG).show()
//        }
        //Send the user to login if he is not logged in:
        FirebaseAuth.getInstance().addAuthStateListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val itemSearch = menu.findItem(R.id.action_search)
        val searchView = itemSearch.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                return true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return false
    }
}