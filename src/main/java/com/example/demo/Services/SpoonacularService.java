package com.example.demo.Services;

import com.example.demo.Entities.recipes;
import com.example.demo.Entities.SpoonacularResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpoonacularService {
    private final String API_KEY = "d1940300cfcf4432abdc3d2d66c66f9a";
    private final String API_URL = "https://api.spoonacular.com/recipes/complexSearch";

    public List<recipes> fetchRecipes(String dishType, String recipeType, List<String> ingredients) {
        String ingredientList = String.join(",", ingredients);
        String url = API_URL + "?type=" + dishType + "&cuisine=" + recipeType + "&includeIngredients=" + ingredientList + "&apiKey=" + API_KEY;

        // Fetch the basic list of recipes
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, null, Map.class);

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");

        List<recipes> recipesList = new ArrayList<>();

        // Loop through the results and fetch detailed information for each recipe
        for (Map<String, Object> result : results) {
            String recipeId = result.get("id").toString();

            // Fetch detailed recipe data
            String recipeUrl = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=" + API_KEY;
            Map<String, Object> recipeDetails = restTemplate.getForObject(recipeUrl, Map.class);

            // Extract relevant information
            recipes recipe = new recipes();
            recipe.setId(recipeId);
            recipe.setTitle((String) result.get("title"));

            // Adjusting to handle complex ingredient objects
            recipe.setIngredients((List<Map<String, Object>>) recipeDetails.get("extendedIngredients"));  // Ingredients

            recipe.setInstructions((String) recipeDetails.get("instructions"));  // Instructions
            recipe.setImage((String) recipeDetails.get("image"));  // Image

            recipesList.add(recipe);
        }

        return recipesList;
    }


}
