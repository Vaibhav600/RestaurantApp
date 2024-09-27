package com.example.beans;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;

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
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;


@SessionScoped
@ManagedBean(name="reservationBean")
public class ReservationBean {
    private String number_of_guests;
    private String reservation_date;
    private String reservation_time;
    private String availability;

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
    public boolean checkRestaurantSeatsAvailability(Integer restaurant_id, Timestamp reservation_timestamp, ApplicationModule am){
        ViewObject check_reserve_vo = am.findViewObject(constants.getReserv_check_vo_name());
        check_reserve_vo.setNamedWhereClauseParam("bRestaurantId", restaurant_id);
        check_reserve_vo.setNamedWhereClauseParam("bTimeStamp", reservation_timestamp);
        check_reserve_vo.executeQuery();

        // Get Total Tables Reserved For given date and time
        RowSetIterator rsi = check_reserve_vo.createRowSetIterator(null);
        int totalTablesReserved = 0;
        while (rsi.hasNext()) {
            Row currentRow = rsi.next();
            Integer tableSize = (Integer) currentRow.getAttribute("TableSize");
            totalTablesReserved += tableSize;
        }
        rsi.closeRowSetIterator();
        
        // Get Total Seats For A Restaurant
        ViewObject rest_vo = am.findViewObject("GetRestTotalSeatsCount_VO");
        rest_vo.setNamedWhereClauseParam("set_rest_id", restaurant_id);
        rest_vo.executeQuery();
        Row row = rest_vo.first();
        Number total_seats = (Number)row.getAttribute("AvailableSeats");

        int guests = Integer.parseInt(number_of_guests);
        
        if((total_seats.intValue() - totalTablesReserved) - guests > 0)
            return true;        
        return false;
    }
    public boolean createBooking(Integer selectedRestaurantId, Timestamp reservation_timestamp, ApplicationModule am){
        // Get Logged in Customer ID
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Object userIdObject = session.getAttribute("userId");
        int CustomerId = Integer.parseInt(userIdObject.toString());
    
        boolean is_available = checkRestaurantSeatsAvailability(selectedRestaurantId, reservation_timestamp, am);

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
    public Timestamp getTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy H");
        String dateTime = formatDateString(reservation_date) + " " + reservation_time;
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Kolkata"));
        Timestamp reservation_timestamp = Timestamp.from(zonedDateTime.toInstant());
        return reservation_timestamp;
    }
    private boolean checkDateValidation(){
        
        System.out.println("DATE : " + formatDateString(reservation_date));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date reservationDate = dateFormat.parse(formatDateString(reservation_date));
            
            Date currentDate = new Date();

            String currentDateString = dateFormat.format(currentDate);
            currentDate = dateFormat.parse(currentDateString);

            return !reservationDate.before(currentDate); 
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Return false in case of parsing error
        }        
    }
    public String bookReservation(){ 
        boolean date_check = checkDateValidation();
        
        if(date_check){
            ApplicationModule am = getApplicationModule();

            Timestamp reservation_timestamp = getTimestamp();
            
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
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Date Validation Failed."));
        }

        return null;
    }

    public String checkAvailabilityButton(){
        ApplicationModule am = getApplicationModule();

        Timestamp reservation_timestamp = getTimestamp();
        
        // Get Selected Restaurant ID
        ViewObject selectedRestVO = am.findViewObject(constants.getRest_for_custApp_vo_name());
        Row selectedRestaurant = selectedRestVO.first();
        DBSequence dbSequence = (DBSequence) selectedRestaurant.getAttribute("RestaurantId");
        Integer restaurant_id = dbSequence.getSequenceNumber().intValue();
        
        ViewObject check_reserve_vo = am.findViewObject(constants.getReserv_check_vo_name());
        check_reserve_vo.setNamedWhereClauseParam("bRestaurantId", restaurant_id);
        check_reserve_vo.setNamedWhereClauseParam("bTimeStamp", reservation_timestamp);
        check_reserve_vo.executeQuery();

        // Get Total Tables Reserved For given date and time
        RowSetIterator rsi = check_reserve_vo.createRowSetIterator(null);
        int totalTablesReserved = 0;
        while (rsi.hasNext()) {
            Row currentRow = rsi.next();
            Integer tableSize = (Integer) currentRow.getAttribute("TableSize");
            totalTablesReserved += tableSize;
        }
        rsi.closeRowSetIterator();
        
        // Get Total Seats For A Restaurant
        ViewObject rest_vo = am.findViewObject("GetRestTotalSeatsCount_VO");
        rest_vo.setNamedWhereClauseParam("set_rest_id", restaurant_id);
        rest_vo.executeQuery();
        Row row = rest_vo.first();
        Number total_seats = (Number)row.getAttribute("AvailableSeats");
        
        int availability = total_seats.intValue() - totalTablesReserved;
        
        setAvailability(String.valueOf(availability));
        
        System.out.println("Availability: " + availability);
        
        return String.valueOf(availability);
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

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAvailability() {
        return availability;
    }
}
