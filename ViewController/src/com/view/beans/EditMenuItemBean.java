package com.view.beans;

import com.example.beans.ConstantBean;

import javax.faces.bean.ManagedBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;


@ManagedBean(name="editMEnuItemBean")
public class EditMenuItemBean {
    private String menuItem;
    public EditMenuItemBean() {
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
        } 
        else {
            System.out.println("BindingContext is null");
        }
        return null;
    }

    public void setMenuItem(String menuItem) {
        System.out.println("set item: "+menuItem);
        this.menuItem = menuItem;
    }

    public String getMenuItem() {
        System.out.println("get item: "+menuItem);
        return menuItem;
    }
    
    public String edit(){
        try{
        System.out.println("menu item geot: "+this.menuItem);
        ApplicationModule am = getApplicationModule();
        ViewObject menusVO = am.findViewObject(constants.getMenu_restaurant_vo());
        
        Row rowToDelete = null;
        for (Row row : menusVO.getAllRowsInRange()) {
            DBSequence rowItemId = (DBSequence) row.getAttribute("ItemId"); // Use correct attribute name for ItemId
                        String item = rowItemId.toString(); // Replace with actual attribute name
            if (item.equals(this.menuItem)) {
                rowToDelete = row;
                break;
            }
        }

        
            if (rowToDelete != null) {
                rowToDelete.remove();
                System.out.println("Menu item removed: " + this.menuItem);
                

                menusVO.getApplicationModule().getTransaction().commit();
                System.out.println("Transaction committed.");
            } else {
                System.out.println("Menu item not found.");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
