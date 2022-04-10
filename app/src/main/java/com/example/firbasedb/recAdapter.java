package com.example.firbasedb;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class recAdapter extends FirebaseRecyclerAdapter<Recipe, recAdapter.ViewHolder> {

    private Context mContext;
    FirebaseUser firebaseUser;
    public recAdapter(@NonNull FirebaseRecyclerOptions<Recipe> options) {
        super(options);
    }

    public recAdapter(@NonNull FirebaseRecyclerOptions<Recipe> options, Context mContext) {
        super(options);
        this.mContext = mContext;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Recipe model) {
        holder.rMaker.setText(model.getRecipeMaker());
        holder.rName.setText(model.getRecipeName());
        Glide.with(holder.rImg.getContext()).load(model.getImg()).into(holder.rImg);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus =
                        DialogPlus.newDialog(holder.rImg.getContext())
                                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.recipe_details_dialog))
                                .setExpanded(true, 1750).create();
                View myView = dialogPlus.getHolderView();

                TextView recName = myView.findViewById(R.id.RecipeName);
                TextView recDiet = myView.findViewById(R.id.RecipeDiet);
                TextView recProcedure = myView.findViewById(R.id.RecipeProcedure);
                ImageView purl = myView.findViewById(R.id.recImage);
                ImageButton previousButton = myView.findViewById(R.id.previousButton2);
                DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("recipe")
                        .child(getRef(position).getKey());
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String rpro= snapshot.child("howmake").getValue(String.class);
                        recProcedure.setText(rpro);
                        String rdiet= snapshot.child("dite").getValue(String.class);
                        recDiet.setText(rdiet);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                recName.setText(model.getRecipeName());
                Glide.with(holder.rImg.getContext()).load(model.getImg()).into(purl);
                dialogPlus.show();
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPlus.dismiss();
                    }
                });

            }
        });
        isLike(model.getRcipeID(),holder.addToFav);
        holder.addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.addToFav.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("RecLikes").child(model.getRcipeID())
                            .child(firebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("RecLikes").child(model.getRcipeID())
                            .child(firebaseUser.getUid()).removeValue();

                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_custom_item,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView rImg;
        private TextView rName;
        private TextView rMaker;
        private ImageButton addToFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.recipeCard);
            rImg = (ImageView) itemView.findViewById(R.id.recipeImg);
            rName = (TextView) itemView.findViewById(R.id.recipeName);
            rMaker = (TextView) itemView.findViewById(R.id.recipeMaker);
            addToFav=(ImageButton)itemView.findViewById(R.id.recipeFav);

        }
    }
    private void isLike(String postid, final ImageButton imageView){
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("RecLikes").child(postid);
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


}

