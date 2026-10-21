
package com.bankanku.smartpantrymanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecipeDetailActivity extends Activity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvInfo = findViewById(R.id.tvDetailInfo);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvInstructions = findViewById(R.id.tvDetailInstructions);

        Button btnBack = findViewById(R.id.btnDetailBack);
        btnBack.setOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra("recipe_id", -1);

        if (recipeId == -1) {
            Toast.makeText(
                    this,
                    "Recipe not found",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        Recipe recipe = databaseHelper.getRecipeById(recipeId);

        if (recipe == null) {
            Toast.makeText(
                    this,
                    "Recipe not found",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        tvName.setText(recipe.getName());

        tvInfo.setText(
                recipe.getCategory() + " • " +
                        recipe.getPreparationTime() + " minutes"
        );

        tvInstructions.setText(recipe.getInstructions());

        List<RecipeIngredient> ingredients =
                databaseHelper.getRecipeIngredients(recipeId);

        StringBuilder ingredientText = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {
            ingredientText.append("• ")
                    .append(ingredient.getIngredientName())
                    .append(" - ")
                    .append(ingredient.getQuantity())
                    .append(" ")
                    .append(ingredient.getUnit())
                    .append("\n");
        }

        tvIngredients.setText(ingredientText.toString());
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}

