package com.example.wsudailyscreener

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast

class UserDataActivity : AppCompatActivity() {
    private lateinit var password: EditText
    private lateinit var accessId: EditText
    private lateinit var phone: EditText
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE)

        this.accessId = findViewById(R.id.editTextAccessId)
        this.password = findViewById(R.id.editTextPassword)
        this.phone = findViewById(R.id.editTextPhone)

        populateUserData()
    }

    fun saveUserData(view: View) {
        if(!validateUserInfo()) {
            return
        }

        Log.i("API", "save userdata")

        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE)

        with (sharedPref.edit()) {
            putString(getString(R.string.sharedpref_access_id_key), accessId.text.toString())
            putString(getString(R.string.sharedpref_password_key), password.text.toString())
            putString(getString(R.string.sharedpref_phone_key), phone.text.toString())
            apply()
            makeToast("Saved!", Toast.LENGTH_LONG)
        }
    }

    private fun populateUserData() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.shared_preference_file_name), Context.MODE_PRIVATE)

        Log.i("API", "populate userdata")
        val accessIdText = sharedPref.getString(getString(R.string.sharedpref_access_id_key), "")
        val passwordText = sharedPref.getString(getString(R.string.sharedpref_password_key), "")
        val phoneText = sharedPref.getString(getString(R.string.sharedpref_phone_key), "")
        if (accessIdText != "") {
            accessId.setText(accessIdText)
        }
        if (passwordText != "") {
            password.setText(passwordText)
        }
        if (phoneText != "") {
            phone.setText(phoneText)
        }
    }

    private fun validateUserInfo(): Boolean {
        if(accessId.text.toString() == "" ||
            password.text.toString() == "" ||
            phone.text.toString() == "") {
            makeToast("Fill all fields.", Toast.LENGTH_LONG)

            return false
        }

        if(phone.text.toString().length != 10) {
            makeToast("Contact No should be exactly 10 digits.", Toast.LENGTH_LONG)

            return false;
        }

        return true
    }

    fun makeToast(text: String,length: Int) {
        this.runOnUiThread {
            Toast.makeText(this@UserDataActivity, text, length).show()
        }
    }
}