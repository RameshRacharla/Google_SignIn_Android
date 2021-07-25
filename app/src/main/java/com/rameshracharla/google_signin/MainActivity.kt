package com.rameshracharla.google_signin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount
import com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN = 0
    private var mGoogleSignInClient: GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Configure sign-in to request the user's ID, email address, and basic profile.
          ID and basic profile are included in DEFAULT_SIGN_IN.*/

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // For Google Standard Button - Size
        sign_in_button.setSize(SignInButton.SIZE_STANDARD);

        sign_in_button.setOnClickListener {
            signIn()
        }

        tv_sign_out.setOnClickListener {
            signout()
        }
    }

    private fun signout() {
        mGoogleSignInClient?.signOut()
            ?.addOnCompleteListener(this) {
                Toast.makeText(this@MainActivity, "Signed Out", Toast.LENGTH_LONG).show()
                profile_layout.visibility = View.GONE
            }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            val task = getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            tv_name.text = account.displayName
            tv_email.text = account.email
            tv_id.text = account.id
            Picasso.get()
                .load(account.photoUrl)
                .into(iv_profile)
            profile_layout.visibility = View.VISIBLE
        } else {
            profile_layout.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        /* Check for existing Google Sign In account, if the user is already signed in the GoogleSignInAccount will be non-null.*/
        val account = getLastSignedInAccount(this)
        updateUI(account)
    }
}

