package com.example.beans;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;

@ManagedBean(name = "paymentBean")
@ViewScoped
public class PaymentBean {
    private String selectedPaymentMethod;
    private String cardHolderName;
    private String cardNumber;
    private String cvv;
    private String upiId;

    private boolean showCardFields;
    private boolean showUPIField;


    ConstantBean constants = new ConstantBean();

    public PaymentBean() {
        super();
    }

    public ApplicationModule getApplicationModule() {
        BindingContext bindingContext = BindingContext.getCurrent();
        if (bindingContext != null) {
            DCBindingContainer bindings = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
            DCDataControl dataControl = bindings.getDataControl();
            ApplicationModule am = (ApplicationModule) dataControl.getDataProvider();
            return am;
        } else {
            System.out.println("BindingContext is null");
        }
        return null;
    }


    public void setShowCardFields(boolean showCardFields) {
        this.showCardFields = showCardFields;
    }

    public boolean isShowCardFields() {
        return showCardFields;
    }

    public void setShowUPIField(boolean showUPIField) {
        this.showUPIField = showUPIField;
    }

    public boolean isShowUPIField() {
        return showUPIField;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCvv() {
        return cvv;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getUpiId() {
        return upiId;
    }

    public String getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(String selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public void makePayment(ActionEvent actionEvent) {
        System.out.println(getSelectedPaymentMethod());

        ApplicationModule am = getApplicationModule();
        BindingContext bindingContext = BindingContext.getCurrent();

        DCIteratorBinding iteratorBinding =
            (DCIteratorBinding) bindingContext.getCurrentBindingsEntry().get("CartItemsVOIterator");
        Row currentRow = iteratorBinding.getCurrentRow();
        DBSequence orderIdDbSeq = (DBSequence) currentRow.getAttribute("OrderId");
        Number orderId = orderIdDbSeq != null ? orderIdDbSeq.getSequenceNumber() : null;

        String paymentStatus = (String) currentRow.getAttribute("PaymentStatus");
        if (paymentStatus == "paid") {
            FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Payment already done", null));
        } else {

            ViewObject orders_vo = am.findViewObject(constants.getOrders_vo_name());
            orders_vo.setWhereClause("G3OrdersEO.ORDER_ID = :ordId");
            orders_vo.defineNamedWhereClauseParam("ordId", null, null);
            orders_vo.setNamedWhereClauseParam("ordId", orderId);
            orders_vo.executeQuery();

            Row orderRow = orders_vo.first();
            BigDecimal TotalAmount = (BigDecimal) orderRow.getAttribute("TotalAmount");

            ViewObject payments_vo = am.findViewObject(constants.getPayments_vo_name());
            Row newPayment = payments_vo.createRow();

            newPayment.setAttribute("OrderId", orderId);
            newPayment.setAttribute("PaymentMethod", selectedPaymentMethod);
            newPayment.setAttribute("Amount", TotalAmount);
            newPayment.setAttribute("Status", "paid");
            payments_vo.insertRow(newPayment);

            orderRow.setAttribute("PaymentStatus", "paid");
            orderRow.setAttribute("OrderStatus", "ordered");
            try {
                am.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            orders_vo.removeNamedWhereClauseParam("ordId");
            orders_vo.setWhereClause(null);
            orders_vo.executeQuery();
        }
    }

    public void paymentMethodChanged(ValueChangeEvent event) {
        String newVal = event.getNewValue().toString();
        setSelectedPaymentMethod(newVal);
    }
}
