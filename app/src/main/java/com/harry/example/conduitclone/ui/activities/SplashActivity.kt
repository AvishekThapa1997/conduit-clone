package com.harry.example.conduitclone.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.harry.example.conduitclone.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
   // private val sharedViewModel: SharedViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        observeToken()
//        observeCurrentUser()
        lifecycleScope.launch {
            delay(1500)
            startActivity(Intent(applicationContext,MainActivity::class.java))
        }
    }

//    private fun observeCurrentUser() {
//        sharedViewModel.userDetails.observe(this) {
//
//            it?.let { currentUser ->
//                val bundle = Bundle().apply {
//                    putSerializable(CURRENT_USER, currentUser)
//                }
//                toMainActivity(bundle)
//            } ?: run {
//                toMainActivity()
//            }
//        }
//    }
//
//    private fun observeToken() {
//        lifecycleScope.launch {
//            readJwtToken().asLiveData().observe(this@SplashActivity) {
//                if (!it.isEmptyOrIsBlank()) {
//                    sharedViewModel.currentUser(it)
//                } else {
//                    lifecycleScope.launch(Dispatchers.Default) {
//                        delay(1500)
//                        toMainActivity()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun toMainActivity(bundle: Bundle? = null) {
//        bundle?.let {
//            Log.i("TAG", "toMainActivity: ")
//            startActivity(
//                Intent(applicationContext, MainActivity::class.java).putExtra(
//                    CURRENT_USER_BUNDLE, bundle
//                )
//            )
//        } ?: run {
//            startActivity(Intent(applicationContext, MainActivity::class.java))
//        }
//    }
}