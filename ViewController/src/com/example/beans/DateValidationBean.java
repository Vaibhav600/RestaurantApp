package com.example.beans;

import java.time.LocalDate;

import java.time.ZoneId;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;


@ViewScoped
@ManagedBean(name="dateValidationBean")
public class DateValidationBean {    
    public DateValidationBean() {
        super();
    }
    public Date getCurrentDate() {
        return new Date();
    }
}
