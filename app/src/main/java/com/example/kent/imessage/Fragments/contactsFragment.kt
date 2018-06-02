package com.example.kent.imessage.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.imessage.Adapters.ContactModel
import com.example.kent.imessage.R
import com.example.kent.imessage.utils.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_contact.*


/**
 * A simple [Fragment] subclass.
 */
class contactsFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration
    private lateinit var userSection: Section
    private var shouldInitRecyclerView = true

    companion object {
        fun newInstance(): contactsFragment = contactsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        userListenerRegistration = FirestoreUtil.addUsersListener(activity!!, this::updateRecyclerView)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<Item>) {
        fun init() {
            recyclerView_people.apply {
                layoutManager = LinearLayoutManager(this@contactsFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    userSection = Section(items)
                    add(userSection)
                    // setOnItemClickListener(onItemClick)
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

}// Required empty public constructor
