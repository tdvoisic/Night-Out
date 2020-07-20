package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth UserAuth;
    private Button LogOutTemp;

    private BottomNavigationView Nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserAuth = FirebaseAuth.getInstance();

        Nav = findViewById(R.id.main_bottom_nav);
        Nav.setSelectedItemId(R.id.nav_home);

        Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });


        LogOutTemp = (Button) findViewById(R.id.logout_temp);

        LogOutTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAuth.signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_discover:
                startActivity(new Intent(getApplicationContext(), DiscoverActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_messages:
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0,0);
                break;

        }
    }
}