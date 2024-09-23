package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;


@ViewScoped
@ManagedBean(name="setCartItemVC")
public class SetCartItemVC {
    ConstantBean constants = new ConstantBean();

    public SetCartItemVC() {
        super();
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
    public String setCartVC(){
        // Get Logged in Customer Id
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Object userIdObject = session.getAttribute("userId");
        int CustomerId = Integer.parseInt(userIdObject.toString());
        
        ApplicationModule am = getApplicationModule();
        ViewObject cart_vo = am.findViewObject(constants.getCart_items_vo_name());
        cart_vo.setNamedWhereClauseParam("bCustId", CustomerId);
        return null;
    }
}
