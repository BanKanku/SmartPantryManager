
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface OnIngredientsChangedListener {
        void onIngredientsChanged();
    }

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final DatabaseHelper databaseHelper;
    private final OnIngredientsChangedListener listener;

    private final DecimalFormat quantityFormat = new DecimalFormat(
            "0.##",
            DecimalFormatSymbols.getInstance(Locale.US)
    );

    public IngredientAdapter(
            List<Ingredient> initialIngredients,
            DatabaseHelper databaseHelper,
            OnIngredientsChangedListener listener) {

        this.databaseHelper = databaseHelper;
        this.listener = listener;
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

        String quantity = quantityFormat.format(
                ingredient.getQuantity()
        ) + " " + ingredient.getUnit();

        holder.tvQuantity.setText(quantity);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    AddIngredientActivity.class
            );

            intent.putExtra("ingredient_id", ingredient.getId());
            intent.putExtra("ingredient_name", ingredient.getName());
            intent.putExtra(
                    "ingredient_quantity",
                    ingredient.getQuantity()
            );
            intent.putExtra("ingredient_unit", ingredient.getUnit());

            holder.itemView.getContext().startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v ->
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Ingredient")
                        .setMessage(
                                "Are you sure you want to delete "
                                        + ingredient.getName() + "?"
                        )
                        .setPositiveButton("Delete", (dialog, which) -> {
                            int deleted = databaseHelper.deleteIngredient(
                                    ingredient.getId()
                            );

                            if (deleted > 0) {
                                if (listener != null) {
                                    listener.onIngredientsChanged();
                                } else {
                                    setIngredients(
                                            databaseHelper.getAllIngredients()
                                    );
                                }

                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Ingredient deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        holder.itemView.getContext(),
                                        "Unable to delete ingredient",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        Button btnEdit;
        Button btnDelete;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvIngredientQuantity);
            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
