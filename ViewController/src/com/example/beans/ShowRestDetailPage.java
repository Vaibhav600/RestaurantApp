package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;


@ViewScoped
@ManagedBean(name="showRestDetailPage")
public class ShowRestDetailPage {
    private RichOutputFormatted rest_id;
    ConstantBean constants = new ConstantBean();

    public ShowRestDetailPage() {
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
    public String showRestDetail() {
        Object rest_value = rest_id.getValue();
        int restaurant_id = Integer.parseInt(rest_value.toString());

//        ApplicationModule am = getApplicationModule();
//        ViewObject cart_vo = am.findViewObject("CheckRestAvailability_ForCustApp_VO");
//        cart_vo.setNamedWhereClauseParam("b_rest_id", restaurant_id);
//        cart_vo.executeQuery();
                
        return "goToRestDetailsPage";
    }

    public void setRest_id(RichOutputFormatted rest_id) {
        this.rest_id = rest_id;
    }

    public RichOutputFormatted getRest_id() {
        return rest_id;
    }
}
