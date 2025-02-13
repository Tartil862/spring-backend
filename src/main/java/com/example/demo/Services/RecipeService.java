package com.example.demo.Services;

import com.example.demo.Entities.recipes;
import com.example.demo.Repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private SpoonacularService spoonacularService;

    public List<recipes> generateRecipes(String dishType, String recipeType, List<String> ingredients) {
        // Fetch recipes from MongoDB
        List<recipes> recipes = recipeRepository.findByDishTypeAndRecipeTypeAndIngredientsIn(dishType, recipeType, ingredients);

        // If no recipes found, fetch from Spoonacular API
        if (recipes.isEmpty()) {
            recipes = spoonacularService.fetchRecipes(dishType, recipeType, ingredients);
            recipeRepository.saveAll(recipes); // Save to MongoDB for future use
        }

        return recipes;
    }
}
