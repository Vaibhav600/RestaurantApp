package com.example.beans;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;


@ManagedBean(name="addCouponBean")
public class AddCouponBean {
    public AddCouponBean() {
        super();
    }
    
    private String description;
    private int discountPercentage;
    private Date validFrom;
    private Date validTo;
    
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
    
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void insertCoupon(ActionEvent actionEvent) {
        // Add event code here...
        try{
        if (discountPercentage < 0 || discountPercentage > 100) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Discount percentage must be between 0 and 100."));
                return ;
            }

            if (validFrom != null && validTo != null && validFrom.after(validTo)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Valid From date must be earlier than Valid To date."));
                return ;
            }
        ApplicationModule am = getApplicationModule();
        ViewObject coupon_restaurant_vo = am.findViewObject(constants.getCoupons_restaurant_user_vo());
        ViewObject restaurants_vo = am.findViewObject(constants.getRestaurant_for_user_vo());

        Row restaurants_row = restaurants_vo.first();
        Row newRow = coupon_restaurant_vo.createRow();
        
        DBSequence dbSeq = (DBSequence) restaurants_row.getAttribute("RestaurantId");
        int rest_id = dbSeq.getSequenceNumber().intValue();
        System.out.println("restaurant_id: "+rest_id);
        
        newRow.setAttribute("Description",this.description);
        newRow.setAttribute("DiscountPercentage",this.discountPercentage);
        newRow.setAttribute("RestaurantId",rest_id);
        newRow.setAttribute("ValidFrom",this.validFrom);
        newRow.setAttribute("ValidTo",this.validTo);
        
        coupon_restaurant_vo.insertRow(newRow);
        am.getTransaction().commit();
        
        this.description="";
        this.discountPercentage=0;
        this.validFrom = null;
        this.validTo=null;
        return ;
        } catch(Exception e){
            e.printStackTrace();
        }

    }
}
