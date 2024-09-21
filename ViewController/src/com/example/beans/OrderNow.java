package com.example.beans;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichListView;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.data.RichListItem;
import oracle.adf.view.rich.component.rich.data.RichListView;
import oracle.adf.view.rich.component.rich.input.RichInputNumberSpinbox;
import oracle.adf.view.rich.component.rich.layout.RichPanelFormLayout;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;

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
            System.out.println(listItem);
            
            if (listItem instanceof RichListItem) {
                // Access the panelFormLayout within the list item
                RichPanelFormLayout formLayout = (RichPanelFormLayout) listItem.findComponent("pfl2");
                System.out.println(formLayout);
                System.out.println("we are in if condition");
                
                if (formLayout != null) {
                    // Retrieve the outputFormatted components
                    RichOutputFormatted dishNameField = (RichOutputFormatted) formLayout.findComponent("dish_name");
            
                    RichOutputFormatted priceField = (RichOutputFormatted) formLayout.findComponent("price");
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
        System.out.println(listViewBinding.getChildren());
        
        for (UIComponent listItem : listViewBinding.getChildren()) {
            System.out.println(listItem);
            
           
            
            if (listItem instanceof RichListItem) {
                // Access the panelFormLayout within the list item
                RichPanelFormLayout formLayout = (RichPanelFormLayout) listItem.findComponent("pfl2");
                System.out.println(formLayout);
                System.out.println("we are in if condition");
                
                if (formLayout != null) {
                    // Retrieve the outputFormatted components
                    RichOutputFormatted dishNameField = (RichOutputFormatted) formLayout.findComponent("dish_name");
            
                    RichOutputFormatted priceField = (RichOutputFormatted) formLayout.findComponent("price");
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
    }
}
