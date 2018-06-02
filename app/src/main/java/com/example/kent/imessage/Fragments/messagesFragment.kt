package com.example.kent.imessage.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.imessage.ChatScreen
import com.example.kent.imessage.MainActivity
import com.example.kent.imessage.R
import com.example.kent.imessage.items.PersonItem
import com.example.kent.imessage.utils.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_messages.*
import org.jetbrains.anko.support.v4.startActivity

class messagesFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private lateinit var userSection: Section
    private var shouldInitRecyclerView = true

    companion object {
        fun newInstance() : messagesFragment = messagesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        userListenerRegistration = FirestoreUtil.addUsersListener(activity!!, this::updateRecyclerView)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {
        fun init() {
            messagerecycelrview.apply {
                layoutManager = LinearLayoutManager(this@messagesFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    userSection = Section(items)
                    add(userSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = userSection.update(items)

        when (shouldInitRecyclerView) {
            true -> init()
            else ->updateItems()
        }
    }

    private val onItemClick = OnItemClickListener { item, _ ->
        if (item is PersonItem) {
            startActivity<ChatScreen>(
                    MainActivity.KEY_USER_NAME to item.person.name, MainActivity.KEY_USER_ID to item.userId)
        }
    }
}