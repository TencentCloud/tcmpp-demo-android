package com.tencent.tcmpp.demo.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tencent.tcmpp.demo.Constants
import com.tencent.tcmpp.demo.R
import com.tencent.tmfmini.sdk.launcher.utils.ProcessUtil

class TransActivity : BaseActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.applet_activity_tran)

        Log.i(Constants.LOG_TAG, "TransActivity:${ProcessUtil.getProcessName(this)}")

        val intent = Intent().apply {
            setClassName("com.tencent.tmf.demo", "com.tencent.tmf.module.main.activtiy.TMFMainActivity")
        }
        startActivityForResult(intent, 10000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.getStringExtra("test")?.let { 
            Log.i(Constants.LOG_TAG, it) 
        }
        
        val intent = Intent().apply {
            putExtra("key", "value")
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}