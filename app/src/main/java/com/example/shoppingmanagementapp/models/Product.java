package com.example.shoppingmanagementapp.models;

public class Product {

    private String productID;
    private String productName;
    private double productPrice;
    private int productStock;
    private int productImage;

    public Product() {
    }

    public Product(String productID) {
        productID = productID;
    }
    public Product(String productID, String productName, double productPrice, int productStock, int productImage) {
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productImage = productImage;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productAmount) {
        this.productStock = productAmount;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductID() {
        return productID;
    }
}
