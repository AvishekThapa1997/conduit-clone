package com.harry.example.conduitclone.ui.fragments


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.helper.CustomTextWatcher
import com.harry.example.conduitclone.utility.*
import com.harry.example.conduitclone.viewmodels.RegisterAuthViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class RegisterFragment : BaseFragment(), CustomTextWatcher, View.OnFocusChangeListener {

    private val registerAuthViewModel: RegisterAuthViewModel by viewModel()
    private val hintId: HashMap<Int, Int> = HashMap()
    override val layoutId = R.layout.fragment_register

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back_to_login.setOnClickListener {
            view.findNavController().navigateUp()
        }
        hintIds()
        attachTextListener()
        addFocusedListener()
        register.setOnClickListener {
            val email = email.text.toString()
            val username = username.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirm_password.text.toString()
            enableOrDisable(false)
            showProgress()
            sharedViewModel.articles = null
            registerAuthViewModel.registerUser(email, username, password, confirmPassword)
        }
    }

    private fun hintIds() {
        hintId[R.id.email] = R.id.textEmailHint
        hintId[R.id.username] = R.id.textUsernameHint
        hintId[R.id.password] = R.id.textPasswordHint
        hintId[R.id.confirm_password] = R.id.textConfirmPasswordHint
    }

    private fun attachTextListener() {
        email.addTextChangedListener(this)
        username.addTextChangedListener(this)
        password.addTextChangedListener(this)
        confirm_password.addTextChangedListener(this)
    }

    private fun addFocusedListener() {
        email.onFocusChangeListener = this
        username.onFocusChangeListener = this
        password.onFocusChangeListener = this
        confirm_password.onFocusChangeListener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeAuth()
    }

    private fun enableOrDisable(value: Boolean) {
        progressGroup.referencedIds.forEach {
            view?.findViewById<View>(it)?.isEnabled = value
        }
    }

    private fun showProgress() {
        registerProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        registerProgress.visibility = View.GONE
    }

    private fun observeAuth() {
        registerAuthViewModel.registeredUserResponse.observe(viewLifecycleOwner)  {
            when (it.status) {
                Status.SUCCESS -> it.data?.let { user ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        context?.apply {
                            writeToDataSource(0)
                            saveJwtToken(user.token)
                        }
                    }
                    sharedViewModel.setCurrentUser(user)
                    view?.findNavController()
                        ?.navigate(R.id.action_registerFragment_to_homeFragment, Bundle())
                }
                Status.ERROR -> view?.showMessage(it.message)
            }
            hideProgress()
            enableOrDisable(true)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        register.isEnabled = !email.text.toString().isEmptyOrIsBlank() && !username.text.toString()
            .isEmptyOrIsBlank() && !password.text.toString()
            .isEmptyOrIsBlank() && !confirm_password.text.toString().isEmptyOrIsBlank()
    }

    override fun onStop() {
        super.onStop()
        activity?.apply {
            closeKeyBoard()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        v?.apply {
            val viewId = hintId[v.id]
            viewId?.let {
                val textView: TextView? = view?.findViewById(viewId)
                textView?.let {
                    if (hasFocus)
                        textView.setTextColor(requireContext().color(R.color.colorPrimary))
                    else
                        textView.setTextColor(requireContext().color(R.color.blackColor))
                }
            }
        }
    }
}