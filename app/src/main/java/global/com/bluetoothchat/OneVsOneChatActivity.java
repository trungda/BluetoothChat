package global.com.bluetoothchat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class OneVsOneChatActivity extends Activity{
    private Button back;
    private Button send;
    private EditText msgContent;

    private ListView mListView;
    private ArrayList<MyDeviceItem> list;
    ListAdapter mAdapter;
    Context mContext;

    public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
    public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
    public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";

    private BluetoothServerSocket mServerSocket = null;
    private ServerThread startServerThread = null;
    private ClientThread clientConnectThread = null;
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private ReadThread mReadThread = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onevsonechat);
        mContext = this;
        init();
    }

    public void init() {
        back = (Button) findViewById(R.id.back);
        send = (Button) findViewById(R.id.send);
        msgContent = (EditText) findViewById(R.id.content);
        msgContent.clearFocus();
        list = new ArrayList<MyDeviceItem>();
        mAdapter = new ListAdapter(this, list);
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);

        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String content = msgContent.getText().toString();
                if (content.length() > 0) {
                    sendMessageHandle(content);
                    msgContent.setText("");
                    msgContent.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(msgContent.getWindowToken(), 0);
                } else {
                    Toast.makeText(mContext, "Message is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (OneVsOneFragment.serverOrClient == OneVsOneFragment.ServerOrClient.CLIENT) {
                    shutdownClient();
                } else if (OneVsOneFragment.serverOrClient == OneVsOneFragment.ServerOrClient.CLIENT) {
                    shutdownServer();
                }
                OneVsOneFragment.isOpen = false;
                OneVsOneFragment.serverOrClient = OneVsOneFragment.ServerOrClient.NONE;
                shutdownClient();
                shutdownServer();
                OneVsOneChatActivity.this.finish();
            }
        });
    }

    private Handler LinkDetectedHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                list.add(new MyDeviceItem((String) msg.obj, true));
            } else {
                list.add(new MyDeviceItem((String) msg.obj, false));
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(list.size() - 1);
        }
    };

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        OneVsOneFragment.isOpen = false;
        OneVsOneFragment.serverOrClient = OneVsOneFragment.ServerOrClient.NONE;
        shutdownClient();
        shutdownServer();
        this.finish();
        super.onDestroy();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (OneVsOneFragment.isOpen) {
            // Toast.makeText(mContext, "123", Toast.LENGTH_SHORT).show();
        }
        if (OneVsOneFragment.serverOrClient == OneVsOneFragment.ServerOrClient.CLIENT) {
            String address = OneVsOneFragment.BlueToothAddress;
            if (!address.equals("null")) {
                device = mBluetoothAdapter.getRemoteDevice(address);
                clientConnectThread = new ClientThread();
                clientConnectThread.start();
                OneVsOneFragment.isOpen = true;
            } else {
                Toast.makeText(mContext, "address is null!", Toast.LENGTH_SHORT).show();
            }
        } else if (OneVsOneFragment.serverOrClient == OneVsOneFragment.ServerOrClient.SERVICE) {
            startServerThread = new ServerThread();
            startServerThread.start();
            OneVsOneFragment.isOpen = true;
        }
    }

    private class ServerThread extends Thread {
        public void run() {
            try {
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                                PROTOCOL_SCHEME_RFCOMM,
                                UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
                Message msg = new Message();
                socket =OneVsOneFragment.socket;
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientThread extends Thread {
        public void run() {
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID
                        .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"));
                socket.connect();
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void sendMessageHandle(String msg) {
        if (socket == null) {
            Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
        }
        try {
            OutputStream out = socket.getOutputStream();
            out.write(msg.getBytes());
        } catch (Exception e) {
        }
        if (socket != null) {
            list.add(new MyDeviceItem(msg, false));
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size() - 1);
    }

    private class ReadThread extends Thread {
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream in = null;
            try {
                in = socket.getInputStream();
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


    private void shutdownServer() {
        new Thread() {
            public void run() {
                if (startServerThread != null) {
                    startServerThread.interrupt();
                    startServerThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                try {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                    if (mServerSocket != null) {
                        mServerSocket.close();
                        mServerSocket = null;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }.start();
    }


    private void shutdownClient() {
        new Thread() {
            public void run() {
                if (clientConnectThread != null) {
                    clientConnectThread.interrupt();
                    clientConnectThread = null;
                }
                if (mReadThread != null) {
                    mReadThread.interrupt();
                    mReadThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                        socket = null;
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }.start();
    }

}