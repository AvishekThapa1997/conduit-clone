package com.harry.example.conduitclone.helper


import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.harry.example.conduitclone.pojos.Article

class ArticleItemCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
    
}