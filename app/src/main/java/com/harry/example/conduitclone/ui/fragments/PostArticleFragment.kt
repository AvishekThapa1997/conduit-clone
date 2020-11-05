package com.harry.example.conduitclone.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.PostArticleViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_post_article.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class PostArticleFragment : BaseFragment(), View.OnClickListener {
    private val postArticleViewModel: PostArticleViewModel by viewModel()
    private val tags: LinkedList<String> = LinkedList()
    private var token: String? = ""
    private var articleToBeUpdated: Article? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        token = sharedViewModel.authToken
    }

    override val layoutId = R.layout.fragment_post_article

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).menu_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        post_or_update_article.setOnClickListener(this)
        arguments?.apply {
            val buttonText = getString(BUTTON_TEXT, "")
            post_or_update_article.text = buttonText
            if (containsKey(ARTICLE_TO_BE_UPDATED)) {
                articleToBeUpdated = getParcelable(ARTICLE_TO_BE_UPDATED)
                bindUpdatedArticleToUI(articleToBeUpdated)
            }
        }
        add_tag.setOnClickListener(this)
        observePostedArticle()
    }

    override fun onStop() {
        super.onStop()
        activity?.closeKeyBoard()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.post_or_update_article -> postArticle()
            else -> addTagToListAndInChipGroups()
        }
    }

    private fun postArticle() {
        val articleTitle = article_title.text.toString()
        val articleDescription = article_description.text.toString()
        val articleBody = article_body.text.toString()
        showProgress()
        enableOrDisableViews(false)
        articleToBeUpdated?.let {
            postArticleViewModel.postOrUpdateArticle(
                token,
                articleTitle,
                articleDescription,
                articleBody,
                tags,
                true,
                it.slug,
                it
            )
            Log.i("TAG", "Update")
        } ?: run {
            postArticleViewModel.postOrUpdateArticle(
                token,
                articleTitle,
                articleDescription,
                articleBody,
                tags
            )
        }
    }

    private fun addTagToListAndInChipGroups() {
        val tag = article_tags.text.toString()
        if (tag.isNotBlank() || tag.isNotBlank()) {
            val chip = createChip(tag)
            chip_groups.addView(chip)
            article_tags.text = Editable.Factory.getInstance().newEditable("")
            tags.add(tag)
        } else {
            view?.showMessage("Tag Cannot be empty")
        }
    }

    private fun createChip(tag: String): Chip {
        val chip = layoutInflater.inflate(R.layout.chip_tag_layout, chip_groups, false) as Chip
        chip.closeIcon = ContextCompat.getDrawable(requireContext(), R.drawable.close)
        chip.text = tag
        chip.setOnCloseIconClickListener {
            removeCurrentChip(it)
        }
        return chip
    }

    private fun removeCurrentChip(view: View) {
        chip_groups.removeView(view)
        tags.remove((view as Chip).text)
    }

    private fun observePostedArticle() {
        postArticleViewModel.currentUserArticle.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let {
                        //sharedViewModel.postedArticle(it)
                    }
                    view?.findNavController()
                        ?.navigate(R.id.action_postArticleFragment_to_homeFragment, Bundle())
                }
                Status.ERROR -> {
                    view?.showMessage(it.message)
                }
            }
            hideProgress()
            enableOrDisableViews(true)
        }
    }

    private fun bindUpdatedArticleToUI(article: Article?) {
        article?.apply {
            article_title.text = getEditable(title)
            article_description.text = getEditable(description)
            article_body.text = getEditable(articleContent)
            tagList?.forEach {
                val chip = createChip(it)
                chip_groups.addView(chip)
                tags.add(it)
            }
        }
    }

    private fun showProgress() {
        post_article_progress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        post_article_progress.visibility = View.INVISIBLE
    }

    private fun enableOrDisableViews(enabled: Boolean) {
        hide_views.referencedIds.forEach { viewId ->
            view?.findViewById<View>(viewId)?.isEnabled = enabled
        }
    }

    override fun onDestroyView() {
        (activity as AppCompatActivity).menu_drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        super.onDestroyView()
    }
}