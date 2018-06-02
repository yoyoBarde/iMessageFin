package com.example.kent.imessage.items

import android.annotation.TargetApi
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.example.kent.imessage.R
import com.example.kent.imessage.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent



abstract class MessageItem(private val message: Message) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        setMessageRootGravity(viewHolder)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewHolder.rootMessage.apply {
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = lParams
                viewHolder.tvSentMessage.apply {
                    this.backgroundResource = R.drawable.send_rounded_rect
                    this.textColor = resources.getColor(android.R.color.white, null)
                }
                viewHolder.tvSender.visibility = View.GONE
            }
        }
        else {
            viewHolder.rootMessage.apply {
                val lParams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = lParams
                viewHolder.tvSentMessage.apply {
                    this.backgroundResource = R.drawable.receive_rounded_rect
                    this.textColor = resources.getColor(android.R.color.black, null)
                }
                viewHolder.tvSender.visibility = View.VISIBLE
            }
        }
    }
}