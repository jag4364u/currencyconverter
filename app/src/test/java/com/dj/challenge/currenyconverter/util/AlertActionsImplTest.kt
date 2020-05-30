package com.dj.challenge.currenyconverter.util

import android.app.ProgressDialog
import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test

class AlertActionsImplTest {

    private val context = mock<Context>()
    private val progressDialog = mock<ProgressDialog>()

    val subject = AlertActionsImpl(context, progressDialog)

    @Test
    fun testShowLoading() {
        subject.showLoading()
        verify(progressDialog).show()
    }

    @Test
    fun testHideLoadingIsShowing() {
        whenever(progressDialog.isShowing).thenReturn(true)
        subject.hideLoading()
        verify(progressDialog).dismiss()
    }

    @Test
    fun testHideLoadingWhenNotShowing() {
        whenever(progressDialog.isShowing).thenReturn(false)
        subject.hideLoading()
        verify(progressDialog, never()).dismiss()
    }
}