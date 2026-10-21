
package com.bankanku.smartpantrymanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder, int position) {

        Recipe recipe = recipes.get(position);

        holder.tvName.setText(recipe.getName());

        holder.tvInfo.setText(
                recipe.getCategory() + " • " +
                        recipe.getPreparationTime() + " minutes"
        );

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    RecipeDetailActivity.class
            );

            intent.putExtra("recipe_id", recipe.getId());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvInfo;
        Button btnView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvInfo = itemView.findViewById(R.id.tvRecipeInfo);
            btnView = itemView.findViewById(R.id.btnViewRecipe);
        }
    }
}
