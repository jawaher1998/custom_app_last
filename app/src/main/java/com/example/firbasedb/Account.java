package com.example.firbasedb;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firbasedb.databinding.ActivityAccountBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Account extends AppCompatActivity  {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Recipe> recipeList;
    private postAdapter postAdapter ;
    private TextView postno;
    FirebaseAuth firebaseAuth;
    ActivityAccountBinding Binding;
    ActivityResultLauncher<String> launcher;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(Binding.getRoot());

        bottomNavigationView=findViewById(R.id.nav_view_account);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(Account.this,HomePage.class));
                        break;
                    case R.id.nav_fav:
                        startActivity(new Intent(Account.this,Favorite.class));
                        break;
                    case R.id.nav_basket:
                        startActivity(new Intent(Account.this,CartPage.class));
                        break;
                    case R.id.nav_add_recipe:
                        startActivity(new Intent(Account.this,AddRecipePage.class));
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(Account.this,Account.class));
                        break;


                }
                return true;
            }
        });

        mActivity = Account.this;
        mContext = getApplicationContext();
        recyclerView = findViewById(R.id.POST);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recipeList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();;
        registerForContextMenu(recyclerView);
        FirebaseStorage storageReference1 = FirebaseStorage.getInstance();
        FirebaseDatabase profile_uri = FirebaseDatabase.getInstance();
        profile_uri.getReference("Account").child(firebaseAuth.getUid()).child("image_uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imge = snapshot.getValue(String.class);
                if (imge.isEmpty()) {
                    Toast.makeText(mContext, "the account not have image", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Picasso.get()
                            .load(imge)
                            .placeholder(R.drawable.person)
                            .error(R.drawable.person)
                            .into(Binding.profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        Query fi= FirebaseDatabase.getInstance().getReference().child("recipe").orderByChild("id").equalTo(firebaseAuth.getUid());
        fi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Recipe imagemodel = dataSnapshot.getValue(Recipe.class);
                    recipeList.add(imagemodel);
                }
                postAdapter = new postAdapter(getApplicationContext(), (ArrayList<Recipe>) recipeList);
                recyclerView.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Account.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void editprof(View view) {
        startActivity(new Intent(Account.this,EditProfile.class));
    }
}

