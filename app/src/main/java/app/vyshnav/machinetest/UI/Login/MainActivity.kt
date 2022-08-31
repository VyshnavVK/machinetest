package app.vyshnav.machinetest.UI.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import app.vyshnav.machinetest.Database.db
import app.vyshnav.machinetest.UI.Profile.ProfileActivity
import app.vyshnav.machinetest.UI.Register.RegisterActivity
import app.vyshnav.machinetest.Util.Util.isValidEmail
import app.vyshnav.machinetest.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            val db = Room.databaseBuilder(
            applicationContext,
            db::class.java, "profile_db"
            ).build()


           suspend fun login() : Boolean {
               return db.ProfileDAO().login(
                   binding.etEmail.text.toString().trim(),
                   binding.etPassword.text.toString().trim()
               ).isNotEmpty()


           }
            lifecycleScope.launch(Dispatchers.IO) {
                if(db.ProfileDAO().isLoggedIn()==1){
                    startActivity(Intent(this@MainActivity,ProfileActivity::class.java))
                    finish()
                }
            }


        binding.apply {
            btnRegister.setOnClickListener {
                startActivity(Intent(this@MainActivity,RegisterActivity::class.java))
            }

            btnLogin.setOnClickListener {
                    if(validation()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (login()) {
                                db.ProfileDAO().setLogin()
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        ProfileActivity::class.java
                                    )
                                )
                            } else {
                                lifecycleScope.launch (Dispatchers.Main){
                                    Toast.makeText(
                                        this@MainActivity,
                                        "login failed!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }
            }
        }

    }

    private fun validation():Boolean {
        binding.apply {
            return if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty()) {
                if (!etEmail.text.toString().isValidEmail()) {
                    etEmail.error = "Enter a valid email!"
                }
                true
            } else {
                if (etEmail.text.isEmpty()) {
                    etEmail.error = "Enter a valid email!"
                }
                if (etPassword.text.isEmpty()) {
                    etEmail.error = "Enter a your password!"
                }
                false
            }
        }
    }
}