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
        System.out.println("Booking Id: "+this.getReservationId());
        ApplicationModule am = getApplicationModule();
        ViewObject vo = am.findViewObject(constants.getReservation_vo_name());
        ViewObject totalSeatsVO = am.findViewObject(constants.getTotal_seats_restaurant_vo());
        ViewObject bookingsVO = am.findViewObject(constants.getReservation_of_customer_for_restaurant());
        // Create a query to find the reservation with the given reservationId
        vo.setWhereClause("RESERVATION_ID = :resId");
        vo.defineNamedWhereClauseParam("resId", null, null);
        vo.setNamedWhereClauseParam("resId", this.reservationId);
           
       vo.executeQuery();
       
       Row rowToDelete = null;
       
       // Check if there are rows matching the query
       if (vo.hasNext()) {
           rowToDelete = vo.next();
       }
       
       // Delete the row if it exists
       if (rowToDelete != null) {
           rowToDelete.remove();
           System.out.println("Booking removed: " + this.reservationId);
           
           // Commit the transaction
           am.getTransaction().commit();
           
           // Refresh the view objects
           totalSeatsVO.executeQuery();
            
           vo.removeNamedWhereClauseParam("resId");
           vo.setWhereClause(null);
           vo.executeQuery();
           bookingsVO.executeQuery();

            
           
           System.out.println("Transaction committed.");
       } else {
           System.out.println("Reservation not found.");
       }
        return null;
        
    }
}
