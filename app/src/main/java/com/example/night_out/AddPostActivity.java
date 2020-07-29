package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {
    private Toolbar TopToolBar;
    private ImageView SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;

    private Uri ImageUri;
    private static final int GalleryPic = 1;

    private String downloadUrl;
    private String description;
    private String SaveCurrentDate;
    private String SaveCurrentTime;
    private String PostRandomName;
    private StorageReference PostImagesReference;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth UserAuth;
    private String current_user_id;

    private long countPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        UserAuth = FirebaseAuth.getInstance();
        current_user_id = UserAuth.getCurrentUser().getUid();
        PostImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        SelectPostImage = findViewById(R.id.add_post_image);
        UpdatePostButton = findViewById(R.id.add_post_button);
        PostDescription = findViewById(R.id.add_post_caption);

        TopToolBar = findViewById(R.id.add_post_toolbar);
        setSupportActionBar(TopToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneGallery();
            }
        });
        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyPost();
            }
        });
    }

    private void VerifyPost() {
        description = PostDescription.getText().toString();
        if (ImageUri == null) {
            Toast.makeText(this, "Please select post image", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please add a caption", Toast.LENGTH_SHORT).show();

        }else{
            StoringImageToFirebaseStorage();
        }
    }

    public void PhoneGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPic);
    }
    private void StoringImageToFirebaseStorage() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("EEE, MMM d, ''yy");
        SaveCurrentDate = currentDate.format(date.getTime());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        SaveCurrentTime = currentTime.format(time.getTime());
        PostRandomName = SaveCurrentDate+SaveCurrentTime;

        final StorageReference filePath = PostImagesReference.child("Post Images").child(ImageUri.getLastPathSegment() +PostRandomName+ ".jpg");
        filePath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(AddPostActivity.this, "Image stored", Toast.LENGTH_SHORT).show();
                        downloadUrl = uri.toString();

                        SavingPostInformationToDatabase();

                    }
                });
            }
        });
    }

    private void SavingPostInformationToDatabase() {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    countPosts = dataSnapshot.getChildrenCount();
                }else{
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userName = dataSnapshot.child("username").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("uid", current_user_id);
                    postMap.put("date", SaveCurrentDate);
                    postMap.put("time", SaveCurrentTime);
                    postMap.put("caption", description);
                    postMap.put("postimage", downloadUrl);
                    postMap.put("profileimage", userProfileImage);
                    postMap.put("username", userName);
                    postMap.put("counter", countPosts);

                    PostsRef.child(current_user_id + PostRandomName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AddPostActivity.this, "Post is created sucessfully", Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(AddPostActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                            }else{
                                Toast.makeText(AddPostActivity.this, "Error post not created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){

            Intent mainIntent = new Intent(AddPostActivity.this, MainActivity.class);
            startActivity(mainIntent);

        }

        return super.onOptionsItemSelected(item);
    }
}