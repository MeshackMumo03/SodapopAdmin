package com.example.sodapopadmin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReportsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView reportTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        db = FirebaseFirestore.getInstance();
        reportTextView = findViewById(R.id.report_text_view);

        generateReports();
    }

    private void generateReports() {
        db.collection("orders").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Double> branchSales = new HashMap<>();
                        Set<String> customers = new HashSet<>();
                        double totalSales = 0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String branch = document.getString("branch");
                            double amount = document.getDouble("amount");
                            String customerId = document.getString("customerId");

                            branchSales.put(branch, branchSales.getOrDefault(branch, 0.0) + amount);
                            customers.add(customerId);
                            totalSales += amount;
                        }

                        StringBuilder report = new StringBuilder();
                        report.append("Total Customers: ").append(customers.size()).append("\n\n");
                        report.append("Branch Sales:\n");
                        for (Map.Entry<String, Double> entry : branchSales.entrySet()) {
                            report.append(entry.getKey()).append(": $").append(String.format("%.2f", entry.getValue())).append("\n");
                        }
                        report.append("\nTotal Sales: $").append(String.format("%.2f", totalSales));

                        reportTextView.setText(report.toString());
                    } else {
                        reportTextView.setText("Error generating reports.");
                    }
                });
    }
}