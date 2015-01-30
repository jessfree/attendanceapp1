// IMyAidlInterfaceNOTIFICATIONS.aidl
package com.example.freeneyj.attendance2;

import android.app.Notification;

// Declare any non-default types here with import statements

oneway interface IMyAidlInterfaceNOTIFICATIONS {

    void notify(String packageName, int id, String tag, in Notification notification);

    /**
     * Cancel an already-notified notification.
     */
    void cancel(String packageName, int id, String tag);

    /**
     * Cancel all notifications for the given package.
     */
    void cancelAll(String packageName);
}





