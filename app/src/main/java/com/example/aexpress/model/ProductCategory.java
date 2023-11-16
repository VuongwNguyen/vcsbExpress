package com.example.aexpress.model;

import java.util.ArrayList;

public class ProductCategory {
    private String category;
    private ArrayList<Product> products;

    public ProductCategory(String category, ArrayList<Product> products) {
        this.category = category;
        this.products = products;
    }

    public ProductCategory() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
