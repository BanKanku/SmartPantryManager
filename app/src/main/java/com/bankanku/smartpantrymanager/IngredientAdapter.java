
package com.bankanku.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private final List<Ingredient> ingredients = new ArrayList<>();

    public IngredientAdapter(List<Ingredient> initialIngredients) {
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

        // Editing will be connected in the next development step.
        holder.btnEdit.setOnClickListener(v -> {
            // TODO: Open Edit Ingredient screen.
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

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvIngredientQuantity);
            btnEdit = itemView.findViewById(R.id.btnEditIngredient);
        }
    }
}
