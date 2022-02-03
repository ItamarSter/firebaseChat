package itamar.stern.arabic.ui.auth


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import itamar.stern.arabic.ArabicApplication
import itamar.stern.arabic.MainActivity
import itamar.stern.arabic.databinding.ActivityAuthBinding
import itamar.stern.arabic.models.AppUser
import itamar.stern.arabic.utils.hideKeyboard
import itamar.stern.arabic.utils.isEmailValid


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val email get() = binding.editTextEmail.text.toString()
    private val name get() = binding.editTextName.text.toString()
    private val password get() = binding.editTextPassword.text.toString()
    private val password2 get() = binding.editTextPassword2.text.toString()
    private val phone get() = binding.editTextPhone.text.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener {
            register()
        }
        binding.buttonLogin.setOnClickListener {
            login()
        }
        binding.buttonVerified.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.progressBarVerified.visibility = View.VISIBLE
            //Check if the user verified the mail:
            FirebaseAuth.getInstance().currentUser?.reload()?.addOnSuccessListener {
               // if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                    ArabicApplication.prefs.edit().putString(email, email).apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
//                } else {
//                    Toast.makeText(
//                        this,
//                        "יש ללחוץ על הקישור שנשלח אליך במייל, ואז על אישור",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    binding.buttonVerified.visibility = View.VISIBLE
//                    binding.progressBarVerified.visibility = View.INVISIBLE
//                }
            }
        }

        binding.textViewSendMailAgain.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            Toast.makeText(this, "קישור חדש נשלח", Toast.LENGTH_SHORT).show()
        }

        binding.textViewGoToRegister.setOnClickListener {
            registerViewsMode()
        }

        binding.textViewGoToLogin.setOnClickListener {
            loginViewsMode()
        }
    }

    private fun register() {
        hideKeyboard(this)
        if (!isValidDetailsRegister()) {
            return
        }
        registerProgressViewsMode()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccessRegister)
            .addOnFailureListener(onFailureRegister)
    }

    private val onSuccessRegister = OnSuccessListener<AuthResult> {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                fromRegisterToVerifyViewsMode()
            }
        //Save in firebase the details of the new user:
        ArabicApplication.fireDBRef.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(
            AppUser(
                FirebaseAuth.getInstance().currentUser!!.uid,
                email,
                name,
                phone,
                Color.rgb(
                    (Math.random() * 256).toInt(),
                    (Math.random() * 256).toInt(),
                    (Math.random() * 256).toInt()
                )
            )
        )
    }

    private val onFailureRegister = OnFailureListener {
        registerFailedViewsMode()
        showSnackbar(it.localizedMessage)
    }

    private fun login() {
        hideKeyboard(this)
        if (!isValidDetailsLogin()) {
            return
        }
        loginProgressViewsMode()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(onSuccessLogin)
            .addOnFailureListener(onFailureLogin)
    }



    private val onSuccessLogin = OnSuccessListener<AuthResult> {
        //Check if the mail hed been verified:
        if (ArabicApplication.prefs.getString(email, null) != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            fromLoginToVerifyViewsMode()
        }
    }
    private val onFailureLogin = OnFailureListener {
        loginFailedViewsMode()
        showSnackbar(it.localizedMessage)
    }

    private fun isValidDetailsLogin(): Boolean {
        return when {
            !checkEmail() -> {
                showSnackbar("Email not valid")
                false
            }
            !checkPassword() -> {
                showSnackbar("Password not valid")
                false
            }
            else -> true
        }
    }

    private fun isValidDetailsRegister(): Boolean {
        return when {
            !checkEmail() -> {
                showSnackbar("Email not valid")
                false
            }
            !checkPassword() -> {
                showSnackbar("Password must be at least 6 letters")
                false
            }
            !checkName() -> {
                showSnackbar("Name must be at least two letters")
                false
            }
            !checkPhone() -> {
                showSnackbar("Phone must be 10 letters")
                false
            }
            !equalsPassword() -> {
                showSnackbar("Passwords not same")
                false
            }
            else -> true
        }
    }

    private fun checkEmail(): Boolean = email.isEmailValid()
    private fun checkPassword(): Boolean = password.length >= 6
    private fun checkName(): Boolean = name.length >= 2
    private fun checkPhone(): Boolean = phone.length == 10
    private fun equalsPassword(): Boolean = password == password2

    private fun showSnackbar(message: CharSequence) {
        Snackbar.make(
            binding.buttonLogin,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun loginViewsMode() {
        binding.buttonLogin.visibility = View.VISIBLE
        binding.buttonRegister.visibility = View.GONE
        binding.editTextName.visibility = View.GONE
        binding.editTextPassword2.visibility = View.GONE
        binding.editTextPhone.visibility = View.GONE
        binding.textViewGoToRegister.visibility = View.VISIBLE
        binding.textViewGoToLogin.visibility = View.GONE
    }

    private fun registerViewsMode() {
        binding.buttonLogin.visibility = View.GONE
        binding.buttonRegister.visibility = View.VISIBLE
        binding.editTextName.visibility = View.VISIBLE
        binding.editTextPassword2.visibility = View.VISIBLE
        binding.editTextPhone.visibility = View.VISIBLE
        binding.textViewGoToLogin.visibility = View.VISIBLE
        binding.textViewGoToRegister.visibility = View.INVISIBLE
    }

    private fun registerProgressViewsMode() {
        binding.progressBarRegister.visibility = View.VISIBLE
        binding.buttonRegister.visibility = View.INVISIBLE
        binding.textViewGoToLogin.visibility = View.INVISIBLE
    }

    private fun loginProgressViewsMode() {
        binding.progressBarLogin.visibility = View.VISIBLE
        binding.buttonLogin.visibility = View.INVISIBLE
        binding.textViewGoToRegister.visibility = View.INVISIBLE
    }

    private fun fromRegisterToVerifyViewsMode() {
        binding.progressBarRegister.visibility = View.INVISIBLE
        binding.buttonRegister.visibility = View.INVISIBLE
        binding.editTextEmail.visibility = View.INVISIBLE
        binding.editTextName.visibility = View.INVISIBLE
        binding.editTextPhone.visibility = View.INVISIBLE
        binding.editTextPassword.visibility = View.INVISIBLE
        binding.editTextPassword2.visibility = View.INVISIBLE
        binding.textViewGoToLogin.visibility = View.INVISIBLE
        binding.textViewVerify.visibility = View.VISIBLE
        binding.buttonVerified.visibility = View.VISIBLE
        binding.textViewSendMailAgain.visibility = View.VISIBLE
    }

    private fun fromLoginToVerifyViewsMode() {
        binding.progressBarLogin.visibility = View.INVISIBLE
        binding.editTextEmail.visibility = View.INVISIBLE
        binding.editTextPassword.visibility = View.INVISIBLE
        binding.textViewVerify.visibility = View.VISIBLE
        binding.buttonVerified.visibility = View.VISIBLE
        binding.textViewSendMailAgain.visibility = View.VISIBLE
    }

    private fun registerFailedViewsMode() {
        binding.progressBarRegister.visibility = View.INVISIBLE
        binding.buttonRegister.visibility = View.VISIBLE
        binding.textViewGoToLogin.visibility = View.VISIBLE
    }

    private fun loginFailedViewsMode() {
        binding.progressBarLogin.visibility = View.INVISIBLE
        binding.buttonLogin.visibility = View.VISIBLE
        binding.textViewGoToRegister.visibility = View.VISIBLE
    }
}