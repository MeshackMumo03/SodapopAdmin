package com.example.sodapopadmin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity2 extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView ordersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db = FirebaseFirestore.getInstance();
        ordersTextView = findViewById(R.id.orders_text_view);

        loadOrders();
    }

    private void loadOrders() {
        db.collection("orders").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder orders = new StringBuilder();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String branch = document.getString("branch");
                                String brand = document.getString("brand");
                                String quantity = document.getString("quantity");
                                String customerId = document.getString("customerId");

                                orders.append("Branch: ").append(branch)
                                        .append("\nBrand: ").append(brand)
                                        .append("\nQuantity: ").append(quantity)
                                        .append("\nCustomer ID: ").append(customerId)
                                        .append("\n\n");
                            }
                            ordersTextView.setText(orders.toString());
                        } else {
                            ordersTextView.setText("Error getting orders.");
                        }
                    }
                });
    }
}
