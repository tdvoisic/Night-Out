package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth UserAuth;
    private DatabaseReference UsersRef, PostsRef, LikesRef;

    private ImageButton AddPostButton;
    private CircleImageView PostProfileImage;
    private TextView PostUsername;
    private RecyclerView PostList;

    private BottomNavigationView Nav;
    private Toolbar TopToolBar;
    String currentUserID;
    Boolean likeChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserAuth = FirebaseAuth.getInstance();
        currentUserID = UserAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        AddPostButton = findViewById(R.id.main_add_new_post_button);
        PostUsername = findViewById(R.id.post_user_name);
        PostProfileImage = findViewById(R.id.posts_user_profile_image);

        PostList = findViewById(R.id.main_posts_list);
        PostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PostList.setLayoutManager(linearLayoutManager);

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

        DisplayPosts();


    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0, 0);
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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = UserAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            CheckUserExistence();
        }
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

    private void CheckUserExistence() {
        final String current_user_id = UserAuth.getCurrentUser().getUid();
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(current_user_id)) {
                    SendUserToSetupActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayPosts() {
        Query sortPostsInCorrectOrder = PostsRef.orderByChild("counter");
        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(sortPostsInCorrectOrder, Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int i, @NonNull Posts posts) {
                final String PostKey = getRef(i).getKey();

                postsViewHolder.username.setText(posts.getUsername());
                postsViewHolder.caption.setText(posts.getCaption());
                postsViewHolder.date.setText(posts.getDate());
//                postsViewHolder.caption_username.setText(posts.getFullname());
                Picasso.get().load(posts.getPostimage()).into(postsViewHolder.PostImage);
                Picasso.get().load(posts.getProfileimage()).into(postsViewHolder.image);

                postsViewHolder.setLikeButtonStatus(PostKey);

                postsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(MainActivity.this, PostDetailActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });

                postsViewHolder.commentPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentIntent = new Intent(MainActivity.this, CommentsActivity.class);
                        commentIntent.putExtra("PostKey", PostKey);
                        startActivity(commentIntent);

                    }
                });

                postsViewHolder.likePostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeChecker = true;

                        LikesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likeChecker.equals(true)) {
                                    if (dataSnapshot.child(PostKey).hasChild(currentUserID)) {
                                        LikesRef.child(PostKey).child(currentUserID).removeValue();
                                        likeChecker = false;

                                    } else {
                                        LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                        likeChecker = false;


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_post_layout, parent, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };

        PostList.setAdapter(adapter);

        adapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView username, time, date, caption;
        CircleImageView image;
        ImageView PostImage;

        ImageButton likePostButton, commentPostButton;
        TextView DisplayNumberOflikes;
        int countLikes;
        String currentUserID;
        DatabaseReference likesRef;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            username = itemView.findViewById(R.id.post_user_name);
            date = itemView.findViewById(R.id.post_date);
            caption = itemView.findViewById(R.id.post_caption);
            image = itemView.findViewById(R.id.posts_user_profile_image);
            PostImage = itemView.findViewById(R.id.post_image);

            likePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            commentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            DisplayNumberOflikes = (TextView) mView.findViewById(R.id.display_number_of_likes);
            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        }

        public void setLikeButtonStatus(final String PostKey) {
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserID)) {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.ic_baseline_like_full);
                        if (countLikes == 1) {
                            DisplayNumberOflikes.setText(Integer.toString(countLikes) + (" Like"));
                        } else {
                            DisplayNumberOflikes.setText(Integer.toString(countLikes) + (" Likes"));
                        }
                    } else {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        likePostButton.setImageResource(R.drawable.ic_baseline_like_empty);
                        if (countLikes == 1) {
                            DisplayNumberOflikes.setText(Integer.toString(countLikes) + (" Like"));
                        } else {
                            DisplayNumberOflikes.setText(Integer.toString(countLikes) + (" Likes"));
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}










