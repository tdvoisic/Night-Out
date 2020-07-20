package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth UserAuth;
    private DatabaseReference UsersRef, PostsRef, LikesRef;

    private ImageButton AddPostButton;

    private BottomNavigationView Nav;
    private Toolbar TopToolBar;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserAuth = FirebaseAuth.getInstance();
        currentUserID = UserAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        AddPostButton = findViewById(R.id.main_add_new_post_button);

        Nav = findViewById(R.id.main_bottom_nav);
        Nav.setSelectedItemId(R.id.nav_home);
        TopToolBar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(TopToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        AddPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPostIntent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivity(addPostIntent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = UserAuth.getCurrentUser();
        if(currentUser == null){
            SendUserToLoginActivity();
        }else{
            CheckUserExistence();
        }
    }



    private void CheckUserExistence() {
        final String current_user_id = UserAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    SendUserToSetupActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetUpAccountActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
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