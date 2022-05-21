package com.example.firbasedb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class IngrdintsPage extends AppCompatActivity {
    private static final String TAG = "ProductPage";
    //CircularProgressIndicator progress_circular;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Product> productsList;
    private ProdAdapter prodAdapter = null;
    ImageButton prev;
    TextView noProducts;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_of_ing);

        Intent intent = getIntent();

        noProducts = (TextView) findViewById(R.id.noProducts) ;
        mActivity = IngrdintsPage.this;
        mContext = getApplicationContext();
        FirebaseApp.initializeApp(this);

        recyclerView = findViewById(R.id.ingView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        productsList = new ArrayList<>();
        String ing = intent.getStringExtra("ingredients");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product imagemodel = dataSnapshot.getValue(Product.class);
                    if(imagemodel.getName().toLowerCase().trim().contains(ing.toLowerCase().trim())) {
                        productsList.add(imagemodel);
                    }
                }
                if(productsList==null||productsList.size()==0){
                    noProducts.setText("Sorry, this product is not provided now! we will provide it as soon as possible");
                }
                prodAdapter = new ProdAdapter(mContext,mActivity, (ArrayList<Product>) productsList);
                recyclerView.setAdapter(prodAdapter);
                prodAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(IngrdintsPage.this,"Error:" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


        prev = findViewById(R.id.previousButton4);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}