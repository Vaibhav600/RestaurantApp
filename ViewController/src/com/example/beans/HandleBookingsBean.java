package com.example.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;

@SessionScoped
@ManagedBean(name="handleBookingsBean")
public class HandleBookingsBean {
    public HandleBookingsBean() {
        super();
    }
    private String reservationId;

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    ConstantBean constants = new ConstantBean();
    public ApplicationModule getApplicationModule() {
        BindingContext bindingContext = BindingContext.getCurrent();
            if (bindingContext != null) {
        DCBindingContainer bindings = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
        DCDataControl dataControl = bindings.getDataControl();
        ApplicationModule am = (ApplicationModule)dataControl.getDataProvider();
           return am;
        } else {
            System.out.println("BindingContext is null");
        }
        return null;
    }
    
    public String removeBooking(){
        ApplicationModule am = getApplicationModule();
        ViewObject vo = am.findViewObject(constants.getReservation_vo_name());
        Row rowToDelete = null;
        for (Row row : vo.getAllRowsInRange()) {
            DBSequence rowItemId = (DBSequence) row.getAttribute("ReservationId"); // Use correct attribute name for ItemId
            String item = rowItemId.toString(); // Replace with actual attribute name
            if (item.equals(this.reservationId)) {
                rowToDelete = row;
                break;
            }
        }
        if (rowToDelete != null) {
            rowToDelete.remove();
            System.out.println("Menu item removed: " + this.reservationId);
            am.getTransaction().commit();
            System.out.println("Transaction committed.");
        } else {
            System.out.println("Reservation not found.");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed", null));

        }
        return null;
        
    }
}
