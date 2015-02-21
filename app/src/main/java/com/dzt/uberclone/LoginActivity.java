package com.dzt.uberclone;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        Button login = (Button) findViewById(R.id.login_button);
        Button signup = (Button) findViewById(R.id.signup_button);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        */
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = new LoginSignupFragment();
        fragmentManager.beginTransaction().replace(R.id.login_container, fragment).addToBackStack("login/signup").commit();

    }

    public void onClick(View v)
    {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(v.getId())
        {
            default:
            case R.id.login_button:
                //Toast.makeText(this, "Login", Toast.LENGTH_SHORT).show();
                fragment = new LoginFragment();

                break;
            case R.id.signup_button:
                fragment = new SignupFragment();
                //Toast.makeText(this, "Signup", Toast.LENGTH_SHORT).show();
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.login_container, fragment).addToBackStack("signup/login")
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        }
        super.onBackPressed();
    }
}
