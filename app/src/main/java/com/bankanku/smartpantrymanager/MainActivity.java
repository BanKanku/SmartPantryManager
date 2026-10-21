
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        RecyclerView recyclerView =
                findViewById(R.id.recyclerViewIngredients);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        ingredientAdapter = new IngredientAdapter(
                new ArrayList<>(),
                databaseHelper
        );

        recyclerView.setAdapter(ingredientAdapter);

        Button btnAdd = findViewById(R.id.btnAddIngredient);
        btnAdd.setOnClickListener(v -> startActivity(
                new Intent(this, AddIngredientActivity.class)
        ));

        Button btnRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnRecipes.setOnClickListener(v -> startActivity(
                new Intent(this, SuggestedRecipesActivity.class)
        ));

        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> startActivity(
                new Intent(this, SettingsActivity.class)
        ));

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
