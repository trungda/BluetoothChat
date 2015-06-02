package global.com.bluetoothchat;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by dinhanhtrung on 5/25/15.
 */
public class BroadcastFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_broadcast, container, false);
        return rootView;
    }

    public void onCreateEditText(
            Bundle savedInstanceState) {
        EditText et = (EditText) getView().findViewById(R.id.et_sendmessage);
        String str = et.getText().toString();

        ListView tv1 = (ListView)getView().findViewById(R.id.listview);
        //  tv1.setTextAlignment();
    }
}
