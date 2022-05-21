package com.example.firbasedb;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;

public class CartAd extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Cart> mContentList;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public void setFilteredList(ArrayList<Cart> mContentList) {
        this.mContentList = mContentList;
        notifyDataSetChanged();

    }

    public CartAd(Context mContext, Activity mActivity, ArrayList<Cart> mContentList) {

        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incart_item, parent, false);
        return new CartAd.ViewHolder(view, viewType);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView cImg;
        private TextView cName;
        private TextView cPrice;
        private TextView cAmount;
        private Button plus;
        private Button minus;
        private ImageButton deleteCart;
        private Button completeOrder;
       // private TextView totalPriceCart;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.productCardCart);
            cImg = (ImageView) itemView.findViewById(R.id.productImgCart);
            cName = (TextView) itemView.findViewById(R.id.productNameCart);
            cPrice = (TextView) itemView.findViewById(R.id.priceCart);
            cAmount = (TextView) itemView.findViewById(R.id.quantityCart);
            deleteCart=itemView.findViewById(R.id.deleteCart);
           // totalPriceCart=(TextView) itemView.findViewById(R.id.totalPriceCart);
            plus=itemView.findViewById(R.id.plusCart);
            minus=itemView.findViewById(R.id.minusCart);
            completeOrder=itemView.findViewById(R.id.completeOrder);

        }
    }
//    private void nrProduct(TextView amount,String productid){
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("cart")
//                .child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                amount.setText(reference.child(productid).child("productAmountInCart").g);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        CartAd.ViewHolder holder = (CartAd.ViewHolder) mainHolder;
        final Cart model = mContentList.get(position);
        // setting data over views
        String imgUrl = model.getImg();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(holder.cImg);
        }
        //double price=Double.parseDouble(model.getTotalPrice())+Double.parseDouble(model.getPrice());

        holder.cName.setText(model.getName());
        holder.cPrice.setText(model.getPrice());

        holder.deleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).getKey().equals(firebaseUser.getUid())) {
                    String y=model.getPrice();
                    double nn=Double.parseDouble(model.getProductAmountInCart());
                        model.setProductAmountInCart(0 + "");
                        Cart.totalPrice=Cart.totalPrice-(Double.parseDouble(y)*nn);
                        FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId())
                                .removeValue();
                        holder.cAmount.setText((model.getProductAmountInCart()));

                }
            }
        });



        holder.plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).getKey().equals(firebaseUser.getUid())) {
                    if(Integer.parseInt(model.getAmount())>Integer.parseInt(model.getProductAmountInCart())) {

                        model.setProductAmountInCart((Integer.parseInt(model.getProductAmountInCart()) + 1) + "");
                        Cart.totalPrice = Cart.totalPrice + Double.parseDouble(model.getPrice());

                        FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId())
                                .child("productAmountInCart").setValue(model.getProductAmountInCart());
                        holder.cAmount.setText((model.getProductAmountInCart()));
                    }else{
                        Toast.makeText(mActivity, "You can't add more from this product", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).getKey().equals(firebaseUser.getUid())) {
                    if((Integer.parseInt(model.getProductAmountInCart()))>1) {
                        model.setProductAmountInCart((Integer.parseInt(model.getProductAmountInCart()) - 1) + "");
                        Cart.totalPrice=Cart.totalPrice-Double.parseDouble(model.getPrice());


                        FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(model.getProductId())
                                .child("productAmountInCart").setValue(model.getProductAmountInCart());
                        holder.cAmount.setText((model.getProductAmountInCart()));

                    }
                }


            }
        });
        holder.cAmount.setText((model.getProductAmountInCart()));

//        holder.completeOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                holder.completeOrder.setText("hi");
//
//            }
//        });




    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }


}

