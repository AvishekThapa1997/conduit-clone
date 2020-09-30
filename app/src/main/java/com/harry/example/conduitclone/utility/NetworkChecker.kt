package com.harry.example.conduitclone.utility

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkChecker(context: Context) : LiveData<Boolean>() {
    private var connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback

    init {
        networkCallback = getNetworkCallBacks()
    }

    override fun onActive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerNetworkForAboveLollipop(networkCallback)
        } else {
            val networkRequest = getNetworkRequest()
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun registerNetworkForAboveLollipop(networkCallback: ConnectivityManager.NetworkCallback) {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }


    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun getNetworkRequest(): NetworkRequest {
        val networkRequestBuilder: NetworkRequest.Builder = NetworkRequest.Builder()
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        return networkRequestBuilder.build()
    }

    private fun getNetworkCallBacks(): ConnectivityManager.NetworkCallback {
        return object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(true)
            }

            override fun onLost(network: Network) {
                postValue(false)
            }

            override fun onUnavailable() {
                postValue(false)
            }
        }
    }
}