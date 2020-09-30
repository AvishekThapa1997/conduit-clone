package com.harry.example.conduitclone.helper


import android.view.View
import java.io.Serializable

interface OnClickListener:Serializable {
    fun favouriteArticle(articleSlug: String?, position: Int) {

    }
     fun unFavouriteArticle(articleSlug: String?, position: Int) {

    }

    fun toArticleDetailsFragment(position: Int){

    }
    fun deleteArticle(position: Int,slug : String?) {

    }

    fun updateArticle(position: Int) {

    }
    fun showImageDialog(imageUrl : String?,view: View) {

    }
}
