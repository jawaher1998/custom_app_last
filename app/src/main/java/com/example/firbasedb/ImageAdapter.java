package com.example.firbasedb;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private Activity mActivity;
    private ArrayList<Recipe> mContentList;
    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


    public void setFilteredList(ArrayList<Recipe> recipeList){
        this.mContentList=recipeList;
        notifyDataSetChanged();

    }

    public ImageAdapter(Context mContext, ArrayList<Recipe> mContentList) {

        this.mContext = mContext;
        this.mContentList = mContentList;

    }
    public ImageAdapter(Context mContext) {

        this.mContext = mContext;

    }

    public ImageAdapter(Context mContext, Activity mActivity, ArrayList<Recipe> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_custom_item,parent,false);
        return new ImageAdapter.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView rImg;
        private TextView rName;
        private TextView rMaker;
        private ImageButton addToFav;

        public ViewHolder(View itemView) {
            super(itemView);

            // Find all views ids
            cardView = (CardView) itemView.findViewById(R.id.recipeCard);
            rImg = (ImageView) itemView.findViewById(R.id.recipeImg);
            rName = (TextView) itemView.findViewById(R.id.recipeName);
            rMaker = (TextView) itemView.findViewById(R.id.recipeMaker);
            addToFav=(ImageButton)itemView.findViewById(R.id.recipeFav);

        }
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        ViewHolder holder = (ViewHolder) mainHolder;

        final Recipe model = mContentList.get(position);
        // setting data over views
        String imgUrl = model.getImg();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext).load(imgUrl).into(holder.rImg);
        }
        holder.rName.setText(model.getRecipeName());
        holder.rMaker.setText(model.getRecipeMaker());
        isLike(model.getRecipeID(),holder.addToFav);
        holder.addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    if (holder.addToFav.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("LikesR").child(model.getRecipeID())
                                .child(firebaseUser.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("LikesR").child(model.getRecipeID())
                                .child(firebaseUser.getUid()).removeValue();

                    }
                }else{
                    System.out.println("product can't add to like");
                }
            }
        });
        holder.rMaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialog_account =
                        DialogPlus.newDialog(holder.rImg.getContext())
                                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.view_account_dialog))
                                .setExpanded(true, 1750).create();
                View myView = dialog_account.getHolderView();
                ImageView profile = myView.findViewById(R.id.profil);
                TextView userName1 = myView.findViewById(R.id.userName1);
                ImageButton previousButton = myView.findViewById(R.id.previousButton3);

                RecyclerView recyclerView = myView.findViewById((R.id.POSTView));
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false));
                recyclerView.setNestedScrollingEnabled(false);
                ((Activity)view.getContext()).registerForContextMenu(recyclerView);

                userName1.setText(model.getRecipeMaker());
                ArrayList<Recipe> recipeList = new ArrayList<>();

                DatabaseReference profile_uri = FirebaseDatabase.getInstance().getReference("Account");
                profile_uri.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot d : snapshot.getChildren()) {
                            String id = d.child("uid").getValue(String.class);
                            if (model.getId().equals(id)) {
                                String imge = d.child("image_uri").getValue(String.class);
                                if (imge.isEmpty()) {
                                    return;
                                } else {
                                    Picasso.get()
                                            .load(imge)
                                            .placeholder(R.drawable.person)
                                            .error(R.drawable.person)
                                            .into(profile);
                                }

                                Query fi = FirebaseDatabase.getInstance().getReference().child("recipe");
                                fi.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Recipe imagemodel = dataSnapshot.getValue(Recipe.class);
                                            if (id.equals(imagemodel.getId())) {
                                                recipeList.add(imagemodel);
                                                ViewAccounts viewAcounts;
                                                viewAcounts = new ViewAccounts(view.getContext(), (ArrayList<Recipe>) recipeList);
                                                recyclerView.setAdapter(viewAcounts);
                                                viewAcounts.notifyDataSetChanged();

                                            }
                                        }
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });



                dialog_account.show();
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_account.dismiss();
                    }
                });

            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus =
                        DialogPlus.newDialog(view.getContext())
                                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.recipe_details_dialog))
                                .setExpanded(true, 1950).create();
                View myView = dialogPlus.getHolderView();
                TextView recName = myView.findViewById(R.id.RecipeName);
                ImageView purl = myView.findViewById(R.id.recImage);
                TextView recDiet = myView.findViewById(R.id.RecipeDiet);
                TextView recProcedure = myView.findViewById(R.id.RecipeProcedure);
                recProcedure.setMovementMethod(new ScrollingMovementMethod());
                ImageButton previousButton = myView.findViewById(R.id.previousButton2);
                ImageButton recipeFav = myView.findViewById(R.id.recipeFav);
                isLike(model.getRecipeID(),recipeFav);
                recipeFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            if (holder.addToFav.getTag().equals("like")) {
                                FirebaseDatabase.getInstance().getReference().child("LikesR").child(model.getRecipeID())
                                        .child(firebaseUser.getUid()).setValue(true);
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("LikesR").child(model.getRecipeID())
                                        .child(firebaseUser.getUid()).removeValue();

                            }
                        }else{
                            System.out.println("product can't add to like");
                        }
                    }
                });
                ArrayAdapter<String> itemsAdopt;
                recDetails(model.getRecipeKey(),recDiet,recProcedure);
                if(model.getIngrediant() == null){
                    Toast.makeText(mContext, "the recipe not have details", Toast.LENGTH_SHORT).show();
                    return;
                }
                recName.setText(model.getRecipeName());



                List<String> stringList =new ArrayList<String>();

                ListView listView = myView.findViewById(R.id.listView);

                for (String ing : model.getIngrediant()) {
                    stringList.add(ing);
                }

                DatabaseReference ef = FirebaseDatabase.getInstance().getReference().child("Products");
                ArrayList<Product> productArrayList = new ArrayList<>();
                itemsAdopt = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1,stringList);
                listView.setAdapter(itemsAdopt);
                itemsAdopt.notifyDataSetChanged();


                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(view.getContext(),IngrdintsPage.class);
                        intent.putExtra("ingredients",stringList.get(i));
                        view.getContext().startActivity(intent);
                        return true;

                    }
                });

                Glide.with(mContext).load(model.getImg()).into(purl);
                dialogPlus.show();
                previousButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogPlus.dismiss();
                    }
                });



            }
        });


    }



    @Override
    public int getItemCount() {
        return mContentList.size();
    }
    private void recDetails(String recKey, final TextView recDiet, final TextView recProcedure){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("recipe").child(recKey);
        reference.addValueEventListener(new ValueEventListener() {
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
    }
    private void isLike(String postid, final ImageButton imageView){
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("LikesR").child(postid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_action_name);
                    imageView.setTag("like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }







//    private void  getInitViewItemDtoList(String recKey) {
//        List<ListViewItemDTO> ret = new ArrayList<ListViewItemDTO>();
//        ArrayList<String> builder = new ArrayList<>();
//        DatabaseReference ef = FirebaseDatabase.getInstance().getReference().child("Products");
//
//
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("recipe").child(recKey);
//
//        // list view ingredients
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String data = "";
//                    Recipe recipe = snapshot.child("ingrediant").getValue(Recipe.class);
//                    for (String ing : recipe.getIngrediant()) {
//                        data = ing;
//                        ListViewItemDTO dto = new ListViewItemDTO();
//                        dto.setChecked(true);
//                        builder.add(data);
//                        dto.setItemText(ing);
//                        ret.add(dto);
//                    }
//
////                    Toast.makeText(mContext,"ingrdint.getRecipeKey()"+ recipe.getRecipeKey(),Toast.LENGTH_LONG).show();
//
//                }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        } );
//        // comber products with ingredients
//        ef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(int i = 0 ; i< builder.size() ; i++) {
//                    String p = builder.get(i);
//                    for (DataSnapshot d : snapshot.getChildren()) {
//                        Product product1 = d.getValue(Product.class);
//                        String pro_comp = product1.getName();
//                        if( p.equals(pro_comp)){
//                            Toast.makeText(mContext, ""+ p, Toast.LENGTH_SHORT).show();
//                            //add to cart
//                        }
//                    }
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(mContext, ""+ error.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//    }


}

//                            ef.addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot snapshot) {
//        String t ;
//        String p = itemDto.getItemText();
//        p.toLowerCase();
////                                        list.setText(p);
//        for (DataSnapshot d : snapshot.getChildren()) {
//        Product product1 = d.getValue(Product.class);
//        String pro_comp = product1.getName();
//        pro_comp.toLowerCase();
//        if (p.equals(pro_comp)) {
//        //add to cart
////                                                if (!o.contains(p)) {
//        builder.add(p);
//        if(initItemList.contains(p)){
//        for(String item : builder){
//        System.out.println(item+"itemmmmmm");
//        list.setText(item);}}
//        addingtocart.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        HashMap<String, Object> hash = new HashMap<>();
//        hash.put("img", product1.getImage());
//        hash.put("name", pro_comp);
//        hash.put("price", product1.getPrice());
//        hash.put("productId", product1.productId);
//        hash.put("amount", product1.getAmount());
//        hash.put("productAmountInCart", "1");
//        FirebaseDatabase.getInstance().getReference().child("cart").child(firebaseUser.getUid()).child(product1.getProductId()).setValue(hash)
//        .addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void unused) {
//
//        //Toast.makeText(mActivity, "Product successfully added to cart", Toast.LENGTH_SHORT).show();
//        }
//        }).addOnFailureListener(new OnFailureListener() {
//@Override
//public void onFailure(@NonNull Exception e) {
//        Toast.makeText(mActivity, "Could not added" + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        }
//        });
//
//        }
//
//        });
//        }
//        }
//        }
//
////                                }
//
//
//@Override
//public void onCancelled(@NonNull DatabaseError error) {
//        Toast.makeText(mContext, ""+ error.getMessage(), Toast.LENGTH_LONG).show();
//
//        }
//        });






//package com.example.firbasedb;
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.orhanobut.dialogplus.DialogPlus;
//
//import java.util.ArrayList;
//
//public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//
//
//    private Context mContext;
//    private Activity mActivity;
//    private ArrayList<Recipe> mContentList;
//    FirebaseUser firebaseUser;
//
//    public void setFilteredList(ArrayList<Recipe> recipeList){
//        this.mContentList=recipeList;
//        notifyDataSetChanged();
//
//    }
//
//    public ImageAdapter(Context mContext, Activity mActivity, ArrayList<Recipe> mContentList) {
//
//        this.mContext = mContext;
//        this.mActivity = mActivity;
//        this.mContentList = mContentList;
//
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_custom_item, parent, false);
//        return new ViewHolder(view, viewType);
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private CardView cardView;
//        private ImageView rImg;
//        private TextView rName;
//        private TextView rMaker;
//        private ImageButton addToFav;
//
//
//        public ViewHolder(View itemView, int viewType) {
//            super(itemView);
//
//            // Find all views ids
//            cardView = (CardView) itemView.findViewById(R.id.recipeCard);
//            rImg = (ImageView) itemView.findViewById(R.id.recipeImg);
//            rName = (TextView) itemView.findViewById(R.id.recipeName);
//            rMaker = (TextView) itemView.findViewById(R.id.recipeMaker);
//            addToFav=(ImageButton)itemView.findViewById(R.id.recipeFav);
//
//        }
//    }
//
//        private void isLike(String postid, final ImageButton imageView){
//            final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//            DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("LikesR").child(postid);
//            ref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.child(firebaseUser.getUid()).exists()){
//                        imageView.setImageResource(R.drawable.ic_baseline_favorite_red_24);
//                        imageView.setTag("liked");
//                    }else{
//                        imageView.setImageResource(R.drawable.ic_action_name);
//                        imageView.setTag("like");
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
//        ViewHolder holder = (ViewHolder) mainHolder;
//        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//
//
//        final Recipe model = mContentList.get(position);
//        // setting data over views
//        String imgUrl = model.getImg();
//        if (imgUrl != null && !imgUrl.isEmpty()) {
//            Glide.with(mContext)
//                    .load(imgUrl)
//                    .into(holder.rImg);
//        }
//
//        holder.rName.setText(model.getRecipeName());
//        holder.rMaker.setText(model.getRecipeMaker());
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DialogPlus dialogPlus =
//                        DialogPlus.newDialog(holder.rImg.getContext())
//                                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.recipe_details_dialog))
//                                .setExpanded(true, 1750).create();
//                View myView = dialogPlus.getHolderView();
//                TextView recName = myView.findViewById(R.id.RecipeName);
//                ImageView purl = myView.findViewById(R.id.recImage);
//                TextView recDiet = myView.findViewById(R.id.RecipeDiet);
//                TextView recProcedure = myView.findViewById(R.id.RecipeProcedure);
//                ImageButton previousButton = myView.findViewById(R.id.previousButton2);
//                String recKey;
//                recDetails(model.getRecipeKey(),recDiet,recProcedure);
//
//                recName.setText(model.getRecipeName());
////                recDiet.setText(model.getRecipeDiet());
////                recProcedure.setText(model.getRecipeProcedure());
//                Glide.with(mContext).load(model.getImg()).into(purl);
//                dialogPlus.show();
//                previousButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialogPlus.dismiss();
//                    }
//                });
//
//
//            }
//        });
//
//
//
//        isLike(model.getRcipeID(), holder.addToFav);
//        holder.addToFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.addToFav.getTag().equals("like")) {
//                    FirebaseDatabase.getInstance().getReference().child("LikesR").child(model.getRcipeID())
//                            .child(firebaseUser.getUid()).setValue(true);
//                } else {
//                    FirebaseDatabase.getInstance().getReference().child("LikesR").child(model.getRcipeID())
//                            .child(firebaseUser.getUid()).removeValue();
//
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mContentList.size();
//    }
//    private void recDetails(String recKey, final TextView recDiet, final TextView recProcedure){
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("recipe").child(recKey);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String rpro= snapshot.child("howmake").getValue(String.class);
//                recProcedure.setText(rpro);
//                String rdiet= snapshot.child("dite").getValue(String.class);
//                recDiet.setText(rdiet);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//
//
//}

//=======================================

//private Context mContext;
//    private Activity mActivity;
//    private ArrayList<Recipe> mContentList;
//
//
//    public void setFilteredList(ArrayList<Recipe> recipeList) {
//        this.mContentList = recipeList;
//        notifyDataSetChanged();
//
//    }
//
//    public ImageAdapter(Context mContext, ArrayList<Recipe> mContentList) {
//
//        this.mContext = mContext;
//        this.mContentList = mContentList;
//
//    }
//
//    public ImageAdapter(Context mContext) {
//
//        this.mContext = mContext;
//
//    }
//
//    public ImageAdapter(Context mContext, Activity mActivity, ArrayList<Recipe> mContentList) {
//        this.mContext = mContext;
//        this.mActivity = mActivity;
//        this.mContentList = mContentList;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_custom_item, parent, false);
//        return new ImageAdapter.ViewHolder(view);
//    }
//
//public static class ViewHolder extends RecyclerView.ViewHolder {
//    private CardView cardView;
//    private ImageView rImg;
//    private TextView rName;
//    private TextView rMaker;
//
//    public ViewHolder(View itemView) {
//        super(itemView);
//
//        // Find all views ids
//        cardView = (CardView) itemView.findViewById(R.id.recipeCard);
//        rImg = (ImageView) itemView.findViewById(R.id.recipeImg);
//        rName = (TextView) itemView.findViewById(R.id.recipeName);
//        rMaker = (TextView) itemView.findViewById(R.id.recipeMaker);
//
//    }
//
//}
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
//        ViewHolder holder = (ViewHolder) mainHolder;
//
//        final Recipe model = mContentList.get(position);
//        // setting data over views
//        String imgUrl = model.getImg();
//        if (imgUrl != null && !imgUrl.isEmpty()) {
//            Glide.with(mContext).load(imgUrl).into(holder.rImg);
//        }
//
//        holder.rName.setText(model.getRecipeName());
//        holder.rMaker.setText(model.getRecipeMaker());
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final DialogPlus dialogPlus =
//                        DialogPlus.newDialog(holder.rImg.getContext())
//                                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.recipe_details_dialog))
//                                .setExpanded(true, 1750).create();
//                View myView = dialogPlus.getHolderView();
//                TextView recName = myView.findViewById(R.id.RecipeName);
//                ImageView purl = myView.findViewById(R.id.recImage);
//                //TextView  listing = myView.findViewById(R.id.listing);
//                //Button addingtocart = myView.findViewById(R.id.addingtocart);
//
//
//                TextView recDiet = myView.findViewById(R.id.RecipeDiet);
//                TextView recProcedure = myView.findViewById(R.id.RecipeProcedure);
//                ImageButton previousButton = myView.findViewById(R.id.previousButton2);
//                String recKey;
//
//                recDetails(model.getRecipeKey(), recDiet, recProcedure);
//                StringBuilder builder = new StringBuilder();
//                if (model.getIngrediant().isEmpty())
//                    return;
//
//                recName.setText(model.getRecipeName());
////                for (int i =0 ; i<model.getIngrediant().size();i++){
////                      builder.append(model.getIngrediant().get(i)+",");
////                        }
//                //listing.setText(builder.toString());
//
//
////                recDiet.setText(model.getRecipeDiet());
////                recProcedure.setText(model.getRecipeProcedure());
//                Glide.with(mContext).load(model.getImg()).into(purl);
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
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mContentList.size();
//    }
//
//    private void recDetails(String recKey, final TextView recDiet, final TextView recProcedure) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("recipe").child(recKey);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String rpro = snapshot.child("howmake").getValue(String.class);
//                recProcedure.setText(rpro);
//                String rdiet = snapshot.child("dite").getValue(String.class);
//                recDiet.setText(rdiet);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }