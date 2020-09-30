package com.harry.example.conduitclone.helper

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.utility.*
import kotlinx.android.synthetic.main.user_article.view.*
import kotlinx.android.synthetic.main.user_article.view.content
import kotlinx.android.synthetic.main.user_article.view.title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserFeedAdapterViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(currentArticle: Article, onclickListener: OnClickListener?) {
        CoroutineScope(Dispatchers.Default).launch {
            val articleContent = currentArticle.articleContent
            val articleTitle = currentArticle.title
            val formattedCreatedDate: String? =
                if (currentArticle.createdAt != currentArticle.updatedAt)
                    updatedTime(currentArticle.updatedAt)
                else
                    getFormattedDate(currentArticle.createdAt)
            var spannableArticleContent: SpannableString? = null
            var spannableArticleTitle: SpannableString? = null
            articleContent?.apply {
                if (length > 20) {
                    spannableArticleContent =
                        getSpannableString(
                            substring(0, 15).plus(" ")
                                .plus(view.getReadMoreString(R.string.read_more)),
                            COLOR_GREEN,
                            adapterPosition,
                            onclickListener
                        )
                }
            }
            articleTitle?.apply {
                if (length > 20) {
                    spannableArticleTitle = getSpannableString(
                        substring(0, 15).plus(" ").plus("...."),
                        COLOR_BLACK,
                        adapterPosition
                    )
                }
            }
            withContext(Dispatchers.Main) {
                view.apply {
                    title.text =
                        if (!spannableArticleTitle.isNullOrBlank()) spannableArticleTitle else articleTitle
                    content.text =
                        if (!spannableArticleContent.isNullOrBlank()) spannableArticleContent else articleContent
                    content.movementMethod = LinkMovementMethod.getInstance()
                    author_username.text = currentArticle.author?.username
                    article_created_date.text = formattedCreatedDate
                    currentArticle.author?.let {
                        Glide.with(view.context).setImage(it.imageUrl, author_profile_image)
                    }
                    menu.setOnClickListener {
                        showPopUpMenu(onclickListener, adapterPosition, currentArticle.slug)
                    }
                }
                view.setOnClickListener {
                    onclickListener?.toArticleDetailsFragment(adapterPosition)
                }
            }
        }
    }

    private fun showPopUpMenu(
        onclickListener: OnClickListener?,
        position: Int,
        currentArticleSlug: String?
    ) {
        val popUpMenu = PopupMenu(
            view.context,
            view.menu_anchor,
            Gravity.TOP
        )
        popUpMenu.inflate(R.menu.user_article_recyclerview_menu)
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> onclickListener?.deleteArticle(position, currentArticleSlug)
                else -> onclickListener?.updateArticle(position)
            }
            return@setOnMenuItemClickListener true
        }
        popUpMenu.show()
    }

}