package com.harry.example.conduitclone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.harry.example.conduitclone.viewmodels.SharedViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseFragment : Fragment() {
    protected val sharedViewModel: SharedViewModel by sharedViewModel()
    open val layoutId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }
}