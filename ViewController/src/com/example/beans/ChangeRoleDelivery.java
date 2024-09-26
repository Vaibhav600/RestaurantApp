package com.example.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.jbo.ApplicationModule;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.Row;

@SessionScoped
@ManagedBean(name = "changeRoleDelivery")
public class ChangeRoleDelivery {

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

    public String changeRoleToDeliveryAgent() {
        ApplicationModule am = getApplicationModule();
        BindingContext bindingContext = BindingContext.getCurrent();

        // Access the DCIteratorBinding for the CartItemsVO view object
        DCIteratorBinding iteratorBinding =
            (DCIteratorBinding) bindingContext.getCurrentBindingsEntry().get("SuperAdminUsersIterator");

        if (iteratorBinding != null) {
            System.out.println("Successfull");
        }
        Row currentRow = iteratorBinding.getCurrentRow();
        String role = (String) currentRow.getAttribute("Role");

        if (!"customer".equals(role)) {
            FacesContext.getCurrentInstance()
                .addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                             "Super-Admin or Owner can't be changed to Delivery Agent", null));
        } else {
            try {
                currentRow.setAttribute("Role", "delivery");
                am.getTransaction().commit();
                FacesContext.getCurrentInstance().addMessage(null,
                                                             new FacesMessage("Role changed successfully"));
            } catch (Exception e) {
                FacesContext.getCurrentInstance()
                    .addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                 "Assignment failed!", null));
                e.printStackTrace();
                
            }
        }

        return null;
    }
}
