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

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OneVsOneFragment extends Fragment{
    private EditText contentEditText = null;
    private ListView chatListView = null;
    private List<ChatEntity> chatList = null;
    private ChatAdapter chatAdapter = null;
    private Button sendButton = null;
    private  View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_onevsone, container, false);

        contentEditText = (EditText) rootView.findViewById(R.id.et_sendmessage);
        sendButton = (Button) rootView.findViewById(R.id.btn_send);

        chatListView = (ListView) rootView.findViewById(R.id.listview);


        chatList = new ArrayList<ChatEntity>();
        ChatEntity chatEntity = null;
        for (int i = 0; i < 2; i++) {
            chatEntity = new ChatEntity();
            if (i % 2 == 0) {
                chatEntity.setComeMsg(false);
                chatEntity.setContent("Hello");
                chatEntity.setChatTime("2012-09-20 15:12:32");
            } else {
                chatEntity.setComeMsg(true);
                chatEntity.setContent("Hello,nice to meet you!");
                chatEntity.setChatTime("2012-09-20 15:13:32");
            }
            chatList.add(chatEntity);
        }

        chatAdapter = new ChatAdapter(getActivity(), chatList);
        chatListView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!contentEditText.getText().toString().equals("")) {
                    //send msg
                    send();
                } else {
                    Toast.makeText(getActivity(), "Content is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void send() {
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setChatTime("2012-09-20 15:16:34");
        chatEntity.setContent(contentEditText.getText().toString());
        chatEntity.setComeMsg(false);
        chatList.add(chatEntity);
        chatAdapter.notifyDataSetChanged();
        chatListView.setSelection(chatList.size() - 1);
        contentEditText.setText("");
    }

    private class ChatAdapter extends BaseAdapter {
        private Context context = null;
        private List<ChatEntity> chatList = null;
        private LayoutInflater inflater = null;
        private int COME_MSG = 0;
        private int TO_MSG = 1;

        public ChatAdapter(Context context, List<ChatEntity> chatList) {
            this.context = context;
            this.chatList = chatList;
            inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return chatList.size();
        }

        @Override
        public Object getItem(int position) {
            return chatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            // use which view
            ChatEntity entity = chatList.get(position);
            if (entity.isComeMsg()) {
                return COME_MSG;
            } else {
                return TO_MSG;
            }
        }

        @Override
        public int getViewTypeCount() {
            // the type of list view
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChatHolder chatHolder = null;
            if (convertView == null) {
                chatHolder = new ChatHolder();
                if (chatList.get(position).isComeMsg()) {
                    convertView = inflater.inflate(R.layout.chat_from_item, null);
                } else {
                    convertView = inflater.inflate(R.layout.chat_to_item, null);
                }
                chatHolder.timeTextView = (TextView) convertView.findViewById(R.id.tv_time);
                chatHolder.contentTextView = (TextView) convertView.findViewById(R.id.tv_content);
                chatHolder.userImageView = (ImageView) convertView.findViewById(R.id.iv_user_image);
                convertView.setTag(chatHolder);
            } else {
                chatHolder = (ChatHolder) convertView.getTag();
            }

            chatHolder.timeTextView.setText(chatList.get(position).getChatTime());
            chatHolder.contentTextView.setText(chatList.get(position).getContent());
            chatHolder.userImageView.setImageResource(chatList.get(position).getUserImage());

            return convertView;
        }

        private class ChatHolder {
            private TextView timeTextView;
            private ImageView userImageView;
            private TextView contentTextView;
        }
    }
}