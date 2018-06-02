package com.example.kent.imessage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.kent.imessage.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.registration.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

class RegistrationActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {

    val TAG="REGISTRATION"

    private var mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    private var mDb: FirebaseFirestore= FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.registration)
        btnCreateAccount.isEnabled=false
        tvShowHide1.visibility = View.GONE; tvShowHide2.visibility = View.GONE
        etPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT

        etPassword.addTextChangedListener(this); etConfirmPassword.addTextChangedListener(this)

        tvShowHide1.setOnClickListener(this); tvShowHide2.setOnClickListener(this)



        btnCreateAccount.setOnClickListener {

            val userName = etUsername.text.toString().trim()
            val email = etEmailAddress.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val user = User( userName,email,password,phoneNumber,null, mutableListOf())

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    mDb.collection("Users").document(mAuth.currentUser!!.uid).set(user)
                    mDb.collection("LoginValidation").document(email!!).set(user)
                    val intent = Intent (this, MainActivity::class.java)
                    startActivity(intent)

                    toast("Account Created")

                } else

                    toast("Email already exist or Failed")

            }

            }

        etEmailAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(mEdit: Editable) {
             if(!isValidEmail(etEmailAddress.text.toString()))
                 etEmailAddress.error = "Email not valid"
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })


        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(mEdit: Editable) {
                if(etPassword.text.toString().length<6)
                    etPassword.error = "Not strong enough"
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(mEdit: Editable) {
             if(etPassword.text.toString()!=etConfirmPassword.text.toString())
                 etConfirmPassword.error = "Password doesn't match"
                else
                 btnCreateAccount.isEnabled=true
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })



        }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        displayShowHide(etPassword, tvShowHide1)
        displayShowHide(etConfirmPassword, tvShowHide2)
    }

    private fun displayShowHide(editText: EditText?, textView: TextView?) {
        if (editText?.text!!.isNotEmpty()) textView?.visibility = View.VISIBLE
        else textView?.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.tvShowHide1 -> toggleShowHide(etPassword, tvShowHide1)

            else -> toggleShowHide(etConfirmPassword, tvShowHide2)
        }
    }

    private fun toggleShowHide(editText: EditText?, textView: TextView?) {
        if (textView?.text =="SHOW") {
            textView?.text = getString(R.string.tv_show2)
            editText?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            editText?.setSelection(etPassword.length())
            textView.text="HIDE"
            Log.d(TAG,"Show")
        }
        else {
            textView?.text = getString(R.string.tv_show)
            editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            editText?.setSelection(etPassword.length())
            textView?.text="SHOW"
            Log.d(TAG,"hide")
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) false


        else android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()

    }
}





