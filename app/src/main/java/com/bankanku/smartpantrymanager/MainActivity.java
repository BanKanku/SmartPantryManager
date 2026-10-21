
package com.bankanku.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private IngredientAdapter ingredientAdapter;
    private RecyclerView recyclerViewIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect to SQLite database
        databaseHelper = new DatabaseHelper(this);

        // Set up pantry RecyclerView
        recyclerViewIngredients =
                findViewById(R.id.recyclerViewIngredients);

        recyclerViewIngredients.setLayoutManager(
                new LinearLayoutManager(this)
        );

        ingredientAdapter = new IngredientAdapter(
                new ArrayList<>(),
                databaseHelper
        );

        recyclerViewIngredients.setAdapter(ingredientAdapter);

        // Add Ingredient button
        Button btnAddIngredient =
                findViewById(R.id.btnAddIngredient);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        // Suggested Recipes button
        Button btnSuggestedRecipes =
                findViewById(R.id.btnSuggestedRecipes);

        btnSuggestedRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });

        // Settings button will be implemented next
        Button btnSettings = findViewById(R.id.btnSettings);

        btnSettings.setOnClickListener(v -> {
            android.widget.Toast.makeText(
                    MainActivity.this,
                    "Settings coming soon",
                    android.widget.Toast.LENGTH_SHORT
            ).show();
        });

        loadIngredients();
    }

    private void loadIngredients() {
        ingredientAdapter.setIngredients(
                databaseHelper.getAllIngredients()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && ingredientAdapter != null) {
            loadIngredients();
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
