package com.example.beans;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;


@ViewScoped
@ManagedBean(name="restaurantRatingBean")
public class RestaurantRatingBean {
    private BigDecimal rating;
    public RestaurantRatingBean() {
        super();
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getRating() {
        return rating;
    }
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
    
    @PostConstruct
    public void setRestRating(){
        ApplicationModule am = getApplicationModule();
        ViewObject rating_for_rest_vo = am.findViewObject("RatingTotalForRestaurant1");
        rating_for_rest_vo.setWhereClause("RestaurantId = :rest_id");
        
    }
}
