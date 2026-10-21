
package com.bankanku.smartpantrymanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private TextView tvPantryCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        tvPantryCount = findViewById(R.id.tvPantryCount);

        Button btnClear = findViewById(R.id.btnClearPantry);
        Button btnBack = findViewById(R.id.btnSettingsBack);

        updatePantryCount();

        btnClear.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear Pantry")
                        .setMessage(
                                "Delete all pantry ingredients? " +
                                        "This action cannot be undone."
                        )
                        .setPositiveButton("Clear", (dialog, which) -> {
                            int deleted = databaseHelper
                                    .getWritableDatabase()
                                    .delete("ingredients", null, null);

                            updatePantryCount();

                            Toast.makeText(
                                    this,
                                    deleted + " ingredients deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        btnBack.setOnClickListener(v -> finish());
    }

    private void updatePantryCount() {
        int count = databaseHelper.getAllIngredients().size();
        tvPantryCount.setText("Pantry ingredients: " + count);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && tvPantryCount != null) {
            updatePantryCount();
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
