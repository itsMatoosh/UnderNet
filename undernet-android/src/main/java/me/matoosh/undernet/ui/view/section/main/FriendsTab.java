package me.matoosh.undernet.ui.view.section.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.UnderNet;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsTab extends Fragment implements ITab {


    public FriendsTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void OnCreate() {
        //Registering the connect button.
        Button b = (Button)MainActivity.instance.findViewById(R.id.connect_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting the UnderNet connection process.
                UnderNet.connect();
            }
        });
    }

    @Override
    public void OnVisible() {
        MainActivity.instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = MainActivity.instance.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void OnInvisible() {
        MainActivity.instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
