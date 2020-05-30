package com.dj.challenge.currenyconverter.util

import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import androidx.annotation.VisibleForTesting

interface AlertActions {
    fun showLoading()
    fun hideLoading()
    fun showToast(message: String)
}

class AlertActionsImpl(
    private val context: Context,
    private val progressDialog: ProgressDialog = ProgressDialog(context).apply { setMessage("Loading..") }
) : AlertActions {

    override fun showLoading() {
        progressDialog.show()
    }

    override fun hideLoading() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}