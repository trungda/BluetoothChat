package global.com.bluetoothchat;

/**
 * Created by dinhanhtrung on 6/8/15.
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
    private ArrayList<?> list;
    private LayoutInflater mInflater;

    public ListAdapter(Context context, ArrayList<?> list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        MyDeviceItem item = (MyDeviceItem) list.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder(
                    (View) convertView.findViewById(R.id.list_child),
                    (TextView) convertView.findViewById(R.id.chat_msg));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item.isSiri) {
            viewHolder.child.setBackgroundResource(R.drawable.bubble_green);
        } else {
            viewHolder.child.setBackgroundResource(R.drawable.bubble_yellow);
        }
        viewHolder.msg.setText(item.message);

        return convertView;
    }

    class ViewHolder {
        protected View child;
        protected TextView msg;

        public ViewHolder(View child, TextView msg) {
            this.child = child;
            this.msg = msg;

        }
    }
}
