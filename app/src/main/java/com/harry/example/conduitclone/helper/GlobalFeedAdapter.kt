package com.harry.example.conduitclone.helper


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Article
import java.lang.ref.WeakReference

class GlobalFeedAdapter(
    private val followListener: WeakReference<OnClickListener?>
) : RecyclerView.Adapter<GlobalFeedAdapterViewHolder>() {
    private var asyncListDiffer: AsyncListDiffer<Article> = AsyncListDiffer(this, ArticleItemCallback())
    private var userSignInStatus: Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalFeedAdapterViewHolder =
        GlobalFeedAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.article_layout, parent, false)
        )

    override fun getItemId(position: Int): Long =
        position.toLong()


    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: GlobalFeedAdapterViewHolder, position: Int) {
        val article = asyncListDiffer.currentList[position]
        article?.let {
            holder.bind(article, userSignInStatus, followListener)
        }
    }

    fun updateUserSignStatus(status: Boolean) {
        userSignInStatus = status
    }

    fun submitList(articles: List<Article>) {
        asyncListDiffer.submitList(articles)
    }

}