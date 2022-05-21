package com.example.firbasedb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.AlertDialog;
import android.content.DialogInterface;





public class CartPage extends AppCompatActivity {
    private static final String TAG = "CartPage";
    DatabaseReference databaseReference;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Cart> cartList;
    private ArrayList<Cart> complete=new ArrayList<>();
    private CartAd cartAd = null;
    Button completeTheOrder;
    BottomNavigationView bottomNavigationView;
//    ImageButton prev;
//    private SearchView searchView;
    RecyclerView recyclerView;
    TextView totalPriceCart;
    double total=0;
    int tAmount=0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mActivity = CartPage.this;
        mContext = getApplicationContext();
        FirebaseApp.initializeApp(this);


//        bottomNavigationView=findViewById(R.id.nav_view_cart);
//        bottomNavigationView.setSelectedItemId(R.id.nav_basket);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.nav_home:
//                        startActivity(new Intent(CartPage.this,HomePage.class));
//                        break;
//                    case R.id.nav_fav:
//                        startActivity(new Intent(CartPage.this,Favorite.class));
//                        break;
//                    case R.id.nav_basket:
//                        startActivity(new Intent(CartPage.this,CartPage.class));
//                        break;
//                    case R.id.nav_add_recipe:
//                        startActivity(new Intent(CartPage.this,AddRecipePage.class));
//                        break;
//                    case R.id.nav_profile:
//                        startActivity(new Intent(CartPage.this,Account.class));
//                        break;
//
//
//                }
//                return true;
//            }
//        });
//        searchView=findViewById(R.id.searchBar);
//        searchView.clearFocus();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterList(newText);
//                return true;
//
//            }
//        });
//        tt=findViewById(R.id.totalPriceCart);
        totalPriceCart=(TextView) findViewById(R.id.totalPriceCart);

        recyclerView = findViewById(R.id.cartRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        cartList = new ArrayList<>();
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();



        databaseReference = FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid());
        String key = FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).getKey();
//        tt.setText(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                if(snapshot.getKey().equals(firebaseUser.getUid())) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){


                        double n=Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                        double a=Double.parseDouble(dataSnapshot.child("productAmountInCart").getValue().toString());
                        if(a==1) {
                            total += n;
                        }else{
                            total= total+(a*n);
                        }
                        tAmount +=a;


                        Cart imagemodel = dataSnapshot.getValue(Cart.class);


                        cartList.add(imagemodel);
                    }
                    totalPriceCart.setText(total+"");
                Cart.totalPrice=total;
                total=0;
                }
                cartAd = new CartAd(mContext,mActivity, (ArrayList<Cart>) cartList);
                recyclerView.setAdapter(cartAd);
                cartAd.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        completeTheOrder=findViewById(R.id.completeOrder);



        completeTheOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartList!=null&& cartList.size()>0) {
                    DatabaseReference allCartsRef = FirebaseDatabase.getInstance().getReference().child("cart");
                    allCartsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (!dataSnapshot.getKey().equals(firebaseUser.getUid())) {
                                    Iterable<DataSnapshot> productsInCart = dataSnapshot.getChildren();
                                    for (DataSnapshot p : productsInCart) {
                                        for (Cart c : cartList) {
                                            if (p.getKey().equals(c.getProductId())) {
                                                String amount = (Integer.parseInt(c.getAmount()) - Integer.parseInt(c.getProductAmountInCart())) + "";
                                                if (Integer.parseInt(amount) == 0) {
                                                    allCartsRef.child(dataSnapshot.getKey()).child(p.getKey()).removeValue();

                                                } else {
                                                    HashMap<String, Object> map = new HashMap<>();
                                                    if (Integer.parseInt(p.child("productAmountInCart").getValue().toString()) > Integer.parseInt(amount)) {
                                                        map.put("productAmountInCart", amount);
                                                    }
                                                    map.put("amount", amount);
                                                    allCartsRef.child(dataSnapshot.getKey()).child(p.getKey()).updateChildren(map);
                                                }

                                            }
                                        }

                                    }


                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    DatabaseReference dbp=FirebaseDatabase.getInstance().getReference().child("Products");
                            dbp.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        Product p=dataSnapshot.getValue(Product.class);
                                        //ap.add(p);
                                        for(Cart c: cartList) {
                                            if (p.getProductId().equals(c.getProductId())){
                                                Map<String, Object> map= new HashMap<>();
                                                String amount = (Integer.parseInt(c.getAmount()) - Integer.parseInt(c.getProductAmountInCart())) + "";
                                                map.put("amount",amount);
                                                dbp.child(p.getProductKey()).updateChildren(map);



                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    allCartsRef.child(firebaseUser.getUid()).removeValue();
                    startActivity(new Intent(CartPage.this,Complete.class));
                }




            }


    });









//
//        @Override
//        public void onClick(View view) {
//            AlertDialog dialog=new AlertDialog.Builder(mActivity)
//                    .setTitle("Complete The Order")
//                    .setMessage("Are you sure you want to complete the order?"+"\nNote: payment when receiving")
//                    .setCancelable(false)
//                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                        }
//                    }).setNegativeButton("Complete the order", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            ArrayList<Cart> com=new ArrayList<>();
//
//                            DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid());
//                            db.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                                        Cart cart=dataSnapshot.getValue(Cart.class);
//                                        com.add(cart);
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                            // FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).removeValue();
//
//                            DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference().child("cart");
//                            dbRef.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
//                                        //if (!(dataSnapshot.getKey().equals(firebaseUser.getUid()))){
//                                        //Cart imagemodel = dataSnapshot.child(dataSnapshot.getKey()).getValue(Cart.class);
//                                        Iterable<DataSnapshot> a = dataSnapshot.getChildren();
//                                        for (DataSnapshot i : a) {
//                                            Cart imagemodel = i.getValue(Cart.class);
//                                            complete.add(imagemodel);
//
//                                        }
//                                        for (Cart c : com) {
//                                            for (Cart s : complete) {
//
//                                                if (s.getProductId().equals(c.getProductId())) {
//                                                    String amount = (Integer.parseInt(c.getAmount()) - Integer.parseInt(c.getProductAmountInCart())) + "";
//                                                    Map<String, Object> map2 = new HashMap<>();
//                                                    //if(Integer.parseInt(amount)==0||dataSnapshot.getKey().equals(firebaseUser.getUid()))
//                                                    if (Integer.parseInt(amount) == 0||dataSnapshot.getKey().equals(firebaseUser.getUid())) {
//                                                        dbRef.child(dataSnapshot.getKey()).child(s.getProductId()).removeValue();
//
//                                                    } else {
//                                                        if (Integer.parseInt(s.getProductAmountInCart()) > Integer.parseInt(amount)) {
//                                                            map2.put("productAmountInCart", amount);
//
//
//                                                        }
//                                                        map2.put("amount", amount);
//                                                        dbRef.child(dataSnapshot.getKey()).child(s.getProductId()).updateChildren(map2);
//                                                    }
//
//
//                                                }
//                                            }
//                                        }
//                                        // }
//
//
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                            //ArrayList<Product> ap=new ArrayList<>();
//                            DatabaseReference dbp=FirebaseDatabase.getInstance().getReference().child("Products");
//                            dbp.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                                        Product p=dataSnapshot.getValue(Product.class);
//                                        //ap.add(p);
//                                        for(Cart c: com) {
//                                            if (p.getProductId().equals(c.getProductId())){
//                                                Map<String, Object> map= new HashMap<>();
//                                                String amount = (Integer.parseInt(c.getAmount()) - Integer.parseInt(c.getProductAmountInCart())) + "";
//                                                map.put("amount",amount);
//                                                dbp.child(p.getProductKey()).updateChildren(map);
//
//
//
//                                            }
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                            //db.removeValue();
//
//
//
//
//
//
//                        }
//                    }).create();
//            dialog.show();
//        }


//        prev = findViewById(R.id.previousButton);
//        prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(RecipePage.this, HomePage.class));
//            }
//        });


    }

    public void previouscart(View view) {
        finish();
    }


//    private void filterList(String newText) {
//        ArrayList<Recipe> filteredList=new ArrayList<>();
//        for(Recipe recipe: recipeList){
//            if(recipe.getRecipeName().toLowerCase().contains(newText.toLowerCase())){
//                filteredList.add(recipe);
//            }
//        }
//        if(filteredList.isEmpty()){
//            Toast.makeText(this,"Can't find this recipe",Toast.LENGTH_LONG).show();
//        }else{
//            imageAdapter.setFilteredList(filteredList);
//
//        }
//    }


}
