package com.harry.example.conduitclone.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.helper.OnClickListener
import com.harry.example.conduitclone.helper.UserFeedAdapter
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.UserFeedViewModel
import kotlinx.android.synthetic.main.fragment_user_feed.*
import kotlinx.android.synthetic.main.fragment_user_feed.loadProgress
import kotlinx.android.synthetic.main.fragment_user_feed.recycler_view
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.lang.ref.WeakReference

class UserFeed : BaseFragment(), OnClickListener {
    private lateinit var userFeedAdapter: UserFeedAdapter
    private var followUserListener: OnClickListener? = null
    private val userFeedViewModel: UserFeedViewModel by viewModel()
    private var notByDeletion: Boolean = true
    private val fabShowListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onShown(fab: FloatingActionButton?) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                requireContext().readFromDataSource().asLiveData()
                    .observe(viewLifecycleOwner, {
                        if (it >= 0) {
                            showPostArticlePrompt()
                        }
                    })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sharedViewModel.user != null && sharedViewModel.authToken != null) {
            userFeedViewModel.getUserFeed(sharedViewModel.authToken, sharedViewModel.user?.username)
        }
        followUserListener = this
        userFeedAdapter = UserFeedAdapter(WeakReference(followUserListener))
        if (!userFeedAdapter.hasObservers()) {
            userFeedAdapter.setHasStableIds(true)
        }
    }

    override val layoutId = R.layout.fragment_user_feed

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        sharedViewModel.user?.let {
            add_article.show(fabShowListener)
            add_article.setOnClickListener {
                it.findNavController()
                    .navigate(
                        R.id.action_homeFragment_to_postArticleFragment,
                        Bundle().apply {
                            putString(BUTTON_TEXT, POST)
                        }
                    )
            }
        } ?: run {
            add_article.hide()
            login.visibility = View.VISIBLE
            hideProgress()
            login.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
        swipe_layout.setOnRefreshListener {
            swipe_layout.isRefreshing = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeUserFeed()
    }

    private fun hideProgress() {
        loadProgress.visibility = View.INVISIBLE
    }

    private fun observeUserFeed() {
        userFeedViewModel.currentUserFeed.observe(viewLifecycleOwner, {
           hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.apply {
                        sharedViewModel.currentUserFeeds = it.data
                        userFeedAdapter.submitList(it.data)
                        showRecyclerView()
                        notByDeletion = true
                    }
                }
                Status.ERROR -> {
                    view?.showMessage(it.message)
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        recycler_view.adapter = userFeedAdapter
        recycler_view.setHasFixedSize(true)
        val layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
        recycler_view.layoutManager = layoutManager
    }


    private fun showRecyclerView() {
        hideProgress()
        swipe_layout.visibility = View.VISIBLE
    }

    override fun toArticleDetailsFragment(position: Int) {
        sharedViewModel.currentUserFeeds?.let {
            sharedViewModel.setCurrentArticle(it[position])
        }
        view?.findNavController()?.navigate(
            R.id.action_homeFragment_to_articleDetailsFragment,
            Bundle()
        )
    }

    override fun deleteArticle(position: Int, slug: String?) {
        notByDeletion = false
        userFeedViewModel.deleteArticle(sharedViewModel.authToken, position, slug)
    }

    override fun updateArticle(position: Int) {
        view?.findNavController()
            ?.navigate(R.id.action_homeFragment_to_postArticleFragment, Bundle().apply {
                putString(BUTTON_TEXT, UPDATE)
                putParcelable(
                    ARTICLE_TO_BE_UPDATED,
                    sharedViewModel.currentUserFeeds?.get(position)
                )
            })
    }

    fun showPostArticlePrompt() {
        hideProgress()
        MaterialTapTargetPrompt.Builder(requireActivity()).apply {
            setTarget(add_article)
            setPrimaryText("Post Your First Article")
            setSecondaryText("OK")
            autoDismiss = true
            backgroundColour =
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_DISMISSED) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        context?.writeToDataSource(-1)
                    }
                }
            }
        }.show()
    }

    override fun onDestroyView() {
        recycler_view.adapter = null
        super.onDestroyView()
    }
}