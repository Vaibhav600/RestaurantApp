package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.adf.view.rich.component.rich.RichPopup;


@ManagedBean(name="checkoutBean")
@SessionScoped
public class CheckoutBean {
    
    private RichPopup checkoutPopup;
    
    
    public CheckoutBean() {
        super();
    }

    public void setCheckoutPopup(RichPopup checkoutPopup) {
        this.checkoutPopup = checkoutPopup;
    }

    public RichPopup getCheckoutPopup() {
        return checkoutPopup;
    }


    public String showCheckoutTrainPopup() {
        
        RichPopup.PopupHints hints = new RichPopup.PopupHints();
        getCheckoutPopup().show(hints);
        return null;
    }
    
    
}
