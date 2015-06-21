package global.com.bluetoothchat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by dinhanhtrung on 5/25/15.
 */
public class BroadcastFragment extends Fragment {
    private  View rootView;

    public static BroadcastFragment newInstance() {
        BroadcastFragment fragment = new BroadcastFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BroadcastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_broadcast, container, false);
        ListView tv1 = (ListView) rootView.findViewById(R.id.listview);

        SimpleAdapter adapter = new SimpleAdapter(getActivity(),getData(), R.layout.broadcast_list,
                new String[]{"title","info","img"},
                new int[]{R.id.title,R.id.info,R.id.img});

        tv1.setAdapter(adapter);
        return rootView;
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "Device1");
        map.put("info", "hello ");
        map.put("img", R.drawable.i1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Device1");
        map.put("info", "hello");
        map.put("img", R.drawable.i1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Device1");
        map.put("info", "hiiii");
        map.put("img", R.drawable.i1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "Device1");
        map.put("info", "hiiii");
        map.put("img", R.drawable.i1);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("title", "Device1");
        map.put("info", "hiiii");
        map.put("img", R.drawable.i1);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("title", "Device1");
        map.put("info", "hiiii");
        map.put("img", R.drawable.i1);
        list.add(map);

        return list;
    }









}
