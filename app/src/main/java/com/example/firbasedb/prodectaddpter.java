package com.example.firbasedb;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class prodectaddpter extends FirebaseRecyclerAdapter<classprodect,prodectaddpter.myviewholder> {



   public prodectaddpter(@NonNull FirebaseRecyclerOptions options) {
      super(options);
   }

   @Override
   protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull classprodect model) {
      holder.item_title.setText(model.getNameitem());
      holder.priceitem.setText(model.getPricitem());
      holder.quantetitem.setText(model.getQuanteitem());
      Glide.with(holder.imageProdect.getContext())
              .load(model.getUriitem())
              .placeholder(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark)
              .error(com.google.firebase.storage.R.drawable.common_google_signin_btn_icon_dark_normal)
              .into(holder.imageProdect);
   }

   @NonNull
   @Override
   public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_incart,parent,false);

      return new myviewholder(view);
   }

   class myviewholder extends RecyclerView.ViewHolder{

   ImageView imageProdect;
   TextView item_title , priceitem, quantetitem,totleprice;
   Button plus,mines;
Double cost =0.0 ;
Double finalcost = 0.0;

int quntity =0;
   public myviewholder(@NonNull View itemView) {
      super(itemView);
      imageProdect=(ImageView) itemView.findViewById(R.id.prodectimage);
      item_title = (TextView) itemView.findViewById(R.id.item_title);
      priceitem=(TextView) itemView.findViewById(R.id.priceitem);
      quantetitem =(TextView) itemView.findViewById(R.id.quantetitem);
      plus = (Button) itemView.findViewById(R.id.addprodect);
      mines= (Button) itemView.findViewById(R.id.remove);
      totleprice = (TextView)itemView.findViewById(R.id.price);
      classprodect classprodect = new classprodect();
      String pric_count = classprodect.getPricitem();
      String quntit = classprodect.getQuanteitem();
//      String image = classprodect.getUriitem();
//      String title_prodect = classprodect.getNameitem();
//      String totle_pric = classprodect.getTotlepriceitem();

//item_title.setText(""+title_prodect);
//priceitem.setText(""+pric_count);
//quantetitem.setText(""+quntit);
//      totleprice.setText(""+finalcost);



itemView.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
      Log.d("demo","remove button");
      quntity--;
      cost  =cost-cost;
      quantetitem.setText(""+quntity);
      priceitem.setText(""+cost);

//      finalcost = Double.parseDouble(pric_count.replaceAll("RS",""));

//finalcost=finalcost + cost;




   }
});
itemView.findViewById(R.id.addprodect).setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
      Log.d("demo","plus button");
     // cost = Double.parseDouble(pric_count);
      quntity++;
      cost  =cost+cost;
      quantetitem.setText(""+quntity);
      priceitem.setText(""+cost);

   }
});
   }
}



}