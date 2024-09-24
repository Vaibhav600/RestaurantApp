package com.example.beans;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpSession;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;


@SessionScoped
@ManagedBean(name="reservationBean")
public class ReservationBean {
    private String number_of_guests;
    private String reservation_date;
    private String reservation_time;

    ConstantBean constants = new ConstantBean();

    public ReservationBean() {
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
    
    public boolean updateRestaurantSeatCount(Integer restaurant_id, ApplicationModule am){
        ViewObject rest_vo = am.findViewObject(constants.getRestaurant_vo_name());
        rest_vo.setWhereClause("G3RestaurantsEO.RESTAURANT_ID = :restId");
        rest_vo.defineNamedWhereClauseParam("restId", null, null);
        rest_vo.setNamedWhereClauseParam("restId", restaurant_id);
        rest_vo.executeQuery();
        Row restaurant_row = rest_vo.first();
        
        boolean canAccommodateRequiredSeats = false;
        int seats_required = Integer.parseInt(number_of_guests);
        
        if (restaurant_row != null) {
            Number seats = (Number)restaurant_row.getAttribute("AvailableSeats");
            int seats_available = seats.intValue();
                
            if(seats_available - seats_required > 0){
                int new_available_seats = seats_available - seats_required;
                restaurant_row.setAttribute("AvailableSeats", new_available_seats);
                am.getTransaction().commit();
                canAccommodateRequiredSeats = true;
                
                System.out.println("Got an Accomodation.");
            }
            
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Restaurant not found."));
        }
        
        // Reset the where clause and parameters to avoid affecting other operations
        rest_vo.removeNamedWhereClauseParam("restId");
        rest_vo.setWhereClause(null);
        rest_vo.executeQuery();
        
        return canAccommodateRequiredSeats;
    }
    
    public void createBooking(Integer selectedRestaurantId, Timestamp reservation_timestamp, ViewObject reservation_vo, ApplicationModule am){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Object userIdObject = session.getAttribute("userId");
        int CustomerId = Integer.parseInt(userIdObject.toString());
        
    
        System.out.println("Before Update Restaurant Seats Method");    
        updateRestaurantSeatCount(selectedRestaurantId, am);
        System.out.println("After Update Restaurant Seats Method");    
        
    
        Row newUserRow = reservation_vo.createRow();
        newUserRow.setAttribute("CustomerId", CustomerId);
        newUserRow.setAttribute("RestaurantId", selectedRestaurantId);
        newUserRow.setAttribute("TableSize", number_of_guests);
        newUserRow.setAttribute("Status", "booked");
        newUserRow.setAttribute("ReservationTime", reservation_timestamp);
        reservation_vo.insertRow(newUserRow);        
        try{
            am.getTransaction().commit();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private boolean isReservationExists(Integer restaurantId, Timestamp reservationTimestamp, ViewObject reservationVO) {
        reservationVO.setWhereClause("G3ReservationsEO.RESTAURANT_ID = :restaurantId AND G3ReservationsEO.RESERVATION_TIME = :reservationTimestamp");
        reservationVO.defineNamedWhereClauseParam("restaurantId", null, null);
        reservationVO.defineNamedWhereClauseParam("reservationTimestamp", null, null);
        reservationVO.setNamedWhereClauseParam("restaurantId", restaurantId);
        reservationVO.setNamedWhereClauseParam("reservationTimestamp", reservationTimestamp);
        reservationVO.executeQuery();
        boolean has_next = reservationVO.hasNext();
        
        reservationVO.removeNamedWhereClauseParam("restaurantId");
        reservationVO.removeNamedWhereClauseParam("reservationTimestamp");
        reservationVO.setWhereClause(null);
        reservationVO.executeQuery();
        
        return has_next;
    }
    public String bookReservation(){        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H");
        String dateTime = reservation_date + " " + reservation_time;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Timestamp reservation_timestamp = Timestamp.valueOf(localDateTime);
        
        ApplicationModule am = getApplicationModule();
        ViewObject reservation_vo = am.findViewObject(constants.getReservation_vo_name());

        ViewObject selectedRestVO = am.findViewObject(constants.getRest_for_custApp_vo_name());
        Row selectedRestaurant = selectedRestVO.first();
        DBSequence dbSequence = (DBSequence) selectedRestaurant.getAttribute("RestaurantId");
        Integer selectedRestaurantId = dbSequence.getSequenceNumber().intValue();
        
        if (isReservationExists(selectedRestaurantId, reservation_timestamp, reservation_vo)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Reservation already exists for the selected restaurant and time.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return null;
        }
        createBooking(selectedRestaurantId, reservation_timestamp, reservation_vo, am);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Booking Successfull"));

        return null;
    }

    public void setNumber_of_guests(String number_of_guests) {
        this.number_of_guests = number_of_guests;
    }

    public String getNumber_of_guests() {
        return number_of_guests;
    }
    public void setReservation_date(String reservation_date) {
        this.reservation_date = reservation_date;
    }

    public String getReservation_date() {
        return reservation_date;
    }

    public void setReservation_time(String reservation_time) {
        this.reservation_time = reservation_time;
    }

    public String getReservation_time() {
        return reservation_time;
    }
}
