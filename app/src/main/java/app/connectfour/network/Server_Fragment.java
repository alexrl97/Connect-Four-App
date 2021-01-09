package app.connectfour.network;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Server_Fragment extends Fragment implements OnBackPressed{

    FragmentManager fragmentManager;
    private static String TAG = "server";
    private static BluetoothService mChatService = null;

    BluetoothAdapter mBluetoothAdapter = null;

    TextView output, player_name;
    Button btn_start, change_name;

    String name;

    public Server_Fragment(String name) {
        this.name = name;
    }

    //TODO: put it in the separate class to avoid repetition?
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

                    output.append("Message from server: " + readMessage + "\n");
                    //in the beginning of game query sent
                    if(readMessage.equals("choosingDialogQuery"))  {
                        AlertDialog dialog = createDialog();
                        dialog.show();
                        output.append("dialog box created - starting the game");
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    //TODO: save the connected device's name
                    //mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected ", Toast.LENGTH_SHORT).show();
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
    public void onStart() {
        super.onStart();
        //creating a BluetoothService here
        if(mChatService == null) {
            mChatService = new BluetoothService(getActivity(), handler);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.server_fragment, container, false);
        //text field for output info.
        output = (TextView) myView.findViewById(R.id.sv_output);
        player_name = (TextView) myView.findViewById(R.id.playerNameView);
        player_name.setText("Name: " + name);

        //setup the bluetooth adapter.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            output.append("No bluetooth device.\n");
            btn_start.setEnabled(false);
        }
        return myView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            //not closing it for using in Game_Fragment
            //mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth service
                mChatService.start();
            }
        }
    }

    public AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Choose your color: ").setTitle("The player 2 is ready");
        builder.setPositiveButton("RED", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendMessage("Server choosed RED color");

                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_menu, Game_Fragment_Online.newInstance("RED", true, name));
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(getActivity(), "RED color was choosen", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("YELLOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User choose to play O
                sendMessage("Server choosed YELLOW color");
                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_menu, Game_Fragment_Online.newInstance("YELLOW", true, name));
                transaction.addToBackStack(null);
                transaction.commit();
                Toast.makeText(getActivity(), "YELLOW color was choosen", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
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

