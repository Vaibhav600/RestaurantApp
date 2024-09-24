package com.example.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;


import org.apache.myfaces.trinidad.event.DisclosureEvent;


@SessionScoped
@ManagedBean(name="tabListenerBean")
public class TabListenerBean {
    public TabListenerBean() {
        super();
    }
    ConstantBean constants = new ConstantBean();
    public void onTabChange(DisclosureEvent disclosureEvent) {
            if (disclosureEvent.isExpanded()) {
            System.out.println("Before Listener");

                        refreshOrdersVO();

            System.out.println("After Listener");
            }
        }

        public void refreshOrdersVO() {
            // Get the current binding context and the iterator for OrdersVO
            DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
                   
                   // Get the iterator binding for OrdersVO
            DCIteratorBinding iter = bindings.findIteratorBinding(constants.getTo_deliver_orders_vo_iter());


            // Execute the query to refresh the View Object
            if (iter != null) {
                iter.executeQuery();
            }
            DCIteratorBinding iter2 = bindings.findIteratorBinding(constants.getTo_deliver_order_by_agent_vo_iter());

            if (iter2 != null) {
                iter2.executeQuery();
            }
        }
}
