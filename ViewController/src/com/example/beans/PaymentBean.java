package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="paymentBean")
@SessionScoped
public class PaymentBean {
    private String selectedPaymentMethod;
    
    public PaymentBean() {
        super();
    }

    public String getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(String selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }
}
