package com.knightua.notepadapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.knightua.notepadapp.utils.NetworkUtil

class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val status: Int = NetworkUtil.getConnectivityStatus(it)

            if (networkReceiverListener != null) {
                networkReceiverListener?.onNetworkConnectionChanged(status != NetworkUtil.TYPE_NOT_CONNECTED)
            }
        }
    }

    companion object {
        var networkReceiverListener: NetworkReceiverListener? = null
        fun isConnected(context: Context?): Boolean {
            context?.let {
                val status: Int = NetworkUtil.getConnectivityStatus(it)
                return status != NetworkUtil.TYPE_NOT_CONNECTED
            }
            return false
        }
    }

    interface NetworkReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }
}