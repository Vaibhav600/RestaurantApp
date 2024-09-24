package com.example.beans;

import java.math.BigDecimal;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Number;


@SessionScoped
@ManagedBean(name="orderNowBean")
public class OrderNow {
    private RichListView listViewBinding;
    ConstantBean constants = new ConstantBean();

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
    public Number createOrder(ApplicationModule am){
        // Get Selected Restaurant ID
         ViewObject selectedRestVO = am.findViewObject(constants.getRest_for_custApp_vo_name());
         Row selectedRestaurant = selectedRestVO.first();
         DBSequence dbSequence = (DBSequence) selectedRestaurant.getAttribute("RestaurantId");
         Integer selectedRestaurantId = dbSequence.getSequenceNumber().intValue();
         
         // Get Logged in Customer Id
         HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
         Object userIdObject = session.getAttribute("userId");
         int CustomerId = Integer.parseInt(userIdObject.toString());
        
        // Now Create Order       
        ViewObject orders_vo = am.findViewObject(constants.getOrders_vo_name());

        // Set Where Clause to find any PENDING order for the given CustomerId
        // Use the exact parameter names in the where clause
        orders_vo.setWhereClause("G3OrdersEO.CUSTOMER_ID = :custId AND UPPER(G3OrdersEO.PAYMENT_STATUS) = UPPER(:paymentStatus)");
        // Define named where clause parameters. Make sure the names here match the ones in the whereClause.
        orders_vo.defineNamedWhereClauseParam("custId", null, null);
        orders_vo.defineNamedWhereClauseParam("paymentStatus", null, null);

        // Set the actual values for the named parameters
        orders_vo.setNamedWhereClauseParam("custId", CustomerId); // Correct parameter name case
        orders_vo.setNamedWhereClauseParam("paymentStatus", "PENDING"); // Correct parameter name case

        orders_vo.executeQuery();

        // Get the first order (if any) that matches the criteria
        Row existing_order_row = orders_vo.first();
        System.out.println(existing_order_row);
        
        if (existing_order_row != null) {
            // If a pending order exists, delete it
            existing_order_row.remove(); // Removes the row
            am.getTransaction().commit(); // Commit the changes to the database
        }

        // Clear the whereClause and named parameters to reset the ViewObject
        orders_vo.removeNamedWhereClauseParam("custId");
        orders_vo.removeNamedWhereClauseParam("paymentStatus");
        orders_vo.setWhereClause(null);  // Clear where clause
        orders_vo.executeQuery();  // Re-execute without the filter to refresh data if necessary

//         exisiting_order_row.remove();
//        orders_vo.executeQuery();
//         am.getTransaction().commit();
//         orders_vo.clearCache();
//         orders_vo.setWhereClause(null);
//        orders_vo.executeQuery();

         Row new_order = orders_vo.createRow();
         new_order.setAttribute("RestaurantId", selectedRestaurantId);
         new_order.setAttribute("CustomerId", CustomerId);
         new_order.setAttribute("OrderStatus", "Ordered");
         new_order.setAttribute("PaymentStatus", "Pending");
         
         
         // new_order.setAttribute("CouponId", );
         // new_order.setAttribute("TotalAmount", );
         
        orders_vo.insertRow(new_order); 
        
        try{
            am.getTransaction().commit();
            // Retrieve the newly created order_id 
            DBSequence newOrderId = (DBSequence) new_order.getAttribute("OrderId");
            return newOrderId.getSequenceNumber();
        } catch(Exception e){
            e.printStackTrace();
        }
        
        // return order id here 
        return null;
    }
    public Number createOrderItem(Number OrderId, Number MenuItemId, Number Quantity, Number Price, ApplicationModule am){
        // Get Selected Order Item VO
         ViewObject order_items_vo = am.findViewObject(constants.getOrderItem_vo_name());
         Row new_order_item = order_items_vo.createRow();
         new_order_item.setAttribute("OrderId", OrderId);
         new_order_item.setAttribute("ItemId", MenuItemId);
         new_order_item.setAttribute("Quantity", Quantity);
         new_order_item.setAttribute("Price", Price);
         
         Number item_total = Quantity.multiply(Price);
         new_order_item.setAttribute("ItemTotal", item_total);
         
        order_items_vo.insertRow(new_order_item); 
        
        try{
            am.getTransaction().commit();
            DBSequence newOrderItemId = (DBSequence) new_order_item.getAttribute("Id");
            return newOrderItemId.getSequenceNumber();            
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void submitOrder(ActionEvent actionEvent) throws SQLException {
            BindingContext bindingContext = BindingContext.getCurrent();
           
            // Get the current binding container
            DCBindingContainer bindingContainer = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
           
            // Get the iterator binding for your View Object
            DCIteratorBinding iteratorBinding = bindingContainer.findIteratorBinding("MenuItems_CustApp_VOIterator");
           
            // Get the View Object to iterate through the rows
            ViewObject viewObject = iteratorBinding.getViewObject();
           
            // Get the RowSetIterator to loop through rows
            RowSetIterator rowSetIterator = viewObject.createRowSetIterator(null); // Create a RowSetIterator
            
            // Get Application Module           
            ApplicationModule am = getApplicationModule();
            
            // Create a new Order
            Number order_id = createOrder(am);
            
            // Loop through all rows
            while (rowSetIterator.hasNext()) {                
                // Get the current row
                Row currentRow = rowSetIterator.next();
               
                // Access the attribute values for the current row
                DBSequence itemIdDbSeq = (DBSequence) currentRow.getAttribute("ItemId");
                Number menu_item_id = itemIdDbSeq != null ? itemIdDbSeq.getSequenceNumber() : null;
                Number price =  new Number(currentRow.getAttribute("Price"));
                Number quantity = currentRow.getAttribute("Quantity")==null? new Number(0) : new Number(currentRow.getAttribute("Quantity"));
                
                if(quantity.equals(new Number(0))) continue;

                String availability = currentRow.getAttribute("Availability").toString();
                Number order_item_id = createOrderItem(order_id, menu_item_id, quantity, price, am);              
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order Created Successfully"));

           // Close the RowSetIterator when done
            rowSetIterator.closeRowSetIterator();
    }
    public OrderNow() {
        super();
    }
    public void setListViewBinding(RichListView listViewBinding) {
        this.listViewBinding = listViewBinding;
    }

    public RichListView getListViewBinding() {
        return listViewBinding;
    }
}
