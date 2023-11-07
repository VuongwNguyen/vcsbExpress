package com.example.aexpress.utils;

public class Constants {
    public static String API_BASE_URL = "https://jugal-hit.000webhostapp.com";
    public static String GET_CATEGORIES_URL = API_BASE_URL + "/services/listCategory";
    public static String GET_PRODUCTS_URL = API_BASE_URL + "/services/listProduct";
    public static String GET_PRODUCTS_ORDER_URL = API_BASE_URL + "/services/getProductOrder?code=";
    public static String GET_OFFERS_URL = API_BASE_URL + "/services/listFeaturedNews";
    public static String GET_PRODUCT_DETAILS_URL = API_BASE_URL + "/services/getProductDetails?id=";
    public static String POST_ORDER_URL = API_BASE_URL + "/services/submitProductOrder";
    public static String PAYMENT_URL = API_BASE_URL + "/services/paymentPage?code=";

    public static  String GET_IMAGES_PRODUCT_URL = API_BASE_URL+"/services/getAllProductImageByProductId?id=";

    public static String NEWS_IMAGE_URL = API_BASE_URL + "/uploads/news/";
    public static String CATEGORIES_IMAGE_URL = API_BASE_URL + "/uploads/category/";
    public static String PRODUCTS_IMAGE_URL = API_BASE_URL + "/uploads/product/";
}
