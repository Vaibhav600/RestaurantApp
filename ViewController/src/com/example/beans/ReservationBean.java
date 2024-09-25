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
    
//    public boolean updateRestaurantSeatCount(Integer restaurant_id, ApplicationModule am){
//        ViewObject rest_vo = am.findViewObject(constants.getRestaurant_vo_name());
//        rest_vo.setWhereClause("G3RestaurantsEO.RESTAURANT_ID = :restId");
//        rest_vo.defineNamedWhereClauseParam("restId", null, null);
//        rest_vo.setNamedWhereClauseParam("restId", restaurant_id);
//        rest_vo.executeQuery();
//        Row restaurant_row = rest_vo.first();
//        
//        boolean canAccommodateRequiredSeats = false;
//        int seats_required = Integer.parseInt(number_of_guests);
//        
//        if (restaurant_row != null) {
//            Number seats = (Number)restaurant_row.getAttribute("AvailableSeats");
//            int seats_available = seats.intValue();
//                
//            if(seats_available - seats_required > 0){
//                int new_available_seats = seats_available - seats_required;
//                restaurant_row.setAttribute("AvailableSeats", new_available_seats);
//                am.getTransaction().commit();
//                canAccommodateRequiredSeats = true;
//                
//                System.out.println("Got an Accomodation.");
//            }
//            
//        } else {
//            FacesContext.getCurrentInstance().addMessage(null,
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Restaurant not found."));
//        }
//        
//        // Reset the where clause and parameters to avoid affecting other operations
//        rest_vo.removeNamedWhereClauseParam("restId");
//        rest_vo.setWhereClause(null);
//        rest_vo.executeQuery();
//        
//        return canAccommodateRequiredSeats;
//    }
    public boolean checkRestaurantSeatsAvailability(Integer restaurant_id, ApplicationModule am){
        /*  For a particular restaurant (restaurant_id = 1)
         *  TimeSlot = 10 - 12
         *  Date = 25 sept 
         *  total seats = 58
         * */
        
        ViewObject vo = am.findViewObject(constants.getCheck_rest_availablity_vo_name());
        vo.setNamedWhereClauseParam("b_rest_id", restaurant_id);
        // vo.executeQuery();

        Row currRow = vo.first();
        Number totalReservedSeats = (Number)currRow.getAttribute("TotalReservedSeats");
        Number totalSeats = (Number)currRow.getAttribute("TotalSeats");
    

        try {
            System.out.println("numberOfGuests: " + number_of_guests);
            System.out.println("totalSeats: " + totalSeats);
            System.out.println("totalReservedSeats: " + totalReservedSeats);
            System.out.println("totalSeats - totalReservedSeats: " + totalSeats.subtract(totalReservedSeats));
            System.out.println((totalSeats.subtract(totalReservedSeats)).subtract(new Number(number_of_guests)));
            System.out.println("If condition result: " + totalSeats.subtract(totalReservedSeats).subtract(new Number(number_of_guests)).compareTo(new Number(0)));

            if ((totalSeats.subtract(totalReservedSeats)).subtract(new Number(number_of_guests)).compareTo(new Number(0)) > 0){                
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in try catch: check checkRestaurantSeatsAvailability method in ReservationBean");
        }
        
        return false;
    }
    public boolean createBooking(Integer selectedRestaurantId, Timestamp reservation_timestamp, ApplicationModule am){
        // Get Logged in Customer ID
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Object userIdObject = session.getAttribute("userId");
        int CustomerId = Integer.parseInt(userIdObject.toString());
    
        // updateRestaurantSeatCount(selectedRestaurantId, am);
        boolean is_available = checkRestaurantSeatsAvailability(selectedRestaurantId, am);
        
        // Get Reservation VO
        ViewObject reservation_vo = am.findViewObject(constants.getReservation_vo_name());
        
        if(is_available){
            Row newUserRow = reservation_vo.createRow();
            newUserRow.setAttribute("CustomerId", CustomerId);
            newUserRow.setAttribute("RestaurantId", selectedRestaurantId);
            newUserRow.setAttribute("TableSize", number_of_guests);
            newUserRow.setAttribute("Status", "booked");
            newUserRow.setAttribute("ReservationTime", reservation_timestamp);
            reservation_vo.insertRow(newUserRow);        
            try{
                am.getTransaction().commit();
                am.findViewObject(constants.getCheck_rest_availablity_vo_name()).executeQuery();
            } catch(Exception e){
                e.printStackTrace();
            }
            return true;
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seats Unavailable"));
        }
        return false;
    }
    public String bookReservation(){   
        // Create Reservation Timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H");
        String dateTime = reservation_date + " " + reservation_time;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Timestamp reservation_timestamp = Timestamp.valueOf(localDateTime);
        
        ApplicationModule am = getApplicationModule();
        
        // Get Selected Restaurant ID
        ViewObject selectedRestVO = am.findViewObject(constants.getRest_for_custApp_vo_name());
        Row selectedRestaurant = selectedRestVO.first();
        DBSequence dbSequence = (DBSequence) selectedRestaurant.getAttribute("RestaurantId");
        Integer selectedRestaurantId = dbSequence.getSequenceNumber().intValue();
                 
        // Create Booking
        boolean booking_successful = createBooking(selectedRestaurantId, reservation_timestamp, am);
        
        if(booking_successful){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Booking Successfull"));            
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Booking Unsuccessful, Something went wrong"));
        }

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
