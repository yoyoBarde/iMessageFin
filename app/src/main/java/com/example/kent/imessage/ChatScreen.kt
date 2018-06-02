package com.example.kent.imessage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.example.kent.imessage.Adapters.ChatMessage
import com.example.kent.imessage.Adapters.ChatModel
import com.example.kent.imessage.model.TextMessage
import com.example.kent.imessage.model.User
import com.example.kent.imessage.utils.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_screen.*
import java.util.*


class ChatScreen : AppCompatActivity() {

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private lateinit var messagesSection: Section
    private var shouldInitRecyclerView = true

    companion object {
        const val KEY_TEXT = "text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = intent.getStringExtra(MainActivity.KEY_USER_NAME)

        FirestoreUtil.getCurrentUser { currentUser = it }

        otherUserId = intent.getStringExtra(MainActivity.KEY_USER_ID)

        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId
            messagesListenerRegistration = FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            ibSendMessage.setOnClickListener {
                val messageToSend = TextMessage(etTextMessage.text.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid, otherUserId, currentUser.contactNumber)
                if (etTextMessage.text.toString().isNotEmpty()) {
                    etTextMessage.setText("")
                    FirestoreUtil.sendMessage(messageToSend, channelId)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> { finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            rvChat.apply {
                layoutManager = LinearLayoutManager(this@ChatScreen)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        when (shouldInitRecyclerView) {
            true -> init()
            else ->updateItems()

        }

        rvChat.scrollToPosition(rvChat.adapter.itemCount - 1)
    }


}









