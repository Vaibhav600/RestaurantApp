package com.example.beans;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.RequestScoped;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.context.AdfFacesContext;
import javax.faces.event.ActionEvent;

@RequestScoped
@ManagedBean(name="deleteRestaurantBean")
public class DeleteRestaurantBean {
    ConstantBean constants = new ConstantBean();
    private RichPopup deletePopup;
    
    public void setDeletePopup(RichPopup deletePopup) {
            this.deletePopup = deletePopup;
        }

        public RichPopup getDeletePopup() {
            return deletePopup;
        }
    public DeleteRestaurantBean() {
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
    public void openDeletePopup(ActionEvent actionEvent) {
            RichPopup.PopupHints hints = new RichPopup.PopupHints();
            deletePopup.show(hints); // Open the popup
        }

        // Method to confirm deletion
        public void confirmDelete(ActionEvent actionEvent) {
            deleteSelectedRestaurant(); // Perform delete
            deletePopup.hide(); // Close the popup after delete
        }

        // Method to close the popup without deleting
        public void closeDeletePopup(ActionEvent actionEvent) {
            deletePopup.hide(); // Close the popup when No is clicked
        }
    public String deleteSelectedRestaurant() {
           try {
               // Get the Application Module and the View Object
               ApplicationModule am = getApplicationModule();
               ViewObject restaurants_vo = am.findViewObject(constants.getRestaurant_vo_name());

               // Find the current selected row
               Row currentRow = restaurants_vo.getCurrentRow();

               if (currentRow != null) {
                   // Remove the row from the View Object
                   currentRow.remove();

                   // Commit the transaction
                   am.getTransaction().commit();

                   // Optionally, display a success message
                   FacesContext.getCurrentInstance().addMessage(null,
                       new FacesMessage(FacesMessage.SEVERITY_INFO, "Restaurant deleted successfully.", null));
               } else {
                   // Handle case where no row is selected
                   FacesContext.getCurrentInstance().addMessage(null,
                       new FacesMessage(FacesMessage.SEVERITY_WARN, "No restaurant selected for deletion.", null));
               }
           } catch (Exception e) {
               System.out.println(e);
               FacesContext.getCurrentInstance().addMessage(null,
                   new FacesMessage(FacesMessage.SEVERITY_ERROR, "Restaurant deletion failed.", null));
               
           }
           return null; // Navigate or stay on the same page
       }

}
