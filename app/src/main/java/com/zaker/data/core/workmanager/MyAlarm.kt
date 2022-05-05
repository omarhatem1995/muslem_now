package com.zaker.data.core.workmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class MyAlarm: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        if (p1 != null) {
            if (p1.action == "Trigger") {



            }else if (p1.action == "off")
            {

            }
        }
    }
}