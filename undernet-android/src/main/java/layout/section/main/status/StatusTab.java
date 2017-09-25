package layout.section.main.status;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import layout.section.main.Tab;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusTab extends Tab {


    public StatusTab() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
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
                    node.setAddress(address);
                    EntryNodeCache.addNode(node);
                }
            }
        });

        //Registering the clear cache button.
        Button clearCacheButton = (Button) view.findViewById(R.id.clear_node_cache);
        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnderNet.logger.info("Clicked on the clear node cache button.");
                //Clearing the node cache.
                EntryNodeCache.clear();
            }
        });

        //Registering local ip text.
        final TextView localIp = (TextView)view.findViewById(R.id.self_local_ip);
        /*try {
            localIp.setText("Local ip: " + Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/

        return view;
    }

    @Override
    public void OnVisible() {
        MainActivity.instance.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = MainActivity.instance.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }
    }

    @Override
    public void OnInvisible() {

    }
}
