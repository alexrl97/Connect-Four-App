package app.connectfour.gameActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.MainMenu;
import app.connectfour.gameEngine.ConnectFourGame;
import app.R;

public class Game_Activity_Offline extends Activity implements View.OnClickListener {

    private ConnectFourGame fc;

    private Button[] buttons = new Button[7];
    private Button gameReset;
    private Button mainMenu;

    private TextView scorePlayer1;
    private TextView scorePlayer2;

    public static int[] lastSet = new int[2];
    public static int[] lastWin = new int[8];

    //https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_view);

        Bundle b = getIntent().getExtras();
        int id = b.getInt("id");

        if(id == 0){
            fc= new ConnectFourGame(false);
            String name2 = b.getString("name2");
            fc.setPlayersName(name2, 2);
        }
        else
            fc= new ConnectFourGame(id);

        String name1 = b.getString("name1");
        fc.setPlayersName(name1, 1);

        scorePlayer1 = findViewById(R.id.scorep1);
        scorePlayer2 = findViewById(R.id.scorep2);
        updateGUIScoreboard();
        gameReset = (Button) findViewById(R.id.button_reset);
        gameReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
        mainMenu = (Button) findViewById(R.id.button_menu);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });

        for(int i = 0; i < 7; i++){
            String buttonID = "button_put" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);
            buttons[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_put0: set(0); break;
            case R.id.button_put1: set(1); break;
            case R.id.button_put2: set(2); break;
            case R.id.button_put3: set(3); break;
            case R.id.button_put4: set(4); break;
            case R.id.button_put5: set(5); break;
            case R.id.button_put6: set(6); break;
        }
    }

    //https://stackoverflow.com/questions/15874117/how-to-set-delay-in-android
    public void set(int x){
        int currentPlayerOneScore = Integer.parseInt(fc.getScoreFromPlayer(1));
        int currentPlayerTwoScore = Integer.parseInt(fc.getScoreFromPlayer(2));

        boolean win = fc.set(x);
        if(win){
            Toast toast;
            if(currentPlayerOneScore < Integer.parseInt(fc.getScoreFromPlayer(1)) && currentPlayerTwoScore < Integer.parseInt(fc.getScoreFromPlayer(2))){
                toast= Toast.makeText(getApplicationContext(), " DRAW!", Toast.LENGTH_SHORT);
            }
            else if(currentPlayerOneScore < Integer.parseInt(fc.getScoreFromPlayer(1))) {
                toast= Toast.makeText(getApplicationContext(), fc.getPlayersname(1) + " WINS!", Toast.LENGTH_SHORT);
            }
            else {
                toast= Toast.makeText(getApplicationContext(), fc.getPlayersname(2) + " WINS!", Toast.LENGTH_SHORT);
            }
            toast.show();
            updateGUIScoreboard();
            updateGUIMatchfield();
            highlightWinInGUI(lastWin);

            enableButtons(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fc.resetMatchfield();
                    updateGUIMatchfield();
                    if(!fc.getComputerWon() && !fc.getOnDeviceEnabled() ) {
                        fc.doFirstComputerSet();
                    }
                    updateGUIMatchfield();
                    enableButtons(true);
                }
            }, 3000);
        }
        else
            highlightSetInGUI(lastSet[0], lastSet[1], 1);
        updateGUIMatchfield();
    }

    // https://stackoverflow.com/questions/36747369/how-to-show-a-pop-up-in-android-studio-to-confirm-an-order
    public void resetGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Reset");
        builder.setMessage("Do you want to reset the game?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fc.reset();
                        updateGUIMatchfield();
                        updateGUIScoreboard();
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateGUIMatchfield();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateGUIMatchfield(){
        for (int x = 0; x < 7; x++)
            for (int y = 0; y < 6; y++) {
                String buttonID = "matchfield_" + x + y;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                TextView t = findViewById(resID);
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

    public void backToMainMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Main menu");
        builder.setMessage("Do you want to go back to the main menu?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Game_Activity_Offline.this, MainMenu.class);
                        startActivity(i);
                        Game_Activity_Offline.this.finish();
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

    // https://stackoverflow.com/questions/35648797/disable-button-on-an-android-app-for-a-specific-period-of-time
    public void enableButtons(boolean enable){
        for(int i = 0; i < 7; i++){
            buttons[i].setEnabled(enable);
        }
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
        int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
        final TextView t = findViewById(resID);

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
