package com.example.kent.imessage.Adapters

import android.content.Intent

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.imessage.R
import com.example.kent.imessage.RegistrationActivity
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.android.synthetic.main.layout_message_row.view.*
import android.util.SparseBooleanArray



class MessageAdapterrrr(private val selectedItem: SparseBooleanArray,val contactlist : ArrayList<ContactModel>): RecyclerView.Adapter<MessageAdapterrrr.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contacts = contactlist!![position]
        holder?.ivMessage?.setImageResource(R.drawable.twitter)
        holder!!.tvName.text = contacts.name
        holder!!.tvDescription.text = contacts.description



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_message_row,parent,false))

    override fun getItemCount(): Int = contactlist.size


    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view){
        val ivMessage = view.messageImageView!!
        val tvName = view.messageName
        val tvDescription = view.messageDescription
      /*  val container = view.messageContainer!!*/
    }
}