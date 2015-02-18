package com.dzt.uberclone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by David on 2/17/2015.
 */
public class ConnectionDetector {
    private Context context;

    public ConnectionDetector(Context context)
    {
        this.context = context;
    }

    public boolean isConnectedToInternet()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null)
        {
            NetworkInfo [] ni = cm.getAllNetworkInfo();
            if(ni != null)
            {
                for(int i = 0; i < ni.length; i++)
                {
                    if (ni[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
