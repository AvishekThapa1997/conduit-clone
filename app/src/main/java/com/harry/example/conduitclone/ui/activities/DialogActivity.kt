package com.harry.example.conduitclone.ui.activities

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.utility.drawable
import com.harry.example.conduitclone.utility.setImage
import kotlinx.android.synthetic.main.activity_dialog.*

class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        intent.getBundleExtra("bundle")?.apply {
            Glide.with(applicationContext).load(getString("url", ""))
                .listener(object : RequestListener<Drawable> {
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

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        stopShimmer()
                        image.setImageDrawable(drawable(R.drawable.default_user_avataar))
                        return false
                    }
                }).into(image)
        }
    }
    private fun stopShimmer() {
        shimmer.stopShimmer()
        shimmer.visibility = View.GONE
    }

}