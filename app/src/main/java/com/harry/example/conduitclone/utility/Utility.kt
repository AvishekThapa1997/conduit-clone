package com.harry.example.conduitclone.utility

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.harry.example.conduitclone.R
import com.harry.example.conduitclone.helper.OnClickListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

private val months: Array<String> = arrayOf(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
)

fun setMessageWithRespectToException(exception: Exception): String {
    return when (exception) {
        is UnknownHostException -> UNKNOWN_HOST_EXCEPTION
        is SSLHandshakeException -> SSL_HANDSHAKE_EXCEPTION
        is SocketTimeoutException -> CONNECTION_TIMEOUT
        is ConnectException -> CONNECTION_EXCEPTION
        is IOException -> TIMEOUT_EXCEPTION
        else -> SOMETHING_WENT_WRONG
    }
}

fun Activity.closeKeyBoard() {
    currentFocus?.let {
        val inputMethodManager: InputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            it.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

private fun parsedDate(date: String?): Date? {
    val pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    date?.let {
        return try {
            simpleDateFormat.parse(date)
        } catch (parseException: ParseException) {
            null
        }
    }
    return null
}

fun getFormattedDate(date: String?): String {
    val parsedDate = parsedDate(date)
    return dateDetails(parsedDate)
}

private fun dateDetails(date: Date?): String {
    date?.let {
        val calender = Calendar.getInstance()
        calender.time = it
        val stringBuffer = StringBuffer()
        return stringBuffer.append(months[calender.get(Calendar.MONTH)]).append(" ")
            .append(calender.get(Calendar.DATE)).append(",")
            .append(calender.get(Calendar.YEAR))
            .toString()
    }
    return ""
}

fun updatedTime(dateAgo: String?): String? {
    if (!dateAgo.isNullOrEmpty() && !dateAgo.isNullOrBlank()) {
        val updatedDate = parsedDate(dateAgo)
        updatedDate?.let {
            val currentDate = Date()
            val hours: Long =
                TimeUnit.MILLISECONDS.toHours(currentDate.time - updatedDate.time)
            val minutes: Long =
                TimeUnit.MILLISECONDS.toMinutes(currentDate.time - updatedDate.time)
            val seconds: Long =
                TimeUnit.MILLISECONDS.toSeconds(currentDate.time - updatedDate.time)
            return when {
                seconds < 60 -> UPDATED.plus(" $seconds Seconds Ago")
                minutes < 60 -> UPDATED.plus(" $minutes minutes ago")
                hours < 24 -> UPDATED.plus(" $hours hour ${minutes % 60} minutes ago")
                else -> dateDetails(it)
            }
        }
    }
    return ""
}

fun String.isEmptyOrIsBlank(): Boolean = this.isEmpty() || this.isBlank()


fun getSpannableString(
    data: String,
    color: String,
    position: Int,
    onClickListener: OnClickListener? = null
): SpannableString {
    return SpannableString(data).apply {
        if (color == COLOR_GREEN) {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#5cb85c")),
                16,
                data.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        onClickListener?.toArticleDetailsFragment(position)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.linkColor = R.color.colorPrimary
                    }
                },
                16,
                data.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            setSpan(
                ForegroundColorSpan(Color.parseColor(color)),
                15,
                data.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

fun View.getReadMoreString(stringId: Int): String {
    return this.context.resources.getString(stringId)
}

fun getEditable(text: String?): Editable {
    text?.let {
        return Editable.Factory.getInstance().newEditable(text)
    } ?: run {
        return Editable.Factory.getInstance().newEditable("")
    }
}

fun Context.createCustomDataStore(name: String): DataStore<Preferences> {
    return createDataStore(
        name = name
    )
}

suspend fun Context.writeToDataSource(status: Int) {
    val dataStore: DataStore<Preferences> = createCustomDataStore(SHOWCASE_DATA_SOURCE)
    dataStore.edit {
        it[preferencesKey<Int>(SHOWCASE_ID)] = status
    }
}

fun Context.readFromDataSource(): Flow<Int> {
    val dataStore: DataStore<Preferences> = createCustomDataStore(SHOWCASE_DATA_SOURCE)
    return dataStore.data.map {
        it[preferencesKey(SHOWCASE_ID)] ?: -1
    }
}

suspend fun Context.saveJwtToken(jwtToken: String) {
    val dataStore: DataStore<Preferences> = createCustomDataStore(JWT_DATA_SOURCE)
    dataStore.edit {
        it[preferencesKey<String>(JWT_TOKEN)] = jwtToken
    }
}

fun Context.readJwtToken(): Flow<String> {
    val dataStore: DataStore<Preferences> = createCustomDataStore(JWT_DATA_SOURCE)
    return dataStore.data.map {
        it[preferencesKey(JWT_TOKEN)] ?: ""
    }
}

fun Context.color(colorId: Int) = ContextCompat.getColor(this, colorId)

fun Context.drawable(drawableId: Int) = ContextCompat.getDrawable(this, drawableId)

fun RequestManager.setImage(imageUrl: String, target: ImageView) {
    if (imageUrl.isNotEmpty() || imageUrl.isNotBlank()) {
        load(imageUrl).centerCrop().error(R.drawable.default_user_avataar).into(target)
    } else {
        target.setImageResource(R.drawable.default_user_avataar)
    }
}

fun View.showMessage(message: String?) {
    message?.let {
        Snackbar.make(this, message, Snackbar.LENGTH_LONG).apply {
            setAction("Dismiss") {
                dismiss()
            }
        }.show()
    }
}