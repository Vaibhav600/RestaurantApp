package com.example.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.RequestScoped;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;


@SessionScoped
@ManagedBean(name="addRestaurantBean")
public class AddRestaurantBean {
    private String owner_id;
    private String name;
    private String address;
    private String phone;
    private String available_seats;
    private String status;
            
    ConstantBean constants = new ConstantBean();
    
    public AddRestaurantBean() {
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
    public String addRestaurant(){
        try{
            ApplicationModule am = getApplicationModule();
            ViewObject restaurants_vo = am.findViewObject(constants.getRestaurant_vo_name());

            Row newRow = restaurants_vo.createRow();
            newRow.setAttribute("OwnerId", owner_id);
            newRow.setAttribute("Name", name);
            newRow.setAttribute("Address", address);
            newRow.setAttribute("Phone", phone);
            newRow.setAttribute("AvailableSeats", available_seats);
            newRow.setAttribute("Status", status);
            restaurants_vo.insertRow(newRow);

            am.getTransaction().commit();
            
            owner_id = "";
            name = "";
            address = "";
            phone = "";
            available_seats = "";
            status = "";
        }
        catch(Exception e){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Restaurant Addition Failed", null));
            System.out.println(e);
        }
        return null;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setAvailable_seats(String available_seats) {
        this.available_seats = available_seats;
    }

    public String getAvailable_seats() {
        return available_seats;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
