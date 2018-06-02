package com.example.kent.imessage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var callbackmanager: CallbackManager?=null
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Twitter.initialize(this)
        setContentView(R.layout.activity_main)
        callbackmanager = CallbackManager.Factory.create()
        btnSignin.isEnabled=false

        loginfacebook.setReadPermissions("email" , "public_profile")
        loginfacebook.registerCallback(callbackmanager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
                Toast.makeText(this@MainActivity,"Successfully logged in!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, HomescreenActivity::class.java)
                startActivity(intent)
            }

            override fun onCancel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onError(error: FacebookException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        btnSignin.setOnClickListener{


                        startActivity(Intent(this,HomescreenActivity::class.java))


        }



        createAccount.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mAuth = FirebaseAuth.getInstance()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        twitterlogin.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>?) {
                Log.d(TAG, "Login success.")
                handleTwitterSession(result?.data)
                progressbar.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity,"Successfully logged in!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, HomescreenActivity::class.java)
                startActivity(intent)
                progressbar.visibility = View.INVISIBLE
            }

            override fun failure(exception: TwitterException?) {
                Log.d(TAG, "Login failure.", exception)
                updateUI(null, null)
            }
        }


        googlesignin.setOnClickListener {
            when (it.id) {
                R.id.googlesignin -> signIn()
            }
        }
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(mEdit: Editable) {
                if (etEmail.text.toString().isNotEmpty())
                    checkIfemailExist()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(mEdit: Editable) {
                    if(etPassword.text.toString().isNotEmpty())
                        checkPassword()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.getToken())
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { p0 ->
                    if (p0.isSuccessful) {
                        val user = mAuth!!.currentUser
                        updateUI(user, null)
                    } else {
                        Toast.makeText(this@MainActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null,null)
                    }
                }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        progressbar.visibility = View.VISIBLE
        Toast.makeText(this@MainActivity,"Successfully logged in!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@MainActivity, HomescreenActivity::class.java)
        startActivity(intent)
        progressbar.visibility = View.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            twitterlogin.onActivityResult(requestCode, resultCode, data)
        }
        else
            callbackmanager?.onActivityResult(requestCode, resultCode, data)

    }


    private fun handleTwitterSession(session: TwitterSession?) {
        Log.d(TAG, "handleTwitterSession: $session")
        val credential =  TwitterAuthProvider.getCredential(session!!.authToken.token, session.authToken.secret)

        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Log.d(TAG, "signInWithCredential: success")
                val user = mAuth?.currentUser
                updateUI(user, null)
                toast("Authentication succeeded.")
            }
            else {
                Log.d(TAG, "signInWithCredential: failure", it.exception)
                toast("Authentication failed.")
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.d(TAG, "signInResult: failed code = ${e.statusCode}")
            updateUI(null, null)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle: ${account?.id}")
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Log.d(TAG, "signInWithCredential: success")
                val user = mAuth?.currentUser
                updateUI(user, null)
            }
            else {
                Log.d(TAG, "signInWithCredential: failure", it.exception)
                Toast.makeText(this@MainActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                updateUI(null, null)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?, account: GoogleSignInAccount?) {}

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    companion object {

        private const val RC_SIGN_IN = 100
        const val KEY_USER_ID = "kent"
        const val KEY_USER_NAME = "gimenez"
    }

    private fun checkPassword(){
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        var documentReference: DocumentReference = db.collection("LoginValidation").document(etEmail.text.toString())
        documentReference.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
            if (task.isSuccessful) {

                val document = task.result

                if (document.getString("password")==etPassword.text.toString()) {
                    image_checked_password.visibility=View.VISIBLE
                    Log.d(TAG, "signIN Success")
                    btnSignin.isEnabled=true
                    btnSignin.setBackgroundResource(R.drawable.button_state)

                } else {
                    image_checked_password.visibility=View.INVISIBLE
                    Log.d(TAG, "signIn Failed")
                    btnSignin.isEnabled=false
                    btnSignin.setBackgroundResource(R.drawable.button_disabled)
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        })
    }
    private fun checkIfemailExist(){
        var db: FirebaseFirestore = FirebaseFirestore.getInstance()
        var documentReference: DocumentReference = db.collection("LoginValidation").document(etEmail.text.toString())
        documentReference.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
            if (task.isSuccessful) {

                val document = task.result

                if (document.exists()) {
                    Log.d(TAG, "documenet exist"+document.getString("email"))
                    image_checked_email.visibility=View.VISIBLE

                } else {
                    btnSignin.isEnabled=false

                    btnSignin.setBackgroundResource(R.drawable.button_disabled)
                    image_checked_email.visibility=View.INVISIBLE
                    Log.d(TAG, "No such document")
                }
            } else {
                Log.d(TAG, "get failed with ", task.exception)
            }
        })
    }
}