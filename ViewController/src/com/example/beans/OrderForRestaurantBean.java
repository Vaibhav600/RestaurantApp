package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;

@SessionScoped
@ManagedBean(name="orderForRestaurantBean")
public class OrderForRestaurantBean {
    public OrderForRestaurantBean() {
        super();
    }
    
    ConstantBean constants = new ConstantBean();
    public ApplicationModule getApplicationModule() {
        BindingContext bindingContext = BindingContext.getCurrent();
        if (bindingContext != null) {
            DCBindingContainer bindings = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
            DCDataControl dataControl = bindings.getDataControl();
            ApplicationModule am = (ApplicationModule)dataControl.getDataProvider();
           return am;
        } 
        else {
            System.out.println("BindingContext is null");
        }
        return null;
    }
    public String commit(){
        System.out.println("commiting");
        ApplicationModule am = getApplicationModule();
        ViewObject menu_restaurants_vo = am.findViewObject(constants.getOrder_restaurant_user_vo());
        am.getTransaction().commit();
        return null;
    }
}
