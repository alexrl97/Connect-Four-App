package app.connectfour.gameEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.MainMenu;
import app.R;
import app.connectfour.gameActivity.Game_Activity_Offline;
import app.connectfour.network.BluetoothMenu;

public class Name extends Activity {

    private EditText name;
    private Button submit;
    private TextView playerToEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_name1);
        name = (EditText) findViewById(R.id.enterName);
        submit = (Button) findViewById(R.id.submit);
        Bundle b = getIntent().getExtras();
        final int id = b.getInt("id");

        playerToEnter = findViewById(R.id.playerToEnter);


        if(id == 4){
            playerToEnter = findViewById(R.id.playerToEnter);
            playerToEnter.setText("Enter your name");
        }

        if(id != 4) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String givenName = "";
                    Intent i = new Intent(Name.this, Game_Activity_Offline.class);
                    Intent i2 = new Intent(Name.this, Name2.class);
                    if (!name.getText().toString().equals("")) {
                        givenName = name.getText().toString();

                        if (id != 0) {
                            i.putExtra("id", id);
                            i.putExtra("name1", givenName);
                            startActivity(i);
                        } else {
                            i2.putExtra("id", id);
                            i2.putExtra("name1", givenName);
                            startActivity(i2);
                        }

                        Name.this.finish();
                    }
                }
            });
        }
        else{
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String givenName = "";
                    Intent i = new Intent(Name.this, BluetoothMenu.class);
                    if (!name.getText().toString().equals("")) {
                        givenName = name.getText().toString();
                            i.putExtra("name", givenName);
                            startActivity(i);
                        Name.this.finish();
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Name.this, MainMenu.class);
        startActivity(setIntent);
        Name.this.finish();
    }
}
