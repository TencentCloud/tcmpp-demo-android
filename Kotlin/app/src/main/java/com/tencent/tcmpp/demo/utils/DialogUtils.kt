package com.tencent.tcmpp.demo.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object DialogUtils {
    
    fun showDialog(context: Context, title: String, msg: String): AlertDialog {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton("Confirm") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .create()
        alertDialog.show()
        return alertDialog
    }

    fun showDialog(
        context: Context, 
        title: String, 
        msg: String, 
        positiveButton: String, 
        listener: DialogInterface.OnClickListener
    ): AlertDialog {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveButton, listener)
            .setCancelable(false)
            .create()
        alertDialog.show()
        return alertDialog
    }

    fun showDialog(
        context: Context, 
        title: String, 
        msg: String,
        positiveButton: String, 
        listener: DialogInterface.OnClickListener,
        negativeButton: String, 
        cancelListener: DialogInterface.OnClickListener
    ): AlertDialog {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(positiveButton, listener)
            .setNegativeButton(negativeButton, cancelListener)
            .setCancelable(false)
            .create()
        alertDialog.show()
        return alertDialog
    }
}