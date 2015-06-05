package global.com.bluetoothchat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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


        SimpleAdapter adapter = new SimpleAdapter(getActivity(),getData(),R.layout.list2,
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
