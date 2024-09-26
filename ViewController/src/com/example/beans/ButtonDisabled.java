package com.example.beans;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;

@SessionScoped
@ManagedBean(name="buttonDisabled")
public class ButtonDisabled {
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
    public Boolean couponDisabled(){
        ApplicationModule am = getApplicationModule();
        BindingContext bindingContext = BindingContext.getCurrent();

        // Access the DCIteratorBinding for the CartItemsVO view object
        DCIteratorBinding iteratorBinding =
            (DCIteratorBinding) bindingContext.getCurrentBindingsEntry().get("CartItemsVOIterator");

        // Get the current row
        Row currentRow = iteratorBinding.getCurrentRow();
        
        BigDecimal couponId = (BigDecimal)currentRow.getAttribute("CouponId");
        return couponId!=null;
    }
}
