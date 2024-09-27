package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.nav.RichButton;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;


@ViewScoped
@ManagedBean(name="myOrderCustomerBean")
public class MyOrderCustomerBean {
    private RichPopup popup_order;

    public MyOrderCustomerBean() {
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
    // Method to show Popup 1
    public void showPopup1() {
        if (popup_order != null) {
            RichPopup.PopupHints hints = new RichPopup.PopupHints();
            popup_order.show(hints);
        }
    }
    public String setCustomerId(){
        // Get Logged in Customer Id
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                                                        .getExternalContext()
                                                        .getSession(false);
        Object userIdObject = session.getAttribute("userId");
        int CustomerId = Integer.parseInt(userIdObject.toString());
        ApplicationModule am = getApplicationModule();
        ViewObject vo = am.findViewObject("MyOrders_forCustApp_VO");
        vo.setNamedWhereClauseParam("bOrderCustomerId", CustomerId);
        vo.executeQuery();
        showPopup1();
        return null;
    }

    public void setPopup_order(RichPopup popup_order) {
        this.popup_order = popup_order;
    }

    public RichPopup getPopup_order() {
        return popup_order;
    }
}
