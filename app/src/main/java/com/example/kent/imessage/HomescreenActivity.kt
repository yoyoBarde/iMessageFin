package com.example.kent.imessage

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.kent.imessage.Adapters.ContactModel
import com.example.kent.imessage.Fragments.contactsFragment
import com.example.kent.imessage.Fragments.messagesFragment
import com.example.kent.imessage.Fragments.profileFragment
import com.example.kent.imessage.Fragments.settingsFragment
import kotlinx.android.synthetic.main.activity_homescreen.*

class HomescreenActivity : AppCompatActivity() {

    private val contactlist = ArrayList<ContactModel>()

    lateinit var choosenfragment: Fragment
    private val manager=supportFragmentManager
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {


        item ->

        when (item.itemId) {
            R.id.navigation_contacts -> {
                choosenfragment= contactsFragment()
                addFragment(choosenfragment)
                toolbar.title = "Contacts"
                toolbar.setTitleTextColor(Color.WHITE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_messages -> {
                choosenfragment= messagesFragment()
                addFragment(choosenfragment)
                toolbar.title = "Messages"
                toolbar.setTitleTextColor(Color.WHITE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profle -> {
                choosenfragment= profileFragment()
                addFragment(choosenfragment)
                toolbar.title = "Profile"
                toolbar.setTitleTextColor(Color.WHITE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                choosenfragment= settingsFragment()
                addFragment(choosenfragment)
                toolbar.title = "Settings"
                toolbar.setTitleTextColor(Color.WHITE)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

  /*      val mAdapter = MessageAdapterrrr(contactlist)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter
*/



        setContentView(R.layout.activity_homescreen)
        choosenfragment = contactsFragment()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        bottomnavigationview.disableShiftMode(navigation)




    }
    private fun addFragment(fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragmentContainer,fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}
