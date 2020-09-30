package com.harry.example.conduitclone.helper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Article
import java.lang.ref.WeakReference

class UserFeedAdapter(private val clickListener: WeakReference<OnClickListener>) :
    RecyclerView.Adapter<UserFeedAdapterViewHolder>() {
     val currentListSize: Int
        get() = asyncListDiffer.currentList.size
    private val asyncListDiffer: AsyncListDiffer<Article> =
        AsyncListDiffer(this, ArticleItemCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFeedAdapterViewHolder =
        UserFeedAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_article, parent, false)
        )


    override fun onBindViewHolder(holder: UserFeedAdapterViewHolder, position: Int) =
        holder.bind(asyncListDiffer.currentList[position], clickListener.get())

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun submitList(feeds: List<Article>?) {
        feeds?.let {
            asyncListDiffer.submitList(feeds)
        }
    }
    fun clearCurrentList() {
        //asyncListDiffer.currentList = null
    }
}