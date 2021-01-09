package app.connectfour.gameEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import app.R;
import app.connectfour.gameActivity.Game_Activity_Offline;

public class Name2 extends Activity {

    private EditText name2;
    private Button submit2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enter_name2);
        name2 = (EditText) findViewById(R.id.enterName2);
        submit2 = (Button) findViewById(R.id.submit2);
        Bundle b = getIntent().getExtras();
        final int id = b.getInt("id");
        final String givenName1 = b.getString("name1");

        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenName2 = "";
                Intent i = new Intent(Name2.this, Game_Activity_Offline.class);
                if (!name2.getText().toString().equals("") && ! givenName2.equals(givenName1)) {
                    givenName2 = name2.getText().toString();
                    i.putExtra("id", id);
                    i.putExtra("name1", givenName1);
                    i.putExtra("name2", givenName2);
                    startActivity(i);
                    Name2.this.finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Name2.this, Name.class);
        i.putExtra("id", 0);
        startActivity(i);
        //CLose Menu
        Name2.this.finish();
    }
}