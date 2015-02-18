package com.dzt.uberclone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by David on 2/17/2015.
 */
public class AlertDialogManager {
    public void showAlertDialog(Context context, String title, String message, Boolean status)
    {
        AlertDialog ad = new AlertDialog.Builder(context).create();
        ad.setTitle(title);
        ad.setMessage(message);
        if(status != null)
        {
            ad.setIcon((status) ? R.drawable.success_icon : R.drawable.fail_icon);
        }
        ad.setButton(DialogInterface.BUTTON_POSITIVE, "OK",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface di, int which)
            {

            }
        });
        ad.show();
    }
}
