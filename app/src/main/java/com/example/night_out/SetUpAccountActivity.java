package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner GenderSpinner, RelationSpinner;
    private TextView DisplayDate;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private static final String TAG = "SetUpAccountActivity";

    private EditText UserFullName, Username, Country;
    private CircleImageView UserProfileImage;
    private Button SaveInfoButton;

    private FirebaseAuth UserAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String currentUserID;
    String GenderPick;
    String RelationshipStatus;
    String DOB;
    final static int GalleryPic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_account);

        UserAuth = FirebaseAuth.getInstance();
        currentUserID = UserAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        UserFullName = findViewById(R.id.setUp_fullname);
        Username = findViewById(R.id.setUp_username);
        UserProfileImage = findViewById(R.id.setUp_profile_image);
        SaveInfoButton = findViewById(R.id.setUp_save_info_button);
        DisplayDate = findViewById(R.id.setUp_DOB);
        Country = findViewById(R.id.setUp_country);

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

        DisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SetUpAccountActivity.this,
                        android.R.style.Theme_Material_Dialog_NoActionBar_MinWidth, onDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                DOB = month + "/" + dayOfMonth + "/" + year;
                DisplayDate.setText(DOB);
            }
        };

        UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPic);
            }
        });
        
        SaveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateProfile();
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.ic_baseline_account).into(UserProfileImage);

                    }else {
                        Toast.makeText(SetUpAccountActivity.this, "Profile image does not exist, please select one.", Toast.LENGTH_SHORT).show();
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void CreateProfile() {
        GenderPick = String.valueOf(GenderSpinner.getSelectedItem());
        RelationshipStatus = String.valueOf(RelationSpinner.getSelectedItem());
        String fullName = UserFullName.getText().toString();
        String username = Username.getText().toString();
        String country = Country.getText().toString();
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username)){
            StyleableToast.makeText(this, "Please fill in all fields to proceed", Toast.LENGTH_LONG, R.style.custom_toast_bad).show();
        }else{
            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullName);
            userMap.put("country", country);
            userMap.put("bio", "Default");
            userMap.put("gender", GenderPick);
            userMap.put("dob", DOB);
            userMap.put("relationshipstatus", RelationshipStatus);
            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        Toast.makeText(SetUpAccountActivity.this, "Successfully created account! Welcome to Night Out!", Toast.LENGTH_LONG).show();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetUpAccountActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(SetUpAccountActivity.this, "Profile image stored successfully", Toast.LENGTH_SHORT).show();
                                final String downloadUrl = uri.toString();
                                UsersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SetUpAccountActivity.this, "Profile image stored to database", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(SetUpAccountActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            } else {
                Toast.makeText(this, "Error image cant be cropped", Toast.LENGTH_SHORT).show();
            }

        }



    }
    public void SendUserToMainActivity(){
        Intent mainIntent = new Intent(SetUpAccountActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

}