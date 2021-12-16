package com.pdf.utils

import android.content.Context
import android.widget.Toast


class Utils(private val mContext: Context) {


    fun showToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

}