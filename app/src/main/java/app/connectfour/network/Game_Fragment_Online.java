package app.connectfour.network;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.MainMenu;
import app.R;
import app.connectfour.gameEngine.ConnectFourGame;


public class Game_Fragment_Online extends Fragment implements OnBackPressed{

    private ConnectFourGame fc;
    private Button[] buttons = new Button[7];
    private Button gameReset;
    private Button mainMenu;

    private TextView scorePlayer1;
    private TextView scorePlayer2;

    private AlertDialog dialog;

    public static int[] lastSet = new int[2];
    public static int[] lastWin = new int[8];

    BluetoothService mConnectedThread = null;
    public String turn = "RED";
    private String myColor;
    public static String COLOR_CHOOSEN = "COLOR_CHOOSEN";
    public static String NAME_ENTERED = "NAME_ENTERED";
    public static String myName;
    private static String IS_SERVER;

    private final String NAME = "name";
    private final String RESETREQUEST = "resetRequest";
    private final String RESETCONFIRMATION = "resetConfirmation";
    private final String MATCHFIELDRESET = "matchfieldReset";
    private final String SCOREBOARDCHANGE = "scoreboardChange";

    public static final Game_Fragment_Online newInstance(String Color, boolean server, String name) {
        Game_Fragment_Online game = new Game_Fragment_Online();
        Bundle bdl = new Bundle(2);
        bdl.putString(COLOR_CHOOSEN, Color);
        bdl.putBoolean(IS_SERVER, server);
        bdl.putString(NAME_ENTERED, name);
        game.setArguments(bdl);
        return game;
    }

    public Game_Fragment_Online() {
        // Required empty public constructor
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                //assume already connected
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    int x = -1;
                    if(readMessage.length() == 1)
                        x = readMessage.codePointAt(0) - 48;
                    if(x >= 0 && x < 7) {
                        set(x, false);
                        updateGUIMatchfield();
                        enableButtons(true);
                        disableButtonsIfColumnIsFull();
                    }
                    if(readMessage.startsWith(NAME)){
                        String opponentName = readMessage.substring(4);
                        if(fc.players.getMyPlayerNumber() == 1)
                            fc.setPlayersName(opponentName, 2);
                        else
                            fc.setPlayersName(opponentName, 1);

                        if(scorePlayer1 != null && scorePlayer2 != null)
                            updateGUIScoreboard();
                    }

                    if(readMessage.equals(RESETREQUEST)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setCancelable(true);
                        builder.setTitle("Reset game");
                        builder.setMessage("Opponent wants to reset the game - do you agree?");
                        builder.setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fc.reset();
                                        updateGUIMatchfield();
                                        updateGUIScoreboard();
                                        handleResetConfirmation();
                                    }
                                });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    if(readMessage.equals(RESETCONFIRMATION)){
                        fc.reset();
                        updateGUIMatchfield();
                        updateGUIScoreboard();
                    }

                    if(readMessage.equals(MATCHFIELDRESET))
                        fc.resetMatchfield();

                    if(readMessage.equals(SCOREBOARDCHANGE)){
                        fc.updateScoreboard(fc.players.getOtherPlayerNumber());
                        updateGUIScoreboard();
                        Toast toast;
                        toast = Toast.makeText(getActivity().getApplicationContext(), fc.getPlayersname(fc.players.getOtherPlayerNumber()) + " WINS!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isServer;
        myColor = getArguments().getString(COLOR_CHOOSEN);
        isServer = getArguments().getBoolean(IS_SERVER);
        myName = getArguments().getString(NAME_ENTERED);
        if(isServer) {
            mConnectedThread = Server_Fragment.getBluetoothService(); }
        else {
            mConnectedThread = Client_Fragment.getBluetoothService(); }

        mConnectedThread.putNewHandler(handler);

        fc = new ConnectFourGame(true);


        Toast toast;
        if(turn.equals(myColor) && myName!= "") {
            fc.setPlayersName(myName, 1);
            fc.setPlayerOneOnline(true);
            toast = Toast.makeText(getActivity().getApplicationContext(), " Your turn!", Toast.LENGTH_SHORT);
        }
        else if(myName!= "") {
            fc.setPlayersName(myName, 2);
            fc.setPlayerOneOnline(false);
            toast = Toast.makeText(getActivity().getApplicationContext(), " Opponents turn...", Toast.LENGTH_SHORT);
        }
        handleName(myName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.game_view, container, false);

        scorePlayer1 = (TextView) myView.findViewById(R.id.scorep1);
        scorePlayer2 = (TextView) myView.findViewById(R.id.scorep2);
        updateGUIScoreboard();

        mainMenu = (Button) myView.findViewById(R.id.button_menu);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });

        gameReset = (Button) myView.findViewById(R.id.button_reset);
        gameReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReset();
            }
        });

        for(int i = 0; i < 7; i++){
            String buttonID = "button_put" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
            buttons[i] = (Button) myView.findViewById(resID);
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.button_put0: set(0, true); handleSet("0"); break;
                        case R.id.button_put1: set(1, true); handleSet("1"); break;
                        case R.id.button_put2: set(2, true); handleSet("2"); break;
                        case R.id.button_put3: set(3, true); handleSet("3"); break;
                        case R.id.button_put4: set(4, true); handleSet("4"); break;
                        case R.id.button_put5: set(5, true); handleSet("5"); break;
                        case R.id.button_put6: set(6, true); handleSet("6"); break;
                    }
                }
            });
        }

        if(!turn.equals(myColor))
            enableButtons(false);

        return myView;
    }

    public void set(int x, boolean sender){

        boolean win = fc.set(x);

        if(win) {
            Toast toast;
            if (fc.matchfieldIsFull()) {
                toast = Toast.makeText(getActivity().getApplicationContext(), " DRAW!", Toast.LENGTH_SHORT);
                toast.show();
                enableButtons(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fc.resetMatchfield();
                        handleMatchfieldReset();
                        updateGUIMatchfield();

                    }
                }, 3000);

            } else {
                updateGUIScoreboard();
                updateGUIMatchfield();

                enableButtons(false);
                highlightWinInGUI(lastWin);

                if (sender) {
                    fc.updateScoreboard(fc.players.getMyPlayerNumber());
                    handleScoreBoard();
                    toast = Toast.makeText(getActivity().getApplicationContext(), fc.getPlayersname(fc.players.getMyPlayerNumber()) + " WINS!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fc.resetMatchfield();
                        handleMatchfieldReset();
                        updateGUIMatchfield();
                    }
                }, 3000);
            }
        }
        else
            highlightSetInGUI(lastSet[0], lastSet[1], 1);

        updateGUIScoreboard();
        updateGUIMatchfield();
        disableButtonsIfColumnIsFull();
    }

    public void handleSet(String column){
            mConnectedThread.write(column.getBytes());
            enableButtons(false);
    }
    public void handleName(final String myName){

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String message = NAME + myName;
                mConnectedThread.write(message.getBytes());
            }
        }, 3000);
    }
    public void handleReset(){
        mConnectedThread.write(RESETREQUEST.getBytes());
    }
    public void handleResetConfirmation(){
        mConnectedThread.write(RESETCONFIRMATION.getBytes());
    }
    public void handleMatchfieldReset(){
        mConnectedThread.write(MATCHFIELDRESET.getBytes());
    }

    public void handleScoreBoard(){
        mConnectedThread.write(SCOREBOARDCHANGE.getBytes());
    }

    public void updateGUIMatchfield(){
        for (int x = 0; x < 7; x++)
            for (int y = 0; y < 6; y++) {
                String buttonID = "matchfield_" + x + y;
                int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
                TextView t = getActivity().findViewById(resID);
                if(fc.getMatchfield().getFieldValue(x,y) == 0)
                    t.setText("");
                else if(fc.getMatchfield().getFieldValue(x,y) == 1) {
                    t.setTextColor(Color.parseColor("#FF3333"));
                    t.setText("o");
                }
                else {
                    t.setTextColor(Color.parseColor("#FFEB3B"));
                    t.setText("o");
                }
                t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50.f);
            }
    }

    public void updateGUIScoreboard(){
        scorePlayer1.setText(String.format("%-8s: %s", fc.getPlayersname(1), fc.getScoreFromPlayer(1)));
        scorePlayer2.setText(String.format("%-8s: %s", fc.getPlayersname(2), fc.getScoreFromPlayer(2)));
    }

    // https://stackoverflow.com/questions/35648797/disable-button-on-an-android-app-for-a-specific-period-of-time
    public void enableButtons(final boolean enable){
        if(fc.win() && enable){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 7; i++) {
                        buttons[i].setEnabled(true);
                        setColorOfButtonText(i, true);
                    }
                }
            }, 3000);
        }
        else
            for(int i = 0; i < 7; i++) {
                buttons[i].setEnabled(enable);
                setColorOfButtonText(i, enable);
            }
    }

    public void disableButtonsIfColumnIsFull(){
        for(int x = 0; x < 7; x++)
            if(fc.columnIsFull(x)) {
                buttons[x].setEnabled(false);
                setColorOfButtonText(x, false);
            }
    }

    public void setColorOfButtonText(int i, boolean enable){
        if(enable){
            buttons[i].setTextColor(Color.parseColor("#EFE9E9"));
        }
        else
            buttons[i].setTextColor(Color.parseColor("#272727"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnectedThread != null) {
            mConnectedThread.stop();
        }
    }


    public AlertDialog createDialog(final String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Winner is: " + winner);
        builder.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), "new game", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }

    public void backToMainMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setCancelable(true);
        builder.setTitle("Main menu");
        builder.setMessage("Do you want to go back to the main menu?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), MainMenu.class);
                        startActivity(i);
                        getActivity().finish();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void registerLastSetPosition(int x, int y){
        lastSet[0] = x;
        lastSet[1] = y;
    }

    public static void registerLastWinPositions(int values[]){
        lastWin = values;
    }

    public void highlightSetInGUI(int x, int y, int seconds) {
        String buttonID = "matchfield_" + x + y;
        int resID = getResources().getIdentifier(buttonID, "id", getActivity().getPackageName());
        final TextView t = getActivity().findViewById(resID);

        t.setBackgroundResource(R.drawable.textview_border_green);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.setBackgroundResource(R.drawable.textview_border);
            }
        }, seconds*1000);
    }

    public void highlightWinInGUI(int values[]){
        highlightSetInGUI(values[0], values[1] ,3);
        highlightSetInGUI(values[2], values[3], 3);
        highlightSetInGUI(values[4], values[5], 3);
        highlightSetInGUI(values[6], values[7],3);
    }

    @Override
    public void onBackPressed() {
        backToMainMenu();
    }
}
