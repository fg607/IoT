package com.hardwork.fg607.iot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hardwork.fg607.iot.model.TimeTask;
import com.hardwork.fg607.iot.view.fragment.MainFragment;
import com.hardwork.fg607.iot.view.fragment.TaskSetFragment;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MainFragment fragment = new MainFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.replace(R.id.container,fragment,"MainFragment").commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();

        Fragment taskSetFragment = fm.findFragmentByTag("TaskSetFragment");
        Fragment mainFragment = fm.findFragmentByTag("MainFragment");

        if(taskSetFragment!= null){

            fm.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .remove(taskSetFragment)
                    .show(mainFragment).commitAllowingStateLoss();
        }else {

            super.onBackPressed();
        }

    }
}
