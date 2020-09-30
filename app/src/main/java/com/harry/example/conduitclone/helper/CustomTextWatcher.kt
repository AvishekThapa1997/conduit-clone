package com.harry.example.conduitclone.helper

import android.text.Editable
import android.text.TextWatcher

interface CustomTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?){}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){}
}