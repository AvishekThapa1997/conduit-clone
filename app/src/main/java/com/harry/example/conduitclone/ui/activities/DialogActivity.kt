package com.harry.example.conduitclone.ui.activities

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.harry.example.conduitclone.R
import kotlinx.android.synthetic.main.activity_dialog.*

class DialogActivity : AppCompatActivity() {
    private val glideListener : RequestListener<Drawable> by lazy {
        object  : RequestListener<Drawable>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                stopShimmer()
               return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                stopShimmer()
                return false
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        intent.getBundleExtra("bundle")?.apply {
            val url = getString("url", "")
            if (url.isNotEmpty() || url.isNotBlank()) {
                Glide.with(applicationContext).load(url).listener(glideListener).into(image)
            } else {
                Glide.with(applicationContext).load(R.drawable.default_user_avataar).listener(glideListener).into(image)
            }
        }
    }

    private fun stopShimmer() {
        shimmer.stopShimmer()
        shimmer.visibility = View.GONE
    }

    override fun onBackPressed() {
        finish()
    }
}