package com.harry.example.conduitclone.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.utility.Status
import com.harry.example.conduitclone.utility.isEmptyOrIsBlank
import com.harry.example.conduitclone.utility.readJwtToken
import com.harry.example.conduitclone.viewmodels.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class SplashFragment : BaseFragment() {
    private val splashViewModel: SplashViewModel by viewModel()
    override val layoutId = R.layout.fragment_splash


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeJwtToken()
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        splashViewModel.currentUser.observe(viewLifecycleOwner) {
            if (it.status == Status.SUCCESS)
                sharedViewModel.setCurrentUser(it.data)
            toHomeFragment()
        }
    }

    private fun observeJwtToken() {
        viewLifecycleOwner.lifecycleScope.launch {
            requireContext().readJwtToken().asLiveData().observe(viewLifecycleOwner, { token ->
                if (!token.isEmptyOrIsBlank())
                    splashViewModel.currentUser(token)
                else
                    toHomeFragment()
            })
        }
    }

    private fun toHomeFragment() {
        Log.i("TAG", "toHomeFragment: ")
        view?.findNavController()?.navigate(R.id.action_splashFragment_to_homeFragment)
    }
}