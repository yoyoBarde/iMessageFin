package com.example.kent.imessage.items

import android.content.Context
import com.example.kent.imessage.R
import com.example.kent.imessage.model.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.list_contact.*

class PersonItem(
        val person: User,
        val userId: String,
        private val context: Context


        ) : Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvUserName.text = person.name
        viewHolder.tvUserEmail.text = person.email
    }

    override fun getLayout() = R.layout.list_contact
}