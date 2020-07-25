package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView Nav;
    private NavigationView ProfileMenu;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout DrawLayout;
    private Toolbar ToolBar;
    private Button LogOutTemp;
    private FirebaseAuth UserAuth;
    private DatabaseReference UsersRef;

    private TextView Username, Friends, Posts, FullName, UserBio;
    private CircleImageView ProfileImage;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Username = findViewById(R.id.profile_top_username);
        ProfileImage = findViewById(R.id.profile_user_image);
        UserBio = findViewById(R.id.profile_user_bio);
        FullName = findViewById(R.id.profile_fullname);

        UserAuth = FirebaseAuth.getInstance();

        currentUserID = UserAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        ProfileMenu = findViewById(R.id.profile_nav_menu);
        DrawLayout = findViewById(R.id.draw_layout);
        ToolBar = findViewById(R.id.profile_layout_bar);
        setSupportActionBar(ToolBar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, DrawLayout, ToolBar, R.string.drawer_open, R.string.drawer_close);
        DrawLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Nav = findViewById(R.id.profile_bottom_nav);
        Nav.setSelectedItemId(R.id.nav_profile);

        Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        ProfileMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                ProfileMenuSelected(menuItem);
                return false;
            }
        });


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String username = dataSnapshot.child("username").getValue().toString();
                    String userImage = dataSnapshot.child("profileimage").getValue().toString();
                    String bio = dataSnapshot.child("bio").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    Username.setText(username);
                    FullName.setText(fullname);
                    UserBio.setText(bio);
                    Picasso.get().load(userImage).into(ProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ProfileMenuSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_profile_edit:
                Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_profile_checked_locations:
                Toast.makeText(this, "Locations", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_profile_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_posts_liked:
                Toast.makeText(this, "Posts liked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_profile_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_profile_logout:
                UserAuth.signOut();
                Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
                break;
        }
    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_discover:
                startActivity(new Intent(getApplicationContext(), DiscoverActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.nav_messages:
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.nav_profile:
                //startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;

        }
    }
}