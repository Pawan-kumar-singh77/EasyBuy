package com.example.easybuy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easybuy.R;
import com.example.easybuy.models.AddressModel;
import com.example.easybuy.models.NewProductsModel;
import com.example.easybuy.models.PopularProductsModel;
import com.example.easybuy.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView rating,name,description,price,quantity;
    Button addToCart,buyNow;
    ImageView addItems,removeItems;
    Toolbar toolbar;

    int totalQuantity = 1, totalPrice = 0;

    NewProductsModel newProductsModel = null;

    PopularProductsModel popularProductsModel = null;

    ShowAllModel showAllModel = null;

    FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        toolbar = findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");

        if(obj instanceof NewProductsModel){
            newProductsModel = (NewProductsModel) obj;
        }

        else if(obj instanceof PopularProductsModel){
            popularProductsModel = (PopularProductsModel) obj;
        }

        else if(obj instanceof ShowAllModel){
            showAllModel = (ShowAllModel) obj;
        }

        detailedImg = findViewById(R.id.detailed_img);
        rating = findViewById(R.id.rating);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);
        addToCart = findViewById(R.id.add_to_cart);
        buyNow = findViewById(R.id.buy_now);
        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);
        quantity = findViewById(R.id.quantity);

        if(newProductsModel!=null){

            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));
            rating.setText(newProductsModel.getRating());

            totalPrice = newProductsModel.getPrice() * totalQuantity;

        }

        if(popularProductsModel!=null){

            Glide.with(getApplicationContext()).load(popularProductsModel.getImg_url()).into(detailedImg);
            name.setText(popularProductsModel.getName());
            description.setText(popularProductsModel.getDescription());
            price.setText(String.valueOf(popularProductsModel.getPrice()));
            rating.setText(popularProductsModel.getRating());

            totalPrice = popularProductsModel.getPrice() * totalQuantity;

        }

        if(showAllModel!=null){

            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailedImg);
            name.setText(showAllModel.getName());
            description.setText(showAllModel.getDescription());
            price.setText(String.valueOf(showAllModel.getPrice()));
            rating.setText(showAllModel.getRating());

            totalPrice = showAllModel.getPrice() * totalQuantity;

        }

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedActivity.this, AddressActivity.class);

                if(newProductsModel!=null){
                    intent.putExtra("item",newProductsModel);
                }
                if(popularProductsModel!=null){
                    intent.putExtra("item",popularProductsModel);
                }
                if(showAllModel!=null){
                    intent.putExtra("item",showAllModel);
                }

                startActivity(intent);
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoCart();
            }
        });

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalQuantity < 10){

                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(newProductsModel!=null){
                        totalPrice = newProductsModel.getPrice() * totalQuantity;
                    }
                    if(popularProductsModel!=null){
                        totalPrice = popularProductsModel.getPrice() * totalQuantity;
                    }
                    if(showAllModel!=null){
                        totalPrice = showAllModel.getPrice() * totalQuantity;
                    }

                }
                else{
                    Toast.makeText(DetailedActivity.this,"Number of Items can not be more than 10",Toast.LENGTH_SHORT).show();
                }

            }
        });
        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalQuantity > 1){

                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(newProductsModel!=null){
                        totalPrice = newProductsModel.getPrice() * totalQuantity;
                    }
                    if(popularProductsModel!=null){
                        totalPrice = popularProductsModel.getPrice() * totalQuantity;
                    }
                    if(showAllModel!=null){
                        totalPrice = showAllModel.getPrice() * totalQuantity;
                    }

                }
                else{
                    Toast.makeText(DetailedActivity.this,"Number of Items can not be less than one",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void addtoCart() {

        String saveCurrentTime,saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final HashMap<String,Object> cartMap =  new HashMap<>();

        cartMap.put("productName",name.getText().toString());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("totalQuantity",quantity.getText().toString());
        cartMap.put("totalPrice",totalPrice);

        firestore.collection("AddToCart").document(firebaseAuth.getCurrentUser().getUid())
                .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(DetailedActivity.this,"Added To Cart",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}