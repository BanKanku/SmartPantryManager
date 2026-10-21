
package com.bankanku.smartpantrymanager;

import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    private static final double EPSILON = 0.000001;

    private RecipeMatcher() {
        // Utility class
    }

    private static String normalizeName(String name) {
        if (name == null) {
            return "";
        }

        String value = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (value.equals("eggs")) {
            return "egg";
        }

        if (value.equals("potatoes")) {
            return "potato";
        }

        if (value.equals("tomatoes")) {
            return "tomato";
        }

        if (value.equals("loaves")) {
            return "loaf";
        }

        if (value.endsWith("ies") && value.length() > 4) {
            return value.substring(0, value.length() - 3) + "y";
        }

        if (value.endsWith("s")
                && !value.endsWith("ss")
                && value.length() > 3) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

    private static String unitGroup(String unit) {
        if (unit == null) {
            return "";
        }

        String value = unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
            case "cups":
            case "tbsp":
            case "tsp":
                return "volume";

            case "pieces":
            case "piece":
                return "count";

            default:
                return "";
        }
    }

    private static double toBaseUnit(double quantity, String unit) {
        if (unit == null) {
            return -1;
        }

        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "g":
                return quantity;

            case "kg":
                return quantity * 1000;

            case "ml":
                return quantity;

            case "l":
                return quantity * 1000;

            case "cups":
                return quantity * 250;

            case "tbsp":
                return quantity * 15;

            case "tsp":
                return quantity * 5;

            case "pieces":
            case "piece":
                return quantity;

            default:
                return -1;
        }
    }

    public static boolean canMakeRecipe(
            List<Ingredient> pantry,
            List<RecipeIngredient> requirements) {

        if (pantry == null || requirements == null
                || requirements.isEmpty()) {
            return false;
        }

        for (RecipeIngredient requirement : requirements) {

            String requiredName = normalizeName(
                    requirement.getIngredientName()
            );

            String requiredGroup = unitGroup(
                    requirement.getUnit()
            );

            double requiredQuantity = toBaseUnit(
                    requirement.getQuantity(),
                    requirement.getUnit()
            );

            if (requiredGroup.isEmpty()
                    || requiredQuantity <= 0
                    || !Double.isFinite(requiredQuantity)) {
                return false;
            }

            double availableQuantity = 0;

            for (Ingredient item : pantry) {

                if (!normalizeName(item.getName())
                        .equals(requiredName)) {
                    continue;
                }

                if (!unitGroup(item.getUnit())
                        .equals(requiredGroup)) {
                    continue;
                }

                double quantity = toBaseUnit(
                        item.getQuantity(),
                        item.getUnit()
                );

                if (quantity > 0 && Double.isFinite(quantity)) {
                    availableQuantity += quantity;
                }
            }

            if (availableQuantity + EPSILON < requiredQuantity) {
                return false;
            }
        }

        return true;
    }
}
