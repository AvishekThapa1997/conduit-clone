package com.harry.example.conduitclone.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Success
import com.harry.example.conduitclone.utility.CURRENT_USER
import com.harry.example.conduitclone.utility.Status
import com.harry.example.conduitclone.utility.isEmptyOrIsBlank
import com.harry.example.conduitclone.utility.readJwtToken
import com.harry.example.conduitclone.viewmodels.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class SplashActivity : AppCompatActivity() {
    private val splashViewModel: SplashViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        observeCurrentUser()
        lifecycleScope.launch {
            applicationContext.readJwtToken().collect { token ->
                if (!token.isEmptyOrIsBlank())
                    splashViewModel.currentUser(token)
                else {
                    delay(1500)
                    toMainActivity()
                }
            }
        }
    }

    private fun observeCurrentUser() {
        splashViewModel.currentUser.observe(this) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    toMainActivity(Bundle().apply {
                        putSerializable(CURRENT_USER, response.data)
                    })
                }
                Status.ERROR -> {
                    toMainActivity()
                }
            }
        }
    }


    private fun toMainActivity(bundle: Bundle? = null) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        bundle?.apply {
            intent.putExtras(this)
        }
        startActivity(intent)
    }
}