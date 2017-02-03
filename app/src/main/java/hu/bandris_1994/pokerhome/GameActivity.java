package hu.bandris_1994.pokerhome;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by András on 2016. 11. 21..
 */

public class GameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hosting = p.getBoolean("pref_host", false);
        if(hosting){
            HostConnection connection = new HostConnection();
            PokerTableModel table = PokerTableModel.getInstance();
            table.NewTable();
            PokerPlayerModel player = new PokerPlayerModel(1500, p.getString("pref_name","nonamefound"));
            table.PlayerSit(player);
            table.Play();
        }else{
            ClientConnection connection = new ClientConnection(p.getString("pref_name","nonamefound"));
        }
    }
}
