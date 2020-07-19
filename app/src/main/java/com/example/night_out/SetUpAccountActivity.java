package com.example.night_out;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner GenderSpinner, RelationSpinner;

    private EditText UserFullName, Username;
    private CircleImageView UserProfileImage;
    private Button SaveInfoButton;

    private FirebaseAuth UserAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String currentUserID;
    String GenderPick;
    String RelationshipStatus;
    final static int GalleryPic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);

        UserAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UserFullName = findViewById(R.id.setUp_fullname);
        Username = findViewById(R.id.setUp_username);
        UserProfileImage = findViewById(R.id.setUp_profile_image);

        GenderSpinner = findViewById(R.id.setUp_gender);
        RelationSpinner = findViewById(R.id.setUp_relationship);

        String[] Genders = getResources().getStringArray(R.array.genders);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GenderSpinner.setAdapter(adapter);
        GenderSpinner.setOnItemSelectedListener(this);

        String[] Relations = getResources().getStringArray(R.array.relationship);
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Relations);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RelationSpinner.setAdapter(adapter1);
        RelationSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}