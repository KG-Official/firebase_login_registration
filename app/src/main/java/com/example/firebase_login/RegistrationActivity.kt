package com.example.firebase_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.firebase_login.databinding.ActivityRegistrationBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class RegistrationActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistrationBinding
    var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val email = binding.regEmail.text.toString()
            val pass = binding.regPass.text.toString()

            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful){
                    Toast.makeText(this,"Registration Complete Succesfully",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }
            }
                .addOnFailureListener {
                    Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
                }


        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.btnGoogleLogin.setOnClickListener {
            binding.progressBar.visibility =View.VISIBLE
            googleSignInClient.signOut()
            startActivityForResult(googleSignInClient.signInIntent, 13)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let {
                firebaseAuthWithGoogle(it)

            }
        }
    }

    //google Authentication
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility =View.GONE
                if (task.isSuccessful) {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }.addOnFailureListener {
                binding.progressBar.visibility =View.GONE
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}