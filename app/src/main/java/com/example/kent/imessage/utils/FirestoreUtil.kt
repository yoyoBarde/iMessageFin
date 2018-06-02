package com.example.kent.imessage.utils

import android.content.Context
import android.util.Log
import com.example.kent.imessage.ChatScreen
import com.example.kent.imessage.items.PersonItem
import com.example.kent.imessage.items.TextMessageItem
import com.example.kent.imessage.model.ChatChannel
import com.example.kent.imessage.model.Message
import com.example.kent.imessage.model.TextMessage
import com.example.kent.imessage.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference get() = firestoreInstance
            .document("Users/${FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null")}")

    private val chatChannelsCollectRef = firestoreInstance.collection("chatChannels")

    fun addNewUser(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            if (!it.exists()) {
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName!!,
                        FirebaseAuth.getInstance().currentUser?.email ?: "No email",
                        FirebaseAuth.getInstance().currentUser?.phoneNumber ?: "No contact number",
                        "",null, mutableListOf())
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    fun updateCurrentUser(name: String, email: String, contactNumber: String, imagePath: String? = null) {
        val dataMap = mutableMapOf<String, Any>()

        if (name.isNotBlank()) dataMap["name"] = name
        if (email.isNotBlank()) dataMap["email"] = email
        if (contactNumber.isNotBlank()) dataMap["contact_number"] = contactNumber
        if (imagePath != null) dataMap["image_path"] = imagePath

        currentUserDocRef.update(dataMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }
    }

    fun addUsersListener(context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return firestoreInstance.collection("Users").addSnapshotListener { it, e ->
            if (e != null) {
                logWithException("Users listener error.", e)
            }
            val items = mutableListOf<Item>()
            it?.documents?.forEach {
                if (it.id != FirebaseAuth.getInstance().currentUser?.uid)
                    items.add(PersonItem(it.toObject(User::class.java)!!, it.id, context))
            }
            onListen(items)
        }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels").document(otherUserId).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        onComplete(it["channelId"].toString())
                        return@addOnSuccessListener
                    }
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    val newChannel = chatChannelsCollectRef.document()

                    newChannel.set(ChatChannel(mutableListOf(currentUserId!!, otherUserId)))
                    currentUserDocRef.collection("engagedChatChannels").document(otherUserId)
                            .set(mapOf("channelId" to newChannel.id))
                    firestoreInstance.collection("users").document(otherUserId)
                            .collection("engagedChatChannels").document(currentUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
    }

    fun addChatMessagesListener(channelId: String, context: Context,
                                onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectRef.document(channelId).collection("messages")
                .orderBy("time")
                .addSnapshotListener { it, e ->
                    if (e != null) {
                        Log.e("FIRESTORE", "ChatMessagesListener error.", e)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    it!!.documents.forEach {
                        if (it["type"] == ChatScreen.KEY_TEXT)
                            items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                        return@forEach
                    }
                    onListen(items)
                }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectRef.document(channelId)
                .collection("messages")
                .add(message)
    }

    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }

    private fun logWithException(message: String, e: Exception) {
        Log.e("FirestoreUtil", message, e)
    }
}