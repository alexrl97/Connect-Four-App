package app.connectfour.network;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import app.R;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class Client_Fragment extends Fragment implements OnBackPressed{
    String TAG = "client";
    TextView output, player_name;
    Button btn_start, btn_device, btn_send, btn_ready, change_name;
    BluetoothAdapter mBluetoothAdapter =null;
    BluetoothDevice device;
    FragmentManager fragmentManager = null;
    String name;

    private static BluetoothService mChatService = null;

    public Client_Fragment(String name) {
        this.name = name;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    output.append("Message from server: " + readMessage +"\n");

                    //when server have the symbol chosen
                    if(readMessage.equals("Server choosed RED color"))  {
                        output.append("playing YELLOW - agree?");
                        AlertDialog dialog = createDialog("YELLOW");
                        dialog.show();
                    }
                    if(readMessage.equals("Server choosed YELLOW color"))  {
                        output.append("playing RED - agree?");
                        AlertDialog dialog = createDialog("RED");
                        dialog.show();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    if (null != activity) {
                        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.client_fragment, container, false);

        player_name = (TextView) myView.findViewById(R.id.playerNameView);
        player_name.setText("Name: " + name);

        //output textview
        output = (TextView) myView.findViewById(R.id.ct_output);
        //buttons
        btn_device = (Button) myView.findViewById(R.id.device);
        btn_device.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                querypaired();
            }
        });
        btn_start = (Button) myView.findViewById(R.id.join);
        btn_start.setEnabled(false);
        btn_start.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                output.append("Starting client\n");
                startClient();
            }
        });

        btn_ready = (Button) myView.findViewById(R.id.ready);
        btn_ready.setEnabled(false);
        btn_ready.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessage("choosingDialogQuery");
            }
        });
        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            output.append("No bluetooth device.\n");
            btn_start.setEnabled(false);
            btn_device.setEnabled(false);
        }
        Log.v(TAG, "bluetooth");

        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();
        //creating a BluetoothService here
        if(mChatService == null) {
            mChatService = new BluetoothService(getActivity(), handler);
        }
        querypaired();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (mChatService != null) { mChatService.stop();} //we need it!
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }



    //setting the device
    public void querypaired() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            output.append("at least 1 paired device\n");
            final BluetoothDevice blueDev[] = new BluetoothDevice[pairedDevices.size()];
            String[] items = new String[blueDev.length];
            int i =0;
            for (BluetoothDevice devicel : pairedDevices) {
                blueDev[i] = devicel;
                items[i] = blueDev[i].getName() + ": " + blueDev[i].getAddress();
                output.append("Device: "+items[i]+"\n");
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                i++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Choose Bluetooth:");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    if (item >= 0 && item <blueDev.length) {
                        device = blueDev[item];
                        btn_device.setText("device: "+blueDev[item].getName());
                        btn_start.setEnabled(true);
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public AlertDialog createDialog(final String colorToPlay) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you agree to play for: " + colorToPlay + "?").setTitle("Player 1 choose");
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //ready to play
                sendMessage("client ready to play"); //for future mb

                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_menu, Game_Fragment_Online.newInstance(colorToPlay, false, name));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        builder.setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //declined
                sendMessage("client declined the game");
            }
        });
        return builder.create();
    }

    public void startClient() {
        if (device != null) {
            Log.v(TAG, "connecting with: " + device);
            Toast.makeText(getActivity(), "connecting...", Toast.LENGTH_SHORT).show();
            mChatService.connect(device);
            btn_ready.setEnabled(true);
            btn_ready.setTextColor(Color.parseColor("#4DB0FF"));
        } else
            Log.v(TAG, "device is null");
    }

    public void sendMessage(String msg) {
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        mChatService.write(msg.getBytes());
    }

    static public BluetoothService getBluetoothService() {
        //invoke it in Game_Fragment to get the connectedThread??
        return mChatService;
    }

    @Override
    public void onBackPressed(){
        getActivity().getSupportFragmentManager().popBackStack();
    }
}