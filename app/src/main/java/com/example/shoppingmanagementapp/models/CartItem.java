package com.example.shoppingmanagementapp.models;

public class CartItem {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private double subtotal;
    private int productImage;

    public CartItem() {}

    public CartItem(String productId, String productName, double price, int quantity, int productImage) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.productImage = productImage;
        this.subtotal = price * quantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        this.subtotal = this.price * this.quantity;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.price * this.quantity;
    }

    public double getSubtotal() { return subtotal; }

    public int getProductImage() { return productImage; }
    public void setProductImage(int productImage) { this.productImage = productImage; }
}