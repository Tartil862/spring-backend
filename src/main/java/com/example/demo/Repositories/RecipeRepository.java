package com.example.demo.Repositories;

import com.example.demo.Entities.recipes;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecipeRepository extends MongoRepository<recipes, String> {
    List<recipes> findByDishTypeAndRecipeTypeAndIngredientsIn(String dishType, String recipeType, List<String> ingredients);
}
