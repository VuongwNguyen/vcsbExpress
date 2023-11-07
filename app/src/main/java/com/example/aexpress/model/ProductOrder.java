package com.example.aexpress.model;

public class ProductOrder {
    private int id, created_at;
    private String code, buyer, status;
    private double total_fees;

    public ProductOrder() {
    }

    public ProductOrder(int id, int created_at, String code, String buyer, String status, double total_fees) {
        this.id = id;
        this.created_at = created_at;
        this.code = code;
        this.buyer = buyer;
        this.status = status;
        this.total_fees = total_fees;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCreated_at() {
        return created_at;
    }

    public void setCreated_at(int created_at) {
        this.created_at = created_at;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public double getTotal_fees() {
        return total_fees;
    }

    public void setTotal_fees(double total_fees) {
        this.total_fees = total_fees;
    }
}
