package com.dzt.uberclone;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.plus.PlusShare;
import com.facebook.Session;

import java.util.List;

/**
 * Created by David on 2/10/2015.
 */
public class ShareFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_share, container, false);

        Button gShare = (Button) v.findViewById(R.id.share_gplus_button);
        gShare.setOnClickListener(this);
        Button fbShare = (Button) v.findViewById(R.id.share_fb_button);
        fbShare.setOnClickListener(this);
        Button waShare = (Button) v.findViewById(R.id.share_wa_button);
        waShare.setOnClickListener(this);
        Button twitterShare = (Button) v.findViewById(R.id.share_twitter_button);
        twitterShare.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v)
    {
        String urlToShare = "UberClone.test";
        Intent intent;
        switch (v.getId()) {
            case R.id.share_gplus_button:
                /*
                Intent shareIntent = new PlusShare.Builder(getActivity())
                        .setType("text/plain")
                        .setText("Sharing on G+.")
                        .setContentUrl(Uri.parse(urlToShare))
                        .getIntent();

                startActivityForResult(shareIntent, 0);
                */

                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                intent.setPackage("com.google.android.apps.plus");

                startActivity(intent);
                break;

            case R.id.share_fb_button:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                intent.setPackage("com.facebook.katana");

                startActivity(intent);
                break;

            case R.id.share_wa_button:

                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                intent.setPackage("com.whatsapp");

                startActivity(intent);
                break;

            case R.id.share_twitter_button:

                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                intent.setPackage("com.twitter.android");

                startActivity(intent);
                break;
        }
    }
}