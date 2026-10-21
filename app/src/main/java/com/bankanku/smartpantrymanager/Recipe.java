
package com.bankanku.smartpantrymanager;

public class Recipe {

    private int id;
    private String name;
    private String instructions;
    private int preparationTime;
    private String category;

    public Recipe(int id, String name, String instructions,
                  int preparationTime, String category) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.preparationTime = preparationTime;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public String getCategory() {
        return category;
    }
}

