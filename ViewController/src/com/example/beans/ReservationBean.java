package com.example.beans;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public boolean checkRestaurantSeatsAvailability(Integer restaurant_id, ApplicationModule am){
        /*  For a particular restaurant (restaurant_id = 1)
         *  TimeSlot = 10 - 12
         *  Date = 25 sept 
         *  total seats = 58
         * */
        
        ViewObject vo = am.findViewObject(constants.getCheck_rest_availablity_vo_name());
        vo.setNamedWhereClauseParam("b_rest_id", restaurant_id);
        vo.executeQuery();

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
    
        System.out.println("Before checkRestaurantSeatsAvailability, createBooking() method");
        boolean is_available = checkRestaurantSeatsAvailability(selectedRestaurantId, am);
        System.out.println("After checkRestaurantSeatsAvailability, createBooking() method");

        // Get Reservation VO
        ViewObject reservation_vo = am.findViewObject(constants.getReservation_vo_name());
        
        if(is_available){
            System.out.println("Creating Booking, bookReservation() method");

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
    public static String formatDateString(String date) {
          String[] parts = date.split("/");

          // Ensure the month and day are two digits
          String month = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
          String day = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
          String year = parts[2]; // Year is assumed to be correct

          // Return the formatted date
          return month + "/" + day + "/" + year;
      }
    public String bookReservation(){ 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy H");
        String dateTime = formatDateString(reservation_date) + " " + reservation_time;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Kolkata"));
        Timestamp reservation_timestamp = Timestamp.from(zonedDateTime.toInstant());
        
        System.out.println("Reservation Timestamp (UTC+5:30): " + reservation_timestamp);
        
        ApplicationModule am = getApplicationModule();

        
        // Get Selected Restaurant ID
        ViewObject selectedRestVO = am.findViewObject(constants.getRest_for_custApp_vo_name());
        Row selectedRestaurant = selectedRestVO.first();
        DBSequence dbSequence = (DBSequence) selectedRestaurant.getAttribute("RestaurantId");
        Integer selectedRestaurantId = dbSequence.getSequenceNumber().intValue();
                 
        // Create Booking
        System.out.println("Creating Booking, bookReservation() method");
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
