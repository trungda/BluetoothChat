package global.com.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * Created by dinhanhtrung on 5/25/15.
 */
public class BroadcastFragment extends Fragment {
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    private View rootView;
    private Button send;
    private EditText msgContent;

    private ListView mListView;
    private ArrayList<MyDeviceItem> mMessageList;
    ListAdapter mAdapter;
    Context mContext;

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler handler = null;
    private BluetoothServerSocket mServerSocket = null;

    private ServerThread mServerThread = null;
    private ReadThread mReadThread = null;
    private ArrayList<ClientThread> mClientThread = new ArrayList<>();
    private BluetoothSocket mSocket = null;
    private ArrayList<BluetoothSocket> mSocketList = new ArrayList<>();
    private ArrayList<String> mDeviceAddressees = new ArrayList<>();
    private Set<BluetoothDevice> pairedDevices;

    private ArrayList<UUID> mUuids = new ArrayList<UUID>();

    public static BroadcastFragment newInstance() {
        BroadcastFragment fragment = new BroadcastFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BroadcastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_broadcast, container, false);
        init();
        return rootView;
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        mServerThread = new ServerThread();
        mServerThread.start();
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    void init() {
        mContext = getActivity();
        send = (Button) rootView.findViewById(R.id.btn_send);
        msgContent = (EditText) rootView.findViewById(R.id.et_sendmessage);
        msgContent.clearFocus();
        mMessageList = new ArrayList<MyDeviceItem>();
        mAdapter = new ListAdapter(mContext, mMessageList);
        mListView = (ListView) rootView.findViewById(R.id.bc_listview);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);

        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBtAdapter.getBondedDevices();
        for (int i = 0; i < pairedDevices.size(); i ++) {
            mUuids.add(UUID.randomUUID());
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d("clicked", String.valueOf(mSocketList.size()));
                String content = msgContent.getText().toString();
                for (int i = 0; i < mSocketList.size(); i++) {
                    if (content.length() > 0) {
                        Log.d("device", mSocketList.get(i).toString());
                        sendMessageHandle(mSocketList.get(i), content);
                        msgContent.setText("");
                        msgContent.clearFocus();
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    } else {
                        Toast.makeText(mContext, "Message is empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendMessageHandle(BluetoothSocket socket, String msg) {
        if (socket == null) {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
        }
        try {
            OutputStream out = socket.getOutputStream();
            out.write(msg.getBytes());
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (socket != null) {
            mMessageList.add(new MyDeviceItem(msg, false));
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mMessageList.size() - 1);
    }

    private class ServerThread extends Thread {
        BluetoothServerSocket mServerSocket = null;

        public void run() {
            for (int i = 0; i < pairedDevices.size(); i++) {
                try {
                    mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                            PROTOCOL_SCHEME_RFCOMM, mUuids.get(i));
                    mSocket = mServerSocket.accept();
                    mSocketList.add(mSocket);
                    mDeviceAddressees.add(mSocket.getRemoteDevice().getAddress());

                    mReadThread = new ReadThread();
                    mReadThread.start();
                }catch(IOException e){
                    Log.d("Exception", "done");
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private UUID mmUuid;

        public ClientThread(BluetoothDevice device, UUID uuid) {
            mmDevice = device;
            BluetoothSocket socket = null;
            mmUuid = uuid;

            BluetoothSocket temp = null;
            try {
                temp = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = temp;
        }

        public void run() {
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                mmSocket.connect();
                mReadThread = new ReadThread();
                mReadThread.start();
            }
            catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReadThread extends Thread {

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream in = null;
            try {
                in = mSocket.getInputStream();
            } catch (Exception e) {
                // TODO: handle exception
            }
            while (true) {
                try {
                    if ((bytes = in.read(buffer)) > 0) {
                        byte[] buf = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf[i] = buffer[i];
                        }
                        String s = new String(buf);
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = 1;
                        LinkDetectedHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    try {
                        in.close();
                    } catch (Exception e2) {
                        // TODO: handle exception
                    }
                    break;
                }
            }
        }
    }

    private Handler LinkDetectedHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mMessageList.add(new MyDeviceItem((String) msg.obj, true));
            } else {
                mMessageList.add(new MyDeviceItem((String) msg.obj, false));
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mMessageList.size() - 1);
        }
    };
}
