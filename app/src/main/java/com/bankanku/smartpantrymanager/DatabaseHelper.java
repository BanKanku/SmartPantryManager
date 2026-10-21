
package com.bankanku.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL)"
        );

        createRecipeTables(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }

    private void createRecipeTables(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL, " +
                        "preparation_time INTEGER NOT NULL, " +
                        "category TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id))"
        );
    }

    // PANTRY CRUD

    public long addIngredient(String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        return getWritableDatabase().insert(
                "ingredients", null, values
        );
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, name, quantity, unit " +
                        "FROM ingredients ORDER BY name COLLATE NOCASE",
                null)) {

            while (cursor.moveToNext()) {
                ingredients.add(new Ingredient(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getString(3)
                ));
            }
        }

        return ingredients;
    }

    public int updateIngredient(int id, String name,
                                double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        return getWritableDatabase().update(
                "ingredients",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteIngredient(int id) {
        return getWritableDatabase().delete(
                "ingredients",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // RECIPE SEEDING

    private long insertRecipe(SQLiteDatabase db, String name,
                              String instructions, int minutes,
                              String category) {

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("instructions", instructions);
        values.put("preparation_time", minutes);
        values.put("category", category);

        return db.insert("recipes", null, values);
    }

    private void addRequired(SQLiteDatabase db, long recipeId,
                             String name, double quantity, String unit) {

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);

        db.insert("recipe_ingredients", null, values);
    }

    private void seedRecipes(SQLiteDatabase db) {
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipes", null)) {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        }

        long id;

        id = insertRecipe(db, "Scrambled Eggs",
                "Beat the eggs. Heat butter in a pan. " +
                        "Cook gently while stirring until set.",
                10, "Breakfast");
        addRequired(db, id, "eggs", 2, "pieces");
        addRequired(db, id, "butter", 10, "g");

        id = insertRecipe(db, "Boiled Eggs",
                "Place eggs in water. Bring to a boil. " +
                        "Cook for 8 to 10 minutes, then cool.",
                12, "Breakfast");
        addRequired(db, id, "eggs", 2, "pieces");

        id = insertRecipe(db, "Fried Eggs",
                "Heat oil in a pan. Crack in the eggs " +
                        "and fry until cooked to your preference.",
                8, "Breakfast");
        addRequired(db, id, "eggs", 2, "pieces");
        addRequired(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Buttered Toast",
                "Toast the bread until golden. " +
                        "Spread butter on each slice.",
                5, "Breakfast");
        addRequired(db, id, "bread", 2, "pieces");
        addRequired(db, id, "butter", 10, "g");

        id = insertRecipe(db, "Peanut Butter Toast",
                "Toast the bread and spread peanut butter.",
                5, "Breakfast");
        addRequired(db, id, "bread", 2, "pieces");
        addRequired(db, id, "peanut butter", 2, "tbsp");

        id = insertRecipe(db, "Oatmeal",
                "Combine oats and milk. Simmer while " +
                        "stirring until the oats soften.",
                10, "Breakfast");
        addRequired(db, id, "oats", 50, "g");
        addRequired(db, id, "milk", 250, "ml");

        id = insertRecipe(db, "Banana Smoothie",
                "Blend banana and milk until smooth.",
                5, "Breakfast");
        addRequired(db, id, "banana", 1, "pieces");
        addRequired(db, id, "milk", 250, "ml");

        id = insertRecipe(db, "Cheese Sandwich",
                "Place cheese between bread slices and serve.",
                5, "Lunch");
        addRequired(db, id, "bread", 2, "pieces");
        addRequired(db, id, "cheese", 40, "g");

        id = insertRecipe(db, "Tomato Sandwich",
                "Slice the tomato. Place between " +
                        "bread slices and serve.",
                5, "Lunch");
        addRequired(db, id, "bread", 2, "pieces");
        addRequired(db, id, "tomato", 1, "pieces");

        id = insertRecipe(db, "Egg Sandwich",
                "Boil the eggs, peel and slice them. " +
                        "Place between bread slices.",
                15, "Lunch");
        addRequired(db, id, "bread", 2, "pieces");
        addRequired(db, id, "eggs", 2, "pieces");

        id = insertRecipe(db, "Plain Rice",
                "Rinse rice. Add water, bring to a boil, " +
                        "cover and simmer until tender.",
                25, "Lunch");
        addRequired(db, id, "rice", 200, "g");
        addRequired(db, id, "water", 400, "ml");

        id = insertRecipe(db, "Tomato Rice",
                "Cook rice in water. Fry chopped tomato " +
                        "in oil and stir into the cooked rice.",
                30, "Lunch");
        addRequired(db, id, "rice", 200, "g");
        addRequired(db, id, "water", 400, "ml");
        addRequired(db, id, "tomato", 2, "pieces");
        addRequired(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Pap",
                "Bring water to a boil. Gradually add " +
                        "maize meal while stirring. Cover and cook.",
                30, "Dinner");
        addRequired(db, id, "maize meal", 200, "g");
        addRequired(db, id, "water", 500, "ml");

        id = insertRecipe(db, "Chakalaka",
                "Fry onion and carrot in oil. Add beans " +
                        "and tomatoes, then simmer until cooked.",
                30, "Dinner");
        addRequired(db, id, "onion", 1, "pieces");
        addRequired(db, id, "carrot", 2, "pieces");
        addRequired(db, id, "beans", 200, "g");
        addRequired(db, id, "tomato", 2, "pieces");
        addRequired(db, id, "oil", 2, "tbsp");

        id = insertRecipe(db, "Fried Cabbage",
                "Chop cabbage and onion. Fry in oil " +
                        "until softened and cooked.",
                20, "Dinner");
        addRequired(db, id, "cabbage", 300, "g");
        addRequired(db, id, "onion", 1, "pieces");
        addRequired(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Mashed Potatoes",
                "Peel and boil potatoes until soft. " +
                        "Mash with milk and butter.",
                30, "Dinner");
        addRequired(db, id, "potato", 4, "pieces");
        addRequired(db, id, "milk", 100, "ml");
        addRequired(db, id, "butter", 20, "g");

        id = insertRecipe(db, "Pasta with Tomato Sauce",
                "Boil pasta until tender. Cook tomatoes " +
                        "in oil and combine with pasta.",
                25, "Dinner");
        addRequired(db, id, "pasta", 200, "g");
        addRequired(db, id, "tomato", 3, "pieces");
        addRequired(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "Chicken and Rice",
                "Cook rice in water. Fry chicken in oil " +
                        "until fully cooked, then serve together.",
                40, "Dinner");
        addRequired(db, id, "chicken", 300, "g");
        addRequired(db, id, "rice", 200, "g");
        addRequired(db, id, "water", 400, "ml");
        addRequired(db, id, "oil", 1, "tbsp");

        id = insertRecipe(db, "French Toast",
                "Beat eggs with milk. Dip bread into " +
                        "the mixture and fry in butter until golden.",
                15, "Breakfast");
        addRequired(db, id, "bread", 2, "pieces");
        addRequired(db, id, "eggs", 2, "pieces");
        addRequired(db, id, "milk", 50, "ml");
        addRequired(db, id, "butter", 10, "g");

        id = insertRecipe(db, "Fruit Salad",
                "Wash and chop the fruits. " +
                        "Combine in a bowl and serve.",
                10, "Snack");
        addRequired(db, id, "apple", 1, "pieces");
        addRequired(db, id, "banana", 1, "pieces");
        addRequired(db, id, "orange", 1, "pieces");
    }

    // RECIPE QUERIES

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, name, instructions, " +
                        "preparation_time, category " +
                        "FROM recipes ORDER BY name", null)) {

            while (cursor.moveToNext()) {
                recipes.add(new Recipe(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                ));
            }
        }

        return recipes;
    }

    public Recipe getRecipeById(int recipeId) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, name, instructions, " +
                        "preparation_time, category " +
                        "FROM recipes WHERE id = ?",
                new String[]{String.valueOf(recipeId)})) {

            if (cursor.moveToFirst()) {
                return new Recipe(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
            }
        }

        return null;
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredient> required = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id, recipe_id, ingredient_name, " +
                        "quantity, unit FROM recipe_ingredients " +
                        "WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)})) {

            while (cursor.moveToNext()) {
                required.add(new RecipeIngredient(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                ));
            }
        }

        return required;
    }

    // MATCHING ENGINE

    private String normalizeName(String name) {
        String value = name.trim().toLowerCase(Locale.ROOT);

        if (value.endsWith("ies") && value.length() > 3) {
            return value.substring(0, value.length() - 3) + "y";
        }

        if (value.endsWith("oes")) {
            return value.substring(0, value.length() - 2);
        }

        if (value.endsWith("s") &&
                !value.endsWith("ss") &&
                value.length() > 3) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

    private double convertQuantity(double quantity,
                                   String fromUnit, String toUnit) {

        if (fromUnit.equalsIgnoreCase(toUnit)) {
            return quantity;
        }

        String from = fromUnit.toLowerCase(Locale.ROOT);
        String to = toUnit.toLowerCase(Locale.ROOT);

        if (from.equals("kg") && to.equals("g")) {
            return quantity * 1000;
        }

        if (from.equals("g") && to.equals("kg")) {
            return quantity / 1000;
        }

        if (from.equals("l") && to.equals("ml")) {
            return quantity * 1000;
        }

        if (from.equals("ml") && to.equals("l")) {
            return quantity / 1000;
        }

        if (from.equals("cups") && to.equals("ml")) {
            return quantity * 250;
        }

        if (from.equals("ml") && to.equals("cups")) {
            return quantity / 250;
        }

        if (from.equals("tbsp") && to.equals("ml")) {
            return quantity * 15;
        }

        if (from.equals("ml") && to.equals("tbsp")) {
            return quantity / 15;
        }

        if (from.equals("tsp") && to.equals("ml")) {
            return quantity * 5;
        }

        if (from.equals("ml") && to.equals("tsp")) {
            return quantity / 5;
        }

        return -1;
    }

    public boolean canMakeRecipe(Recipe recipe) {
        List<Ingredient> pantry = getAllIngredients();
        List<RecipeIngredient> required =
                getRecipeIngredients(recipe.getId());

        if (required.isEmpty()) {
            return false;
        }

        for (RecipeIngredient requirement : required) {
            double available = 0;

            for (Ingredient item : pantry) {
                if (!normalizeName(item.getName()).equals(
                        normalizeName(requirement.getIngredientName()))) {
                    continue;
                }

                double converted = convertQuantity(
                        item.getQuantity(),
                        item.getUnit(),
                        requirement.getUnit()
                );

                if (converted >= 0) {
                    available += converted;
                }
            }

            if (available + 0.000001 < requirement.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> suggestions = new ArrayList<>();

        for (Recipe recipe : getAllRecipes()) {
            if (canMakeRecipe(recipe)) {
                suggestions.add(recipe);
            }
        }

        return suggestions;
    }
}
