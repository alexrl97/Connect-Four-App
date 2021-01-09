package app.connectfour.network;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import app.R;
import app.connectfour.gameEngine.Name;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class BluetoothMenu extends AppCompatActivity implements Start_Fragment.OnFragmentInteractionListener{

    FragmentManager fragmentManager;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        name = b.getString("name");
        setContentView(R.layout.main_menu);

        //fragment for start or join the game
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_menu, new Start_Fragment()).commit();
    }

    @Override
    public void onButtonSelected(int id) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        if (id == 1) { //server
            transaction.replace(R.id.main_menu, new Server_Fragment(name));
        } else if(id == 2){ //client
            transaction.replace(R.id.main_menu, new Client_Fragment(name));
        }
        else{
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( intent);
        }
        // and add the transaction to the back stack so the user can navigate back
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    // https://medium.com/@Wingnut/onbackpressed-for-fragments-357b2bf1ce8e
    @Override
    public void onBackPressed() {
        View bluetoothSelect = findViewById(R.id.bluetooth_select);
        if(isVisible(bluetoothSelect)){
            Intent i = new Intent(BluetoothMenu.this, Name.class);
            i.putExtra("id", 4);
            startActivity(i);
            BluetoothMenu.this.finish();
        }
        else{
            tellFragments();
            super.onBackPressed();
        }
    }

    // https://stackoverflow.com/questions/14039454/how-can-you-tell-if-a-view-is-visible-on-screen-in-android/41690874#:~:text=isShown()%20or%20view.,should%20take%20care%20of%20that.&text=The%20getVisibility()%20method%20will,the%20visibility%20of%20any%20View%20.
    public static boolean isVisible(final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        return actualPosition.intersect(screen);
    }

    private void tellFragments(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment f : fragments){
            if(f != null && f instanceof Base_Fragment)
                ((Base_Fragment)f).onBackPressed();
        }
    }
}
