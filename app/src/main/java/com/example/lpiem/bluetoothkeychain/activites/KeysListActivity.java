package com.example.lpiem.bluetoothkeychain.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lpiem.bluetoothkeychain.R;


public class KeysListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keys_list);
        setTitle("Liste de clés");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_keys_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_add) {
            Intent intent = new Intent(KeysListActivity.this, AddKeyActivity.class);
            KeysListActivity.this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    public void onStart() {
        super.onStart();
    }


}
