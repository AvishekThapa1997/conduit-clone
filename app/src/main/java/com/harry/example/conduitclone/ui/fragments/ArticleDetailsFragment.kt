package com.harry.example.conduitclone.ui.fragments

import android.os.Bundle

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.pojos.User
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.ArticleDetailsViewModel
import kotlinx.android.synthetic.main.fragment_article_details.*
import kotlinx.android.synthetic.main.fragment_article_details.article_created_date
import kotlinx.android.synthetic.main.fragment_article_details.article_full_content
import kotlinx.android.synthetic.main.fragment_article_details.article_slug
import kotlinx.android.synthetic.main.fragment_article_details.article_title
import kotlinx.android.synthetic.main.fragment_article_details.author_profile_image
import kotlinx.android.synthetic.main.fragment_article_details.author_username
import kotlinx.android.synthetic.main.fragment_article_details.chip_groups
import kotlinx.android.synthetic.main.fragment_article_details.follow_unFollow_author
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel


class ArticleDetailsFragment : BaseFragment() {

    private var article: Article? = null
    private val articleDetailsViewModel: ArticleDetailsViewModel by viewModel()
    private var currentUser: User? = null
    private var authToken: String? = ""

    override val layoutId: Int = R.layout.fragment_article_details
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeCurrentArticle()
        observeCurrentUser()
        observeUpdateAuthorProfile()
    }

    private fun observeCurrentUser() {
        sharedViewModel.userDetails.observe(viewLifecycleOwner, {
            currentUser = it
            authToken = it?.token
        })
    }

    private fun observeUpdateAuthorProfile() {
        articleDetailsViewModel.updatedAuthorProfile.observe(viewLifecycleOwner) {
            it?.apply {
                when (it.status) {
                    Status.SUCCESS -> {
                        this.data?.apply {
                            setFollowUnFollowButton(isFollowing)
                            sharedViewModel.setFollowOrUnFollowStatusOfSameAuthorsInList(this)
                        }
                    }
                    Status.ERROR -> {
                        view?.showMessage(it.message)
                    }
                }
            }
        }
    }

    private fun setUI(article: Article?) {
        article?.apply {
            val formattedCreatedDate: String? =
                if (createdAt != updatedAt)
                    updatedTime(updatedAt)
                else
                    getFormattedDate(createdAt)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.Main) {
                    article_title.text = title
                    article_created_date.text = formattedCreatedDate
                    article_slug.text = slug
                    author?.apply {
                        arguments?.let {
                            follow_unFollow_author.visibility = View.INVISIBLE
                            val params = separator.layoutParams as ConstraintLayout.LayoutParams
                            params.topToBottom = R.id.article_slug_barrier
                            params.startToStart = R.id.start_guideline
                            params.endToEnd = R.id.end_guideline
                            separator.requestLayout()
                        } ?: run {
                            setFollowUnFollowButton(isFollowing)
                            follow_unFollow_author.setOnClickListener {
                                if (!isFollowing) {
                                    articleDetailsViewModel.followUser(authToken, username)
                                } else {
                                    articleDetailsViewModel.unfollowUser(authToken, username)
                                }
                            }
                        }
                        author_username.text = username
                        view?.apply {
                            Glide.with(context).setImage(imageUrl, author_profile_image)
                        }
                    }
                    article_full_content.text = articleContent
                    tagList?.let {
                        it.forEach { chipText ->
                            val chip: Chip =
                                layoutInflater.inflate(
                                    R.layout.chip_tag_layout,
                                    chip_groups,
                                    false
                                ) as Chip
                            chip.text = chipText
                            chip.isCloseIconVisible = false
                            chip_groups.addView(chip)
                        }
                    }
                    textView_tags.visibility = View.VISIBLE
                    if (chip_groups.childCount > 0)
                        chip_scroller.visibility = View.VISIBLE
                    else
                        no_tags.visibility = View.VISIBLE
                    allArticleDetails.visibility = View.VISIBLE
                    loadProgress.visibility = View.GONE
                }
            }
        }
    }

    private fun setFollowUnFollowButton(isFollowing: Boolean) {
        if (isFollowing) {
            follow_unFollow_author.text = UNFOLLOW
            follow_unFollow_author.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.after_follow)
        } else {
            follow_unFollow_author.text = FOLLOW
            follow_unFollow_author.icon =
                ContextCompat.getDrawable(requireContext(), R.drawable.before_follow)
        }

    }

    private fun observeCurrentArticle() {
        sharedViewModel.article.observe(viewLifecycleOwner) {
            it?.apply {
                article = it
                setUI(article)
            }
        }
    }
}
