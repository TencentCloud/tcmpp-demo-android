package com.tencent.tcmpp.demo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DialogUtils {
    public static AlertDialog showDialog(Context context, String title, String msg) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showDialog(Context context, String title, String msg, String positiveButton, DialogInterface.OnClickListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, listener)
                .setCancelable(false)
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showDialog(Context context, String title, String msg,
                                         String positiveButton, DialogInterface.OnClickListener listener,
                                         String negativeButton, DialogInterface.OnClickListener cancelListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positiveButton, listener)
                .setNegativeButton(negativeButton, cancelListener)
                .setCancelable(false)
                .create();
        alertDialog.show();
        return alertDialog;
    }
}
