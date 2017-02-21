package me.matoosh.undernet.ui.view.section.main;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.UnknownHostException;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusTab extends Fragment implements ITab {


    public StatusTab() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_status, container, false);

        //Registering the connect button.
        Button b = (Button)view.findViewById(R.id.connect_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Starting the UnderNet connection process.
                UnderNet.connect();
            }
        });

        //Registering the add node button.
        Button addNodeButton = (Button) view.findViewById(R.id.add_node);
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnderNet.logger.info("Clicked on the add node button.");
                //Adding node to the node cache.
                Node node = new Node();
                InetAddress address;
                try {
                    address = InetAddress.getByName(((EditText)view.findViewById(R.id.nodeAddress)).getText().toString());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    address = null;
                }

                if(address != null) {
                    node.address = address;
                    NodeCache.addNode(node);
                }
            }
        });

        return view;
    }

    @Override
    public void OnCreate() {

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
