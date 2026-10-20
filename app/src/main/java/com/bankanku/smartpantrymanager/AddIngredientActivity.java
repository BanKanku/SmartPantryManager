
package com.bankanku.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddIngredientActivity extends Activity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private Spinner spinnerUnit;
    private DatabaseHelper databaseHelper;

    private boolean isEditMode = false;
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        databaseHelper = new DatabaseHelper(this);

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancel);

        String[] units = {
                "pieces", "g", "kg", "ml", "L",
                "cups", "tbsp", "tsp"
        };

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

        // Check whether an existing ingredient is being edited.
        if (getIntent().hasExtra("ingredient_id")) {
            ingredientId = getIntent().getIntExtra(
                    "ingredient_id", -1
            );

            isEditMode = ingredientId > 0;
        }

        if (isEditMode) {
            String name = getIntent().getStringExtra(
                    "ingredient_name"
            );

            double quantity = getIntent().getDoubleExtra(
                    "ingredient_quantity", 0
            );

            String unit = getIntent().getStringExtra(
                    "ingredient_unit"
            );

            etIngredientName.setText(name);
            etQuantity.setText(String.valueOf(quantity));

            if (unit != null) {
                int unitPosition = unitAdapter.getPosition(unit);

                if (unitPosition >= 0) {
                    spinnerUnit.setSelection(unitPosition);
                }
            }

            btnSave.setText("Update Ingredient");




            setTitle("Edit Ingredient");
        } else {
            setTitle("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveIngredient() {
        String name = etIngredientName.getText()
                .toString().trim();

        String quantityText = etQuantity.getText()
                .toString().trim();

        String unit = spinnerUnit.getSelectedItem()
                .toString();

        if (TextUtils.isEmpty(name)) {
            etIngredientName.setError(
                    "Ingredient name is required"
            );
            etIngredientName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityText)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            etQuantity.setError(
                    "Quantity must be greater than zero"
            );
            etQuantity.requestFocus();
            return;
        }

        boolean success;

        if (isEditMode) {
            int rowsUpdated = databaseHelper.updateIngredient(
                    ingredientId,
                    name,
                    quantity,
                    unit
            );

            success = rowsUpdated > 0;
        } else {
            long newId = databaseHelper.addIngredient(
                    name,
                    quantity,
                    unit
            );

            success = newId != -1;
        }

        if (success) {
            Toast.makeText(
                    this,
                    isEditMode
                            ? "Ingredient updated successfully"
                            : "Ingredient saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(
                    this,
                    "Failed to save ingredient",
                    Toast.LENGTH_SHORT
            ).show();
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
