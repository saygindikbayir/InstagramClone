package com.saygindikbayir.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser!= null){
            val intent = Intent(applicationContext,TimelineActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signInClicked(view: View) {

        auth.signInWithEmailAndPassword(userEmailText.text.toString(), passwordText.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //login message
                    Toast.makeText(
                        applicationContext,
                        "Welcome back ${auth.currentUser?.email.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(applicationContext, TimelineActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                if (exception != null) {
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

    }

    fun signUpClicked(view: View) {

        val email = userEmailText.text.toString()
        val password = passwordText.text.toString()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(applicationContext, TimelineActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            if (exception != null) {
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}