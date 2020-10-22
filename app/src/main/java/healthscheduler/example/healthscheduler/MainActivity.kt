package healthscheduler.example.healthscheduler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var mGoogleSignInClient : GoogleSignInClient? = null

    companion object {
        val TAG = "MainActivity"
        private const val REQUEST_CODE_SIGN_IN = 9001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        val buttonContinueWithEmailMain = findViewById<Button>(R.id.buttonContinueWithEmailMain)
        val imageViewRegistarMain = findViewById<ImageView>(R.id.imageViewRegistarMain)
        val buttonGoogle = findViewById<ImageView>(R.id.imageViewGoogleMain)

        buttonContinueWithEmailMain.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        imageViewRegistarMain.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, options)

        buttonGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Log.w("", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("", "loginSuccess!")
                    val user = auth.currentUser
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                } else {
                    Log.w("", "loginFailed! Info = ", task.exception)

                    Toast.makeText(baseContext, "Falha ao entrar na conta.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}