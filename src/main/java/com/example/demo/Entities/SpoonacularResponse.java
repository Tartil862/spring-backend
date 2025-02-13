package com.example.demo.Entities;

import java.util.List;

public class SpoonacularResponse {
    private List<recipes> results;

    public List<recipes> getResults() {
        return results;
    }

    public void setResults(List<recipes> results) {
        this.results = results;
    }
}
