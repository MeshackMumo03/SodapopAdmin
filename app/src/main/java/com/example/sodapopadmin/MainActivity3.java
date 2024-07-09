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

public class MainActivity3 extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView reportTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        db = FirebaseFirestore.getInstance();
        reportTextView = findViewById(R.id.report_text_view);

        generateReports();
    }

    private void generateReports() {
        db.collection("orders").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder report = new StringBuilder();
                            double totalAmount = 0.0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String branch = document.getString("branch");
                                String brand = document.getString("brand");
                                String quantity = document.getString("quantity");
                                double price = document.getDouble("price"); // assuming price is stored

                                double amount = price * Integer.parseInt(quantity);
                                totalAmount += amount;

                                report.append("Branch: ").append(branch)
                                        .append("\nBrand: ").append(brand)
                                        .append("\nQuantity: ").append(quantity)
                                        .append("\nAmount: ").append(amount)
                                        .append("\n\n");
                            }
                            report.append("Total Amount: ").append(totalAmount);
                            reportTextView.setText(report.toString());
                        } else {
                            reportTextView.setText("Error generating reports.");
                        }
                    }
                });
    }
}
