package global.com.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button goButton = (Button)findViewById(R.id.go_button);
        final EditText deviceName = (EditText)findViewById(R.id.device_name);
        goButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newDeviceName = deviceName.getText().toString();
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        Log.v("LOG", "localdevicename : "+ mBluetoothAdapter.getName()+" localdeviceAddress : " + mBluetoothAdapter.getAddress());
                        mBluetoothAdapter.setName(newDeviceName);
                        Log.v("LOG", "localdevicename : " + mBluetoothAdapter.getName() + " localdeviceAddress : " + mBluetoothAdapter.getAddress());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
