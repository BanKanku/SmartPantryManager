
package com.bankanku.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final DatabaseHelper databaseHelper;

    public IngredientAdapter(List<Ingredient> initialIngredients,
                             DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        setIngredients(initialIngredients);
    }

    public void setIngredients(List<Ingredient> updatedIngredients) {
        ingredients.clear();

        if (updatedIngredients != null) {
            ingredients.addAll(updatedIngredients);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull IngredientViewHolder holder, int position) {

        Ingredient ingredient = ingredients.get(position);

        holder.tvName.setText(ingredient.getName());

        String quantity = ingredient.getQuantity()
                + " " + ingredient.getUnit();

        holder.tvQuantity.setText(quantity);

        // Edit an existing ingredient
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    AddIngredientActivity.class
            );

            intent.putExtra("ingredient_id", ingredient.getId());
            intent.putExtra("ingredient_name", ingredient.getName());
            intent.putExtra("ingredient_quantity", ingredient.getQuantity());
            intent.putExtra("ingredient_unit", ingredient.getUnit());

            holder.itemView.getContext().startActivity(intent);
        });

        // Delete an ingredient after confirmation
        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Ingredient")
                    .setMessage(
                            "Are you sure you want to delete "
                                    + ingredient.getName() + "?"
                    )
                    .setPositiveButton("Delete", (dialog, which) -> {

                        int rowsDeleted = databaseHelper.deleteIngredient(
                                ingredient.getId()
                        );

                        if (rowsDeleted > 0) {
                            setIngredients(databaseHelper.getAllIngredients());

                            Toast.makeText(
                                    holder.itemView.getContext(),
                                    "Ingredient deleted successfully",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    holder.itemView.getContext(),
                                    "Failed to delete ingredient",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) ->
                            dialog.dismiss()
                    )
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        Button btnEdit;
        Button btnDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvIngredientQuantity);
            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
