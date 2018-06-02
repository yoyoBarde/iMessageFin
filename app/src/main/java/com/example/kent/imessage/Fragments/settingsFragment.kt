package com.example.kent.imessage.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kent.imessage.MainActivity
import com.example.kent.imessage.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_settings.*


class settingsFragment : Fragment() {

   /* private val signoutt: FirebaseAuth = FirebaseAuth.getInstance()*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the toolbar for this fragment

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        signout.setOnClickListener{

            FirebaseAuth.getInstance().signOut()

            val intent = Intent (this.activity, MainActivity::class.java)
            startActivity(intent)
        }
    }


}
