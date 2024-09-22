package com.example.beans;

import javax.faces.bean.ManagedBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.DBSequence;

@ManagedBean(name="deleteCouponBean")
public class DeleteCouponBean {
    public DeleteCouponBean() {
        super();
    }
    private String couponId;
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
    
    public String deleteCoupon(){
        try{
        System.out.println("coupon item geot: "+this.couponId);
        ApplicationModule am = getApplicationModule();
        ViewObject menusVO = am.findViewObject(constants.getCoupon_vo());
        
        Row rowToDelete = null;
            for (Row row : menusVO.getAllRowsInRange()) {
                DBSequence rowCouponId = (DBSequence) row.getAttribute("CouponId"); // Use correct attribute name for ItemId
                            String item = rowCouponId.toString(); // Replace with actual attribute name
                if (item.equals(this.couponId)) {
                    rowToDelete = row;
                    break;
                }
            }

         
            if (rowToDelete != null) {
                rowToDelete.remove();
                System.out.println("Coupon item removed: " + this.couponId);
       
                menusVO.getApplicationModule().getTransaction().commit();
                System.out.println("Transaction committed.");
            } else {
                System.out.println("Coupon item not found.");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponId() {
        return couponId;
    }
}
