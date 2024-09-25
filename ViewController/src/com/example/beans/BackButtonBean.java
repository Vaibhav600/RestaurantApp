package com.example.beans;

import java.util.Iterator;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;


@SessionScoped
@ManagedBean(name="backButtonBean")
public class BackButtonBean {
    
    ConstantBean constants = new ConstantBean();
    
    public BackButtonBean() {
        super();
    }
    public String goBack() {
        BindingContext bindingContext = BindingContext.getCurrent();
        DCBindingContainer bindings = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
        DCDataControl dataControl = bindings.getDataControl();
        ApplicationModule am = (ApplicationModule)dataControl.getDataProvider();
    
        DCIteratorBinding iter = bindings.findIteratorBinding("RestaurantVO_ForCustAppIterator");
        ViewObject vo = iter.getViewObject();
        //vo.removeNamedWhereClauseParam("bRestName");
        vo.getViewCriteriaManager().removeViewCriteria("searchRestByNameVC");
        vo.executeQuery();
        
        return constants.getRest_to_cust_dashboard_navigation();
    }
}
