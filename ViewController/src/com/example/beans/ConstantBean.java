package com.example.beans;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;


@ApplicationScoped
@ManagedBean(name="constantBean")
public class ConstantBean {
    private String super_admin_navigation = "goToSuperAdmin";
    private String owner_navigation = "goToOwner";
    private String customer_navigation = "goToCustomer";
    private String login_page_navigation = "goToLogin";
    private String error_page_navigation = "goToError";
    private String restaurant_vo_name = "G3RestaurantsVO";
    private String reservation_vo_name = "G3ReservationsVO";
    private String orders_vo_name = "G3OrdersVO";
    private String rest_for_custApp_vo_name = "RestaurantVO_ForCustApp";
    private String orderItem_vo_name = "G3OrderItemsVO";
    private String cart_items_vo_name = "CartItemsVO";


    public void setCart_items_vo_name(String cart_items_vo_name) {
        this.cart_items_vo_name = cart_items_vo_name;
    }

    public String getCart_items_vo_name() {
        return cart_items_vo_name;
    }

    public void setOrderItem_vo_name(String orderItem_vo_name) {
        this.orderItem_vo_name = orderItem_vo_name;
    }

    public String getOrderItem_vo_name() {
        return orderItem_vo_name;
    }

    public void setOrders_vo_name(String orders_vo_name) {
        this.orders_vo_name = orders_vo_name;
    }

    public String getOrders_vo_name() {
        return orders_vo_name;
    }

    public void setRest_for_custApp_vo_name(String rest_for_custApp_vo_name) {
        this.rest_for_custApp_vo_name = rest_for_custApp_vo_name;
    }

    public String getRest_for_custApp_vo_name() {
        return rest_for_custApp_vo_name;
    }

    public void setReservation_vo_name(String reservation_vo_name) {
        this.reservation_vo_name = reservation_vo_name;
    }

    public String getReservation_vo_name() {
        return reservation_vo_name;
    }

    public void setLogin_page_navigation(String login_page_navigation) {
        this.login_page_navigation = login_page_navigation;
    }

    public String getLogin_page_navigation() {
        return login_page_navigation;
    }

    public void setSuper_admin_navigation(String super_admin_navigation) {
        this.super_admin_navigation = super_admin_navigation;
    }

    public String getSuper_admin_navigation() {
        return super_admin_navigation;
    }

    public void setOwner_navigation(String owner_navigation) {
        this.owner_navigation = owner_navigation;
    }

    public String getOwner_navigation() {
        return owner_navigation;
    }

    public void setCustomer_navigation(String customer_navigation) {
        this.customer_navigation = customer_navigation;
    }

    public String getCustomer_navigation() {
        return customer_navigation;
    }

    public ConstantBean() {
        super();
    }

    public String getError_page_navigation() {
        return error_page_navigation;
    }

    public void setRestaurant_vo_name(String restaurant_vo_name) {
        this.restaurant_vo_name = restaurant_vo_name;
    }

    public String getRestaurant_vo_name() {
        return restaurant_vo_name;
    }
}
