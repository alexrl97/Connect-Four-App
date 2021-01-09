package app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.connectfour.gameEngine.Name;

public class MainMenu extends AppCompatActivity {

    private final int onDeviceMode = 0;
    private final int easyMode = 1;
    private final int moderateMode = 2;
    private final int hardMode = 3;
    private final int bluetoothMode = 4;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        TextView game_name = (TextView) findViewById(R.id.textView);
        String name = getColoredSpanned("Connect", "#FF3333");
        String surName = getColoredSpanned("Four","#FFEB3B");
        game_name.setText(Html.fromHtml(name+" "+surName));

        Button onePlayer = (Button) findViewById(R.id.oneplayer);
        onePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOnePlayerOptions();
            }
        });

        Button twoPlayers = (Button) findViewById(R.id.twoplayers);
        twoPlayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTwoPlayersOptions();
            }
        });
    }

    //https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter
    public void displayOnePlayerOptions(){
        LayoutInflater inflater = getLayoutInflater();
        View OnePlayerLayout = inflater.inflate(R.layout.singleplayer_select, null);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(OnePlayerLayout);
        dialog.setCancelable(true);

        Button easyComputer = OnePlayerLayout.findViewById(R.id.easyMode);
        easyComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //change to game activity
                Intent i = new Intent(MainMenu.this, Name.class);
                i.putExtra("id", easyMode);
                startActivity(i);
                //CLose Menu
                MainMenu.this.finish();
                dialog.cancel();
            }
        });

        Button moderateComputer = OnePlayerLayout.findViewById(R.id.moderateMode);
        moderateComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //change to game activity
                Intent i = new Intent(MainMenu.this, Name.class);
                i.putExtra("id", moderateMode);
                startActivity(i);
                //CLose Menu
                MainMenu.this.finish();
                dialog.cancel();
            }
        });

        Button hardComputer = OnePlayerLayout.findViewById(R.id.hardMode);
        hardComputer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //change to game activity
                Intent i = new Intent(MainMenu.this, Name.class);
                i.putExtra("id", hardMode);
                startActivity(i);
                //CLose Menu
                MainMenu.this.finish();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void displayTwoPlayersOptions(){
        LayoutInflater inflater = getLayoutInflater();
        View TwoPlayersLayout = inflater.inflate(R.layout.multiplayer_select, null);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(TwoPlayersLayout);
        dialog.setCancelable(true);

        Button onDevice = TwoPlayersLayout.findViewById(R.id.onDevice);
        onDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //change to game activity
                Intent i = new Intent(MainMenu.this, Name.class);
                i.putExtra("id", onDeviceMode);
                startActivity(i);
                //CLose Menu
                MainMenu.this.finish();
                dialog.cancel();
            }
        });

        Button bluetoothButton = TwoPlayersLayout.findViewById(R.id.bluetooth_button);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenu.this, Name.class);
                i.putExtra("id", bluetoothMode);
                startActivity(i);
                MainMenu.this.finish();
            }
        });

        dialog.show();
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
}