package com.harry.example.conduitclone.ui.fragments


import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.helper.CustomTextWatcher
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.LoginAuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment(), View.OnClickListener, View.OnFocusChangeListener,
    CustomTextWatcher {
    private val loginAuthViewModel: LoginAuthViewModel by viewModel()

    override val layoutId = R.layout.fragment_login

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        to_register.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        login.setOnClickListener(this)
        user_email.addTextChangedListener(this)
        user_password.addTextChangedListener(this)
        user_email.onFocusChangeListener = this
        user_password.onFocusChangeListener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeAuth()
    }

    override fun onClick(v: View?) {
        val email = user_email.text.toString()
        val password = user_password.text.toString()
        enableOrDisable(false)
        showProgress()
        sharedViewModel.articles = null
        loginAuthViewModel.loginUser(email, password)
    }

    private fun observeAuth() {
        loginAuthViewModel.currentUserResponse.observe(viewLifecycleOwner) {
            hideProgress()
            enableOrDisable(true)
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { user ->
                        sharedViewModel.setCurrentUser(user)
                        viewLifecycleOwner.lifecycleScope.launch {
                            context?.saveJwtToken(user.token)
                        }
                        view?.findNavController()
                            ?.navigate(
                                R.id.action_loginFragment_to_homeFragment,
                                Bundle()
                            )
                    }
                }
                Status.ERROR -> view?.showMessage(it.message)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        activity?.apply {
            closeKeyBoard()
        }
    }

    private fun showProgress() {
        loginProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        loginProgress.visibility = View.GONE
    }

    private fun enableOrDisable(enability: Boolean) {
        group.referencedIds.forEach {
            val view = view?.findViewById<View>(it)
            when (it) {
                R.id.inner_layout -> {
                    view?.findViewById<View>(R.id.dont_have_an_account)?.isEnabled = enability
                    view?.findViewById<View>(R.id.to_register)?.isEnabled = enability
                }
                else -> view?.isEnabled = enability
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        super.onTextChanged(s, start, before, count)
        login.isEnabled =
            !user_email.text.toString().isEmptyOrIsBlank() && !user_password.text.toString()
                .isEmptyOrIsBlank()
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.user_email -> {
                emailHint.setTextColor(requireContext().color(R.color.colorPrimary))
                passwordHint.setTextColor(requireContext().color(R.color.blackColor))
            }
            else -> {
                passwordHint.setTextColor(requireContext().color(R.color.colorPrimary))
                emailHint.setTextColor(requireContext().color(R.color.blackColor))
            }
        }
    }
}