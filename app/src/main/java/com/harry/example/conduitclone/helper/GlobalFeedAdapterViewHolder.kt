package com.harry.example.conduitclone.helper

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.utility.*
import kotlinx.android.synthetic.main.article_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class GlobalFeedAdapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(
        article: Article,
        userSignInStatus: Boolean,
        onClickListener: WeakReference<OnClickListener?>
        //  position: Int
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            val onclickListener: OnClickListener? = onClickListener.get()
            val article_content = article.articleContent
            val article_title = article.title
            val formattedCreatedDate: String? =
                if (article.createdAt != article.updatedAt)
                    updatedTime(article.updatedAt)
                else
                    getFormattedDate(article.createdAt)
//            val formattedCreatedDate = getFormattedDate(article.createdAt)
//            if (formattedCreatedDate.isNotEmpty() || !formattedCreatedDate.isBlank()) {
//                article.createdAt = formattedCreatedDate
//            }
            var spannableArticleContent: SpannableString? = null
            var spannableArticleTitle: SpannableString? = null
            article_content?.apply {
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
            article_title?.apply {
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
                        if (!spannableArticleTitle.isNullOrBlank()) spannableArticleTitle else article_title
                    content.text =
                        if (!spannableArticleContent.isNullOrBlank()) spannableArticleContent else article_content
                    content.movementMethod =
                        LinkMovementMethod.getInstance()
                    username.text = article.author?.username
                    created_date.text = formattedCreatedDate
                    if (article.isFavourited) {
                        favourite_button.icon = context?.drawable(R.drawable.heart)
                    } else {
                        favourite_button.icon =
                            context?.drawable(R.drawable.hollow_heart)
                    }
                    article.author?.apply {
                        if (isFollowing) {
                            follow_icon.visibility = View.VISIBLE
                        } else {
                            follow_icon.visibility = View.GONE
                        }
                    }
                    favourite_button.text = article.favoritesCount.toString()
                    article.author?.let {
                        Glide.with(context).setImage(it.imageUrl,profile_image)
                    }
                    profile_image.setOnClickListener {
                        onClickListener.get()?.showImageDialog(article.author?.imageUrl,profile_image)
                    }
                    view.favourite_button.setOnClickListener {
                        if (userSignInStatus == false) {
                            it.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                        } else {
                            if (!article.isFavourited) {
                                favouriteArticle(
                                    article,
                                    adapterPosition,
                                    onclickListener
                                )
                            } else {
                                unFavouriteArticle(
                                    article,
                                    adapterPosition,
                                    onclickListener
                                )
                            }
                        }
                    }
                    setOnClickListener {
                        onClickListener.get()?.toArticleDetailsFragment(adapterPosition)
                    }
                }
            }
        }
    }


    private fun favouriteArticle(
        currentArticle: Article,
        position: Int,
        onClickListener: OnClickListener?
    ) {
        onClickListener?.favouriteArticle(currentArticle.slug, position)
    }

    private fun unFavouriteArticle(
        currentArticle: Article,
        position: Int,
        onClickListener: OnClickListener?
    ) {
        onClickListener?.unFavouriteArticle(currentArticle.slug, position)
    }
}
