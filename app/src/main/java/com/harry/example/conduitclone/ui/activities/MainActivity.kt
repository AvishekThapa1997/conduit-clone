package com.harry.example.conduitclone.ui.activities


import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_about.view.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private var currentDestinationId by Delegates.notNull<Int>()
    private var fragmentId by Delegates.notNull<Int>()
    private val sharedViewModel: SharedViewModel by viewModel()
    private val drawerListener: DrawerLayout.DrawerListener =
        object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                navigateFragment(fragmentId)
            }
        }

    private val titles: MutableMap<Int, String> = hashMapOf()
    private val navigationController: NavController.OnDestinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            navigation_view.setCheckedItem(destination.id)
            currentDestinationId = destination.id
            fragmentId = currentDestinationId
            if (currentDestinationId == R.id.homeFragment) {
                setupSupportActionBar(R.drawable.hamburger_menu)
                return@OnDestinationChangedListener
            }
            setupSupportActionBar(R.drawable.back)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch(Dispatchers.Default) {
            setUpTitles()
            withContext(Dispatchers.Main) {
                setSupportActionBar(findViewById(R.id.toolbar))
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                setupSupportActionBar(R.drawable.hamburger_menu)
                findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
                    if (currentDestinationId == R.id.homeFragment)
                        openDrawer()
                    else {
                        navController.navigateUp()
                    }
                }
                findViewById<Toolbar>(R.id.toolbar).isSaveEnabled
            }
        }
        navController = findNavController(R.id.fragment_container)
        navigation_view.setNavigationItemSelectedListener(this@MainActivity)
        navigation_view.menu.findItem(R.id.logout).isVisible = false
        observeUserResponse()
    }

    private fun navigateFragment(fragmentId: Int) {
        if (fragmentId != currentDestinationId) {
            val isIncluded: Boolean = (fragmentId == R.id.homeFragment)
            navController.navigate(
                fragmentId, null, NavOptions.Builder().setEnterAnim(R.anim.fragment_enter)
                    .setExitAnim(R.anim.fragment_exit).setPopEnterAnim(R.anim.fragment_enter)
                    .setPopExitAnim(R.anim.fragment_exit)
                    .setPopUpTo(R.id.homeFragment, isIncluded)
                    .build()
            )
        } else {
            closeDrawer()
        }
    }

    private fun showAboutDialog() {
        lifecycleScope.launch {
            Dispatchers.Default
            val firstParagraph = getSpannableString(
                getData(R.string.about_paragraph1).plus(" ")
                    .plus(getData(R.string.realworld_main_url))
            )
            val secondParagraph = getData(R.string.about_paragraph2)
            withContext(Dispatchers.Main) {
                val view =
                    LayoutInflater.from(applicationContext)
                        .inflate(R.layout.fragment_about, null, false)
                view.paragraph1.text = firstParagraph
                view.paragraph2.text = secondParagraph
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setView(view)
                builder.create()
                val alertDialog = builder.show()
                view.close.setOnClickListener {
                    alertDialog.cancel()
                    navigation_view.setCheckedItem(currentDestinationId)
                    closeDrawer()
                }
            }
        }
    }

    private fun getSpannableString(data: String): SpannableString {
        return SpannableString(data).apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#5cb85c")),
                data.lastIndexOf(" "),
                data.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun getData(paragraphId: Int): String {
        return applicationContext.resources.getString(paragraphId)
    }

    private fun closeDrawer() = menu_drawer_layout.closeDrawer(GravityCompat.START)


    private fun openDrawer() = menu_drawer_layout.openDrawer(GravityCompat.START)


    private fun setupSupportActionBar(icon: Int) {
        supportActionBar?.title = titles[currentDestinationId]
        supportActionBar?.setHomeAsUpIndicator(icon)
    }

    private fun setUpTitles() {
        titles[R.id.homeFragment] = getData(R.string.app_name)
        titles[R.id.settingFragment] = getData(R.string.setting_title)
        titles[R.id.loginFragment] = getData(R.string.login)
        titles[R.id.registerFragment] = getData(R.string.register)
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener(navigationController)
        menu_drawer_layout.addDrawerListener(drawerListener)
    }

    override fun onStop() {
        super.onStop()
        navController.removeOnDestinationChangedListener(navigationController)
        menu_drawer_layout.removeDrawerListener(drawerListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        titles.clear()
    }

    private fun observeUserResponse() {
        sharedViewModel.userDetails.observe(this) {
            it?.let { user ->
                navigation_view.menu.findItem(R.id.loginFragment).isVisible = false
                navigation_view.menu.findItem(R.id.logout).isVisible = true
                setWelcomeUserData(user.username)
            } ?: run {
                navigation_view.menu.findItem(R.id.loginFragment).isVisible = true
                navigation_view.menu.findItem(R.id.logout).isVisible = false
                setWelcomeUserData("")
            }
        }
    }

    private fun setWelcomeUserData(username: String) {
        val headerView = navigation_view.getHeaderView(0)
        if (!username.isEmptyOrIsBlank()) {
            headerView.user_name.text = username
            headerView.hello_text.visibility = View.VISIBLE
            headerView.user_name.visibility = View.VISIBLE
        } else {
            headerView.hello_text.visibility = View.GONE
            headerView.user_name.visibility = View.GONE
        }
    }

//    private fun requestPermissionForReadingStorage() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(
//                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                READ_STORAGE_REQUEST_CODE
//            )
//        } else {
//            chooseImage()
//        }
//    }

    private fun logOutUser() {
        sharedViewModel.setCurrentUser(null)
        sharedViewModel.articles = null
        sharedViewModel.currentUserFeeds = null
//        lifecycleScope.launch {
//            applicationContext.saveJwtToken("")
//        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.homeFragment, R.id.loginFragment, R.id.settingFragment -> {
                fragmentId = item.itemId
                closeDrawer()
            }
            R.id.logout -> {
                logOutUser()
                fragmentId = R.id.homeFragment
                currentDestinationId = 0
                closeDrawer()
            }
            else -> showAboutDialog()
        }
        return true
    }


//    private fun checkPermissionForReadingStorage(): Int {
//        return ContextCompat.checkSelfPermission(
//            applicationContext,
//            Manifest.permission.READ_EXTERNAL_STORAGE
//        )
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == READ_STORAGE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // chooseImage()
//        } else {
//            showUploadErrorMessage(ALLOW_PERMISSION)
//        }
//    }

//    private fun showUploadErrorMessage(message: String?) {
//        message?.let {
//            findViewById<View>(android.R.id.content).showMessage(message)
//        }
//    }


//    private fun chooseImage() {
//        CropImage.activity()
//            .setGuidelines(CropImageView.Guidelines.ON)
//            .start(this)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == RESULT_OK) {
//                val resultUri: Uri = result.uri
//                sharedViewModel.user?.let {
//                    sharedViewModel.uploadPhoto(it, resultUri)
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                showUploadErrorMessage(SOMETHING_WENT_WRONG)
//            }
//        }
//    }
}
