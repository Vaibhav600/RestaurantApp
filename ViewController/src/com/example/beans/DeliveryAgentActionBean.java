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

@SessionScoped
@ManagedBean(name="deliveryAgentActionBean")
public class DeliveryAgentActionBean {
    private String order_id;
    private String agent_id;
    public DeliveryAgentActionBean() {
        super();
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
    public String pickOrder(){
        System.out.println("agentId: "+agent_id+", "+order_id);
        try{
        ApplicationModule am = getApplicationModule();
        ViewObject myOrders_vo = am.findViewObject(constants.getDelivery_agent_info_vo());
        ViewObject orders_vo = am.findViewObject(constants.getOrders_vo_name());
            
        myOrders_vo.setNamedWhereClauseParam("bAgentId", agent_id);
        myOrders_vo.setNamedWhereClauseParam("bOrderStatus", "Out for delivery");
        myOrders_vo.executeQuery();
        Row myOrdersRow = myOrders_vo.first();    
        System.out.println("my row: +"+myOrdersRow);    

        if (myOrdersRow!=null) {
            
            System.out.println("This delivery agent already has an order out for delivery.");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You already have an order out for delivery.", null));
            return null;
        }    
            
        System.out.println("OrderId: "+order_id);
        orders_vo.setNamedWhereClauseParam("bOrderId",order_id);
        orders_vo.executeQuery();
        Row orderRow = orders_vo.first();
        if(orderRow!=null){

        String currentStatus = (String) orderRow.getAttribute("OrderStatus");
        
        // one more check: if order already delivered then should not pick that order

        if (!"Out for delivery".equalsIgnoreCase(currentStatus)) {
            // Update order_status to 'out_for_delivery'
            orderRow.setAttribute("OrderStatus", "Out for delivery");

            // Commit the transaction to save the changes
            am.getTransaction().commit();
            myOrders_vo.executeQuery();
            System.out.println("Order " + order_id + " has been set to 'out_for_delivery'.");
        } else {
            // If the order is already 'out_for_delivery'
            System.out.println("Order " + order_id + " is already 'out_for_delivery'.");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order is already out for delivery", null));

        }
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Picking Order error", null));
        }
            ViewObject delivery_vo = am.findViewObject(constants.getDelivery_agent_info_for_user());
            delivery_vo.executeQuery();

        } catch(Exception e){
            e.printStackTrace();
        }
        
        return null;

        
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public String deliverOrder() {
        // Add event code here...
        ApplicationModule am = getApplicationModule();
        ViewObject myOrders_vo = am.findViewObject(constants.getDelivery_agent_info_vo());
        ViewObject orders_vo = am.findViewObject(constants.getOrders_vo_name());
        orders_vo.setNamedWhereClauseParam("bOrderId",order_id);
        orders_vo.executeQuery();
        Row orderRow = orders_vo.first();
        if(orderRow!=null){

        String currentStatus = (String) orderRow.getAttribute("OrderStatus");
        
        // one more check: if order already delivered then should not pick that order
        if("Delivered".equalsIgnoreCase(currentStatus)){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order is already delivered", null));
            return null;
        }
        if ("Out for delivery".equalsIgnoreCase(currentStatus)) {
            // Update order_status to 'out_for_delivery'
            orderRow.setAttribute("OrderStatus", "Delivered");

            // Commit the transaction to save the changes
            am.getTransaction().commit();
            System.out.println("Order " + order_id + "You have successfully delivered the order");
        } else {
            // If the order is already 'out_for_delivery'
            System.out.println("Order " + order_id + "Order hasn't been picked up");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order hasn't been picked up", null));

        }
            ViewObject delivery_vo = am.findViewObject(constants.getDelivery_agent_info_for_user());
            delivery_vo.executeQuery();
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Picking Order error", null));
        }
        return null;
    }
}
