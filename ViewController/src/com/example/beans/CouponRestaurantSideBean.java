import com.example.beans.ConstantBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.BindingContainer;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDataControl;

import oracle.jbo.ApplicationModule;

@ManagedBean
@ViewScoped
public class CouponRestaurantSideBean {
    private RowSetIterator couponIterator;
    ConstantBean constants = new ConstantBean();
    public CouponRestaurantSideBean() {
        // Initialize the coupon iterator in the constructor
        initializeIterator();
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

    private void initializeIterator() {
        ApplicationModule am = getApplicationModule();
       
       
        ViewObject couponVO =am.findViewObject(constants.getCoupons_restaurant_user_vo()); 
        if (couponVO != null) {
            this.couponIterator = couponVO.createRowSetIterator("CouponForRestaurantForUserIterator");
            couponIterator.reset();
        }
    }

    // Getter for the coupon iterator
    public RowSetIterator getCouponIterator() {
        return couponIterator;
    }
}
