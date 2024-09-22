package com.example.beans;

import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.adf.view.rich.component.rich.data.RichListView;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;


@SessionScoped
@ManagedBean(name="resetRestaurantVCBean")
public class ResetRestaurantVCBean {
    private String rest_vc_name = "searchRestByNameVC";
    private String rest_for_custApp_vo_name = "RestaurantVO_ForCustApp";
    private RichListView list;

    public ResetRestaurantVCBean() {
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
    @PostConstruct
    public String resetVC(){
        ApplicationModule am = getApplicationModule();
        ViewObject rest_vo = am.findViewObject(rest_for_custApp_vo_name);
        rest_vo.setWhereClause(null);
        rest_vo.removeNamedWhereClauseParam("bRestName"); // Remove the parameter
        rest_vo.executeQuery();
        rest_vo.getViewCriteriaManager().clearViewCriterias();
        return null;
    }

    public void setList(RichListView list) {
        this.list = list;
    }

    public RichListView getList() {
        return list;
    }
}
