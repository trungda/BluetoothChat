package global.com.bluetoothchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import android.os.Looper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OneVsOneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneVsOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneVsOneFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
    static enum ServerOrClient {
        NONE, SERVICE, CLIENT
    };

    private BluetoothServerSocket mserverSocket = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
            .getDefaultAdapter();
    static BluetoothSocket socket = null;
    private serverThread startServerThread = null;

    static String BlueToothAddress = "null";
    static ServerOrClient serviceOrCilent = ServerOrClient.NONE;
    static boolean isOpen = false;

    private ListView mListView;
    private ArrayList<MyDeviceItem> list;
    private Button searchButton;

    ListAdapter mAdapter;
    Context mContext;
    View rootView;

    private BluetoothAdapter mBtAdapter;
    boolean isBTOpen;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OneVsOneFragment.
     */
    public static OneVsOneFragment newInstance() {
        OneVsOneFragment fragment = new OneVsOneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OneVsOneFragment() {


        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_one_vs_one, container, false);
        init();

        startServerThread = new serverThread();
        startServerThread.start();

        return rootView;
    }

    private void init() {




        mContext = getActivity();

        list = new ArrayList<MyDeviceItem>();
        mAdapter = new ListAdapter(mContext, list);
        mBtAdapter =  BluetoothAdapter.getDefaultAdapter();

        mListView = (ListView)rootView.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        mListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter discoveryFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, discoveryFilter);

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                list.add(new MyDeviceItem(device.getName() + "\n"
                        + device.getAddress(), true));
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(list.size() - 1);
            }
        } else {
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(list.size() - 1);
        }





        searchButton = (Button) rootView.findViewById(R.id.startSearch);
                searchButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {



                        isBTOpen = mBtAdapter.isEnabled();
                        if (isBTOpen) {
                            if (mBtAdapter.isDiscovering()) {
                                mBtAdapter.cancelDiscovery();
                            } else {
                                list.clear();
                                mAdapter.notifyDataSetChanged();
                                Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
                                if (pairedDevices.size() > 0) {
                                    for (BluetoothDevice device : pairedDevices) {
                                        list.add(new MyDeviceItem(device.getName() + "\n"
                                                + device.getAddress(), true));
                                        mAdapter.notifyDataSetChanged();
                                        mListView.setSelection(list.size() - 1);
                                    }
                                }
                                else {
                                    list.add(new MyDeviceItem("No devices have been paired", true));
                                    mAdapter.notifyDataSetChanged();
                                    mListView.setSelection(list.size() - 1);
                                }
                                mBtAdapter.startDiscovery();
                            }
                        } else {
                            Toast.makeText(mContext, "Log: ", Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            MyDeviceItem item = list.get(arg2);
            String info = item.message;
            String address = info.substring(info.length() - 17);
            BlueToothAddress = address;

            AlertDialog.Builder StopDialog = new AlertDialog.Builder(mContext);
            StopDialog.setMessage(item.message);
            StopDialog.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            isBTOpen = mBtAdapter.isEnabled();
                            if (isBTOpen) {
                                mBtAdapter.cancelDiscovery();
                                searchButton.setText("no");
                                serviceOrCilent = ServerOrClient.CLIENT;
                                Intent intent = new Intent();
                                intent.setClass(mContext, OneVsOneChatActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(mContext, "Log: ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            StopDialog.setNegativeButton("no",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            BlueToothAddress = null;
                        }
                    });
            StopDialog.show();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    list.add(new MyDeviceItem(device.getName() + "\n"
                            + device.getAddress(), false));
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size() - 1);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mListView.getCount() == 0) {
                    list.add(new MyDeviceItem("1235", false));
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size() - 1);
                }
            }
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class serverThread extends Thread {
        public void run() {
            Looper.prepare();
            try {

                mserverSocket = mBluetoothAdapter
                        .listenUsingRfcommWithServiceRecord(
                                PROTOCOL_SCHEME_RFCOMM,
                                UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
                socket = mserverSocket.accept();
                Log.v("str","yao1111");
                if(socket!=null)
                {

                    Log.v("str","yao");
                    AlertDialog.Builder StopDialog = new AlertDialog.Builder(mContext);
                    StopDialog.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub


                                    isBTOpen = mBtAdapter.isEnabled();
                                    if (isBTOpen) {
                                        Log.v("str","yao1");

                                        //   mBtAdapter.cancelDiscovery();
                                        serviceOrCilent = ServerOrClient.SERVICE;
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, OneVsOneChatActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(mContext, "Log: ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                    StopDialog.setNegativeButton("no",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    BlueToothAddress = null;
                                }
                            });
                    StopDialog.show();
                }
                else{
                    Log.v("yao","yu");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            Looper.loop();
        }
    }
}
