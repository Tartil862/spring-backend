package com.example.demo.Repositories;

import com.example.demo.Entities.Ingredients;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
public interface IngredientRepository extends MongoRepository<Ingredients, String> {
    List<Ingredients> findByCategory(String category);
}
