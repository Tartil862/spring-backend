package com.example.demo.Controllers;

import com.example.demo.Entities.RecipeRequest;
import com.example.demo.Entities.recipes;
import com.example.demo.Services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @PostMapping("/recipes")
    public List<recipes> getRecipes(@RequestBody RecipeRequest request) {
        return recipeService.generateRecipes(request.getDishType(), request.getRecipeType(), request.getIngredients());
    }
}


