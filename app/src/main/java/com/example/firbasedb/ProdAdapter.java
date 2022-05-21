package com.example.firbasedb;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class
ProdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;

    private Activity mActivity;
    private ArrayList<Product> mContentList;

    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    //boolean containAllergy=false;
    String allergy="null";
    String containAllergy="";
    boolean inCart=false;
    String aCart="0";





    public void setFilteredList(ArrayList<Product> productList){
        this.mContentList=productList;
        notifyDataSetChanged();

    }

//    public ProdAdapter(Context mContext,ArrayList<Product> mContentList) {
//        this.mContext = mContext;
//        this.mContentList = mContentList;
//    }

    public ProdAdapter(Context mContext, Activity mActivity, ArrayList<Product> mContentList) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_custom_item, parent, false);
        return new ViewHolder(view, viewType);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView pImg;
        private TextView pName;
        private TextView pPrice;
        private ImageButton addToCart,addToFav;



        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.productCard);
            pImg = (ImageView) itemView.findViewById(R.id.productImg);
            pName = (TextView) itemView.findViewById(R.id.productNam);
            pPrice = (TextView) itemView.findViewById(R.id.price);
            addToCart=(ImageButton) itemView.findViewById(R.id.addToCart);
            addToFav=(ImageButton) itemView.findViewById(R.id.productFav);





        }

    }
    private void isLike(String postid, final ImageButton imageView){
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_action_name);
                    imageView.setTag("like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private void showAlertDialog(String message){
//        AlertDialog dialog=new AlertDialog.Builder(mActivity)
//                .setTitle("Be careful")
//                .setMessage("this product contains an allergen\n Do you sure you want to add it to cart?")
//                .setCancelable(false)
//                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                }).setNegativeButton("add to cart", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                    }
//                }).create();
//        dialog.show();
//    }
    private void isInCart(String id,ImageButton imageButton){
        imageButton.setTag(R.id.cart,"out_cart");
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference().child("cart");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){

                    if(snapshot.child(firebaseUser.getUid()).child(id).exists()) {
                        //aCart=snapshot.child(firebaseUser.getUid()).child(id).child("productAmountInCart").getValue().toString();
                        imageButton.setTag(R.id.cart,"in_cart");

                    }else{
                        //aCart="0";
                        imageButton.setTag(R.id.cart,"out_cart");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void isContainAllergy(String ingred, final ImageButton imageView){
        DatabaseReference dbref=FirebaseDatabase.getInstance().getReference().child("user").child("healthinfo");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setTag(R.id.allergy,"");
                    containAllergy="";
                    allergy = snapshot.child(firebaseUser.getUid()).child("allergy").getValue(String.class);
                    ArrayList<String> al = new ArrayList<>(Arrays.asList(allergy.split(",")));
                    ArrayList<String> ing = new ArrayList<>(Arrays.asList(ingred.split(",")));
                    for (String ingredient : ing) {
                        for (String a : al){
                            if (!ingredient.equals("")) {
                                if (a.toLowerCase().trim().contains(ingredient.toLowerCase().trim())) {
                                    imageView.setTag(R.id.allergy,ingredient+" "+imageView.getTag(R.id.allergy).toString());
                                }
                            }
                    }
                    }

                }else{
                    imageView.setTag(R.id.allergy,"");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        ViewHolder holder = (ViewHolder) mainHolder;
        final Product model = mContentList.get(position);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.pImg.getContext())
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.product_details_dialog))
                        .setExpanded(true, 1750).create();
                View myView = dialogPlus.getHolderView();
                TextView productName = myView.findViewById(R.id.ProductName);
                TextView productPrice = myView.findViewById(R.id.productPrice);
                ImageView purl = myView.findViewById(R.id.ProductImage);
                TextView productIngredients = myView.findViewById(R.id.ProductIngredients);
                TextView productDiet = myView.findViewById(R.id.ProductDiet);
                productIngredients.setMovementMethod(new ScrollingMovementMethod());
                ImageButton previousButton = myView.findViewById(R.id.ProductPreviousButton);
                ImageButton favoriteButton = myView.findViewById(R.id.productFavD);
                ImageButton addToCartDetail=myView.findViewById(R.id.addToCartDetail);
                productName.setText(model.getName());
                productPrice.setText(model.getPrice());
                Glide.with(mContext).load(model.getImage()).into(purl);
                productDetails(model.getProductKey(),productIngredients,productDiet);

                isLike(model.getProductId(),favoriteButton);
                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(favoriteButton.getTag().equals("like")){
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
                                    .child(firebaseUser.getUid()).setValue(true);
                        }else{
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
                                    .child(firebaseUser.getUid()).removeValue();

                        }

                    }
                });

                isContainAllergy(model.getIngredients(),addToCartDetail);
                isInCart(model.getProductId(),addToCartDetail);

                addToCartDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Integer.parseInt(model.getAmount())!=0){

                            if(!(addToCartDetail.getTag(R.id.allergy).toString().equals(""))){
                                AlertDialog dialog=new AlertDialog.Builder(mActivity)
                                        .setTitle("Be careful")
                                        .setMessage("this product contains allergen/s: ("+addToCartDetail.getTag(R.id.allergy).toString()+")\n Are you sure you want to add it to cart?")
                                        .setCancelable(false)
                                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                            
                                        }).setNegativeButton("add to cart", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(addToCartDetail.getTag(R.id.cart).toString().equals("out_cart")) {


                                                    HashMap<String, Object> hash = new HashMap<>();

                                                    hash.put("img", model.getImage());
                                                    hash.put("name", model.getName());
                                                    hash.put("price", model.getPrice());
                                                    hash.put("productId", model.getProductId());
                                                    hash.put("amount", model.getAmount());
                                                    hash.put("productAmountInCart", "1");
                                                    FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId()).setValue(hash)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    Toast.makeText(mActivity, "Product successfully added to cart", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(mActivity, "Could not added" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(mActivity, "Product is already in the cart", Toast.LENGTH_SHORT).show();

                                                }



                                            }
                                        }).create();
                                dialog.show();

                            }else {

                                if (addToCartDetail.getTag(R.id.cart).toString().equals("out_cart")) {


                                    HashMap<String, Object> hash = new HashMap<>();

                                    hash.put("img", model.getImage());
                                    hash.put("name", model.getName());
                                    hash.put("price", model.getPrice());
                                    hash.put("productId", model.getProductId());
                                    hash.put("amount", model.getAmount());
                                    //hash.put("totalPrice",model.getPrice());
                                    hash.put("productAmountInCart", "1");
                                    //hash.put("productAmountInCart", Integer.toString(Integer.parseInt(aCart)+1));
                                    FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId()).setValue(hash)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                    Toast.makeText(mActivity, "Product successfully added to cart", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mActivity, "Could not added" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                }else{
                                    Toast.makeText(mActivity, "Product is already in the cart", Toast.LENGTH_SHORT).show();
                                }
                            }}else{
                            Toast.makeText(mActivity, "This product is sold out", Toast.LENGTH_SHORT).show();

                        }

                    }
                });

                dialogPlus.show();
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPlus.dismiss();
                    }
                });

            }
        });




//        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.pImg.getContext())
//                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.product_details_dialog))
//                        .setExpanded(true, 1750).create();
//                View myView = dialogPlus.getHolderView();
//                TextView productName = myView.findViewById(R.id.ProductName);
//                TextView productPrice = myView.findViewById(R.id.productPrice);
//                ImageView purl = myView.findViewById(R.id.ProductImage);
//                TextView productIngredients = myView.findViewById(R.id.ProductIngredients);
//                TextView productDiet = myView.findViewById(R.id.ProductDiet);
//                ImageButton previousButton = myView.findViewById(R.id.ProductPreviousButton);
//                ImageButton favoriteButton = myView.findViewById(R.id.productFav);
//                productName.setText(model.getName());
//                productPrice.setText(model.getPrice());
//                Glide.with(mContext).load(model.getImage()).into(purl);
//                productDetails(model.getProductKey(),productIngredients,productDiet);
//
//                favoriteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        isLike(model.getProductId(),favoriteButton);
//                        holder.addToFav.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if(holder.addToFav.getTag().equals("like")){
//                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
//                                            .child(firebaseUser.getUid()).setValue(true);
//                                }else{
//                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
//                                            .child(firebaseUser.getUid()).removeValue();
//
//                                }
//                            }
//                        });
//                    }
//                });
//
//                dialogPlus.show();
//                previousButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialogPlus.dismiss();
//                    }
//                });
//
//            }
//        });



        // setting data over views
        String imgUrl = model.getImage();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(holder.pImg);
        }

        holder.pName.setText(model.getName());
        holder.pPrice.setText(model.getPrice());



        isLike(model.getProductId(),holder.addToFav);
        holder.addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.addToFav.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
                            .child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
                            .child(firebaseUser.getUid()).removeValue();

                }
            }
        });
        isContainAllergy(model.getIngredients(),holder.addToCart);
        isInCart(model.getProductId(),holder.addToCart);


        //containAllergy=false;
        holder.addToCart.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                //inCart=false;






//               DatabaseReference dbref=FirebaseDatabase.getInstance().getReference().child("user").child("healthinfo");
//                dbref.addValueEventListener(new ValueEventListener() {
//
//
//
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.child(firebaseUser.getUid()).exists()) {
//
//
//                            allergy = snapshot.child(firebaseUser.getUid()).child("allrgy").getValue(String.class);
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        //containAllergy=true;
//
//                    }
//                });
//                ArrayList<String> ing = new ArrayList<>(Arrays.asList(model.getIngredients().split(",")));
//                for (String ingredient : ing) {
//                    if (ingredient.toLowerCase().trim().contains(allergy.toLowerCase().trim())){
//                        containAllergy=true;
//                        //holder.addToCart.setTag("allergy");
//                    }
//                }




        if(Integer.parseInt(model.getAmount())!=0){

                if(!(holder.addToCart.getTag(R.id.allergy).toString().equals(""))){

                    //showAlertDialog(holder.addToCart.getTag().toString());
                    //Toast.makeText(mActivity, "Contains allergy" , Toast.LENGTH_SHORT).show();
                    AlertDialog dialog=new AlertDialog.Builder(mActivity)
                            .setTitle("Be careful")
                            .setMessage("this product contains allergen/s: ("+holder.addToCart.getTag(R.id.allergy).toString()+")\n Are you sure you want to add it to cart?")
                            .setCancelable(false)
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("add to cart", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(holder.addToCart.getTag(R.id.cart).toString().equals("out_cart")) {


                                        HashMap<String, Object> hash = new HashMap<>();

                                        hash.put("img", model.getImage());
                                        hash.put("name", model.getName());
                                        hash.put("price", model.getPrice());
                                        hash.put("productId", model.getProductId());
                                        hash.put("amount", model.getAmount());
                                        //hash.put("totalPrice",model.getPrice());
                                        hash.put("productAmountInCart", "1");

                                        //hash.put("productAmountInCart", Integer.toString(Integer.parseInt(aCart)+1));
                                        FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId()).setValue(hash)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        Toast.makeText(mActivity, "Product successfully added to cart", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(mActivity, "Could not added" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }else{
                                        Toast.makeText(mActivity, "Product is already in the cart", Toast.LENGTH_SHORT).show();

                                    }



                                }
                            }).create();
                    dialog.show();

                }else {

                    if (holder.addToCart.getTag(R.id.cart).toString().equals("out_cart")) {


                        HashMap<String, Object> hash = new HashMap<>();

                        hash.put("img", model.getImage());
                        hash.put("name", model.getName());
                        hash.put("price", model.getPrice());
                        hash.put("productId", model.getProductId());
                        hash.put("amount", model.getAmount());
                        //hash.put("totalPrice",model.getPrice());
                        hash.put("productAmountInCart", "1");
                        //hash.put("productAmountInCart", Integer.toString(Integer.parseInt(aCart)+1));
                        FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId()).setValue(hash)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(mActivity, "Product successfully added to cart", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mActivity, "Could not added" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }else{
                        Toast.makeText(mActivity, "Product is already in the cart", Toast.LENGTH_SHORT).show();
                    }
                }}else{
            Toast.makeText(mActivity, "This product is sold out", Toast.LENGTH_SHORT).show();

        }
                //inCart=false;


//                if(holder.addToCart.getTag().equals("in_cart")){
//                    FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid())
//                            .child(firebaseUser.getUid()).setValue(true);
//                }else{
//                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getProductId())
//                            .child(firebaseUser.getUid()).removeValue();
//
//                }
                //containAllergy=false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    private void productDetails(String productKey, final TextView productIngredients ,final TextView productDiet){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products").child(productKey);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String proIngredients= snapshot.child("ingredients").getValue(String.class);
                productIngredients.setText(proIngredients);
                boolean ketoDiet= snapshot.child("keto").getValue(boolean.class);
                if(snapshot.child("keto").getValue(boolean.class)==true &&
                        snapshot.child("sugarFree").getValue(boolean.class)==true &&
                        snapshot.child("vegan").getValue(boolean.class)==true){
                    productDiet.setText("Keto, Sugar Free, vegan");
                }else if (snapshot.child("keto").getValue(boolean.class)==true &&
                        snapshot.child("sugarFree").getValue(boolean.class)==true &&
                        snapshot.child("vegan").getValue(boolean.class)==false){
                    productDiet.setText("Keto, Sugar Free");
                }else if (snapshot.child("keto").getValue(boolean.class)==false &&
                        snapshot.child("sugarFree").getValue(boolean.class)==true &&
                        snapshot.child("vegan").getValue(boolean.class)==true){
                    productDiet.setText("Sugar Free, vegan");
                }else if (snapshot.child("keto").getValue(boolean.class)==true &&
                        snapshot.child("sugarFree").getValue(boolean.class)==false &&
                        snapshot.child("vegan").getValue(boolean.class)==true){
                    productDiet.setText("Keto, vegan");
                }else if (snapshot.child("keto").getValue(boolean.class)==true &&
                        snapshot.child("sugarFree").getValue(boolean.class)==false &&
                        snapshot.child("vegan").getValue(boolean.class)==false){
                    productDiet.setText("Keto");
                }else if (snapshot.child("keto").getValue(boolean.class)==false &&
                        snapshot.child("sugarFree").getValue(boolean.class)==true &&
                        snapshot.child("vegan").getValue(boolean.class)==false){
                    productDiet.setText("Sugar Free");
                }else if (snapshot.child("keto").getValue(boolean.class)==false &&
                        snapshot.child("sugarFree").getValue(boolean.class)==false &&
                        snapshot.child("vegan").getValue(boolean.class)==true){
                    productDiet.setText("vegan");
                }else if (snapshot.child("keto").getValue(boolean.class)==false &&
                        snapshot.child("sugarFree").getValue(boolean.class)==false &&
                        snapshot.child("vegan").getValue(boolean.class)==false){
                    productDiet.setText("No diet suited");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

//        cart(model.getProductId(),holder.addToCart);
//        int productsAmountInCart=0;
//        holder.addToCart.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                if(holder.addToCart.getTag().equals("in_cart")){
//                    FirebaseDatabase.getInstance().getReference().child("cart").child(model.getProductId())
//                            .child(firebaseUser.getUid()).setValue(++productsAmountInCart);
//                }
////                else{
////                    FirebaseDatabase.getInstance().getReference().child("cart").child(model.getProductId())
////                            .child(firebaseUser.getUid()).removeValue();
////
////                }
//            }
//        });






//        holder.addToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase.getInstance().getReference().child("Products").child(mContentList.get(position).getKey())
//
//            }
//        });





//    private void productAmountInCart(TextView likes,String productId){
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("cart")
//                .child(productId);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                likes.setText(snapshot.getChildren()+" likes");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//    private void nrLikes(TextView likes,String postid){
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes")
//                .child(postid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                likes.setText(snapshot.getChildren()+" likes");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }



