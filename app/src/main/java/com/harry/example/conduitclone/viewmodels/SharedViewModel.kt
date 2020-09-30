package com.harry.example.conduitclone.viewmodels



import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harry.example.conduitclone.pojos.Article
import com.harry.example.conduitclone.pojos.Author
import com.harry.example.conduitclone.pojos.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent



class SharedViewModel : ViewModel(), KoinComponent {
    //private val appRepository: AppRepository by inject()
    private val currentUser: MutableLiveData<User?> = MutableLiveData()
    val userDetails: LiveData<User?>
        get() = currentUser
    val user: User?
        get() = currentUser.value
    private val currentArticle: MutableLiveData<Article> = MutableLiveData()
    val article: LiveData<Article?>
        get() = currentArticle

    var currentUserFeeds: List<Article>? = null
    var authToken: String? =""
        get() = currentUser.value?.token
        set(value) {
            field = value
        }

    var articles: List<Article>? = null

    fun setCurrentUser(user: User?) {
        currentUser.postValue(user)
    }

    fun setCurrentArticle(article: Article?) {
        currentArticle.value = article
    }

    fun setFollowOrUnFollowStatusOfSameAuthorsInList(updatedProfile: Author) {
        viewModelScope.launch(Dispatchers.Default) {
            articles?.forEach {
                if (it.author?.username.equals(updatedProfile.username)) {
                    it.author?.isFollowing = updatedProfile.isFollowing
                }
            }
        }
    }

//    fun currentUser(token: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = appRepository.currentUser(TOKEN.plus(" ").plus(token))
//                response?.let { currentUser ->
//                    setCurrentUser(currentUser)
//                }
//            } catch (exception: Exception) {
//                setCurrentUser(null)
//            }
//        }
//    }
}