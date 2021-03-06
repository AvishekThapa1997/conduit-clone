package com.harry.example.conduitclone.ui.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.helper.OnClickListener
import com.harry.example.conduitclone.helper.GlobalFeedAdapter
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.ui.activities.DialogActivity
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.fragment_global_feed.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.ref.WeakReference


class GlobalFeed : BaseFragment(), OnClickListener {

    private val homeViewModel: HomeViewModel by viewModel()
   // private val networkChecker: NetworkChecker by inject()
    private var token: String = ""
    private var isRecyclerViewDataLoaded = false
    private var moreDataNeeded = true
    private val recyclerViewScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItem = recyclerView.layoutManager?.childCount
                val totalItem = globalFeedAdapter.itemCount
                val scrolledOutItem =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (moreDataNeeded && visibleItem?.plus(scrolledOutItem) == totalItem && (sharedViewModel.articles == null || homeViewModel.offset <= 80)) {
                    more_data_loading_progress.visibility = View.VISIBLE
                    getArticles()
                    moreDataNeeded = false
                }
            }
        }

    private val articlesResponseObserver: Observer<Resource<List<Article>?>> by lazy {
        Observer<Resource<List<Article>?>> {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { articles ->
                        globalFeedAdapter.submitList(articles)
                        sharedViewModel.articles = articles
                        isRecyclerViewDataLoaded =
                            true // this indicates that items are visible in recyclerview so no need to set articles again
                        moreDataNeeded = true
                    }
                    showRecyclerView()
                }
                Status.ERROR -> {
                    if (globalFeedAdapter.itemCount == 0) {
                        noArticleFound(it.message)
                    }
                    isRecyclerViewDataLoaded = false
                    moreDataNeeded = true
                }
            }
            stopShimmer()
            if (more_data_loading_progress.isVisible) {
                more_data_loading_progress.visibility = View.GONE
            }
        }
    }

    private lateinit var globalFeedAdapter: GlobalFeedAdapter
    private var followUserListener: OnClickListener? = null

    override val layoutId = R.layout.fragment_global_feed
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        followUserListener = this
        globalFeedAdapter = GlobalFeedAdapter(WeakReference(followUserListener))
        globalFeedAdapter.apply {
            if (!hasObservers()) {
                setHasStableIds(true)
            }
        }
        sharedViewModel.authToken?.apply {
            token = this
            globalFeedAdapter.updateUserSignStatus(true)
        } ?: run {
            token = ""
            globalFeedAdapter.updateUserSignStatus(false)
        }
        if (sharedViewModel.articles == null)
            getArticles()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
        retry.setOnClickListener {
            error_message.visibility = View.GONE
            no_article_found.visibility = View.GONE
            startShimmer()
            getArticles()
        }
        setUpRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeArticles()
        observeError()
    }

    private fun observeError() {
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            view?.showMessage(errorMessage)
        }
    }

//    private fun observeNetworkStatus() {
//        networkChecker.observe(viewLifecycleOwner) {
//           // isNetworkAvailable = it
//        }
//    }

    private fun observeArticles() =
        homeViewModel.apiResponse.observe(viewLifecycleOwner, articlesResponseObserver)

    private fun setUpRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = globalFeedAdapter
        sharedViewModel.articles?.let { // this indicates data has already been retrieved but the fragment is destroyed and created again but no need to retrieve data from server because user signin status has not changed and previous data is in the memory and #isRecyclerViewDataLoaded indicated no data is showing in recyclerview
            if (!isRecyclerViewDataLoaded)
                homeViewModel.setMemoryCacheArticleList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        recycler_view.addOnScrollListener(recyclerViewScrollListener)
    }

    override fun onPause() {
        super.onPause()
        recycler_view.removeOnScrollListener(recyclerViewScrollListener)
    }

    private fun showRecyclerView() {
        swipe.visibility = View.VISIBLE
    }

    private fun noArticleFound(message: String?) {
        error_message.text = message
        no_article_found.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        recycler_view.adapter = null
        super.onDestroyView()
    }

    override fun favouriteArticle(
        articleSlug: String?,
        position: Int,
    ) {
        homeViewModel.favouriteArticle(token, articleSlug, position)
    }

    override fun unFavouriteArticle(articleSlug: String?, position: Int) {
        homeViewModel.unFavouriteArticle(token, articleSlug, position)
    }

    override fun toArticleDetailsFragment(position: Int) {
        sharedViewModel.setCurrentArticle(sharedViewModel.articles?.get(position))
        view?.findNavController()
            ?.navigate(R.id.action_homeFragment_to_articleDetailsFragment)
    }

    override fun showImageDialog(imageUrl: String?, view: View) {
        val activityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(activity as Activity, view, "image")
        val intent = Intent(context, DialogActivity::class.java)
        val bundle = Bundle()
        imageUrl?.let {
            bundle.putString("url", it)
        } ?: run {
            bundle.putString("url", "")
        }
        intent.putExtra("bundle", bundle)
        activity?.startActivity(intent, activityOptionsCompat.toBundle())
    }

    private fun getArticles() {
        sharedViewModel.user?.apply {
            homeViewModel.getArticles(token, username)
        } ?: run {
            homeViewModel.getArticles(token)
        }
    }

    private fun stopShimmer() {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
    }

    private fun startShimmer() {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        followUserListener = null
    }
}