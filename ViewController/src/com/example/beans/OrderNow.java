package com.example.beans;

import java.math.BigDecimal;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichListView;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichListItem;
import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.adf.view.rich.component.rich.input.RichInputNumberSpinbox;
import oracle.adf.view.rich.component.rich.layout.RichPanelFormLayout;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;

@SessionScoped
@ManagedBean(name="orderNowBean")
public class OrderNow {
    private RichListView listViewBinding;
    
    public OrderNow() {
        super();
    }
    public String submitAll(){
        
        System.out.println(listViewBinding.getChildren());
        for (UIComponent listItem : listViewBinding.getChildren()) {
            System.out.println(listItem.getId());
            
            if (listItem instanceof RichListItem) {
                // Access the panelFormLayout within the list item
                RichPanelFormLayout formLayout = (RichPanelFormLayout) listItem.findComponent("pfl2");
                System.out.println(formLayout);
                System.out.println("we are in if condition");
                
                if (formLayout != null) {
                    // Retrieve the outputFormatted components
                    System.out.println("Inside form layout");
                    RichOutputFormatted dishNameField = (RichOutputFormatted) formLayout.findComponent("dish_name");
                    System.out.println("dishField"+dishNameField.getAttributes().get("value"));
                    RichOutputFormatted priceField = (RichOutputFormatted) formLayout.findComponent("price");
                    System.out.println("priceField"+priceField);
                    RichOutputFormatted availabilityField = (RichOutputFormatted) formLayout.findComponent("availability");
                    RichOutputFormatted cuisineField = (RichOutputFormatted) formLayout.findComponent("cuisine");
                    RichOutputFormatted ratingField = (RichOutputFormatted) formLayout.findComponent("rating");
                    RichInputNumberSpinbox quantityField = (RichInputNumberSpinbox) formLayout.findComponent("quantity");
                    // Retrieve values
                    String dishName = (String) dishNameField.getValue();
                    String price = (String) priceField.getValue();
                    String availability = (String) availabilityField.getValue();
                    String cuisine = (String) cuisineField.getValue();
                    String rating = (String) ratingField.getValue();
                    String quantity = (String)quantityField.getValue(); // Assuming the inputNumberSpinbox returns Integer
                    // Process the values
                    System.out.println("Row Data: Dish Name = " + dishName +
                                       ", Price = " + price +
                                       ", Availability = " + availability +
                                       ", Cuisine = " + cuisine +
                                       ", Rating = " + rating +
                                       ", Quantity = " + quantity);
                }
            }
        }

        return null;
    }

    public void setListViewBinding(RichListView listViewBinding) {
        this.listViewBinding = listViewBinding;
    }

    public RichListView getListViewBinding() {
        return listViewBinding;
    }

    public void submitOrder(ActionEvent actionEvent) {
        BindingContext bindingContext = BindingContext.getCurrent();
           
           // Get the current binding container
           DCBindingContainer bindingContainer = (DCBindingContainer) bindingContext.getCurrentBindingsEntry();
           
           // Get the iterator binding for your View Object
           DCIteratorBinding iteratorBinding = bindingContainer.findIteratorBinding("MenuItems_CustApp_VOIterator");
           
           // Get the View Object to iterate through the rows
           ViewObject viewObject = iteratorBinding.getViewObject();
           
           // Get the RowSetIterator to loop through rows
           RowSetIterator rowSetIterator = viewObject.createRowSetIterator(null); // Create a RowSetIterator
           
           // Loop through all rows
           while (rowSetIterator.hasNext()) {
               // Get the current row
               Row currentRow = rowSetIterator.next();
               
               // Access the attribute values for the current row
               DBSequence itemIdDbSeq = (DBSequence) currentRow.getAttribute("ItemId");
               Integer itemId = Integer.parseInt(itemIdDbSeq != null ? itemIdDbSeq.toString() : null);

               String dishName = (String) currentRow.getAttribute("DishName");
               BigDecimal price = (BigDecimal) currentRow.getAttribute("Price");
               String availability = (String) currentRow.getAttribute("Availability");
               Integer quantity = Integer.parseInt(currentRow.getAttribute("Quantity")==null?"0":currentRow.getAttribute("Quantity").toString());
           }
           
           // Close the RowSetIterator when done
           rowSetIterator.closeRowSetIterator();
    }
}
