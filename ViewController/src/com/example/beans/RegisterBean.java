package com.example.beans;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
@ManagedBean(name="registerBean")
public class RegisterBean {
    private String password;
    private String confirm_password;
    private String email;
    private String usersVO_name = "G3UsersVO";
    private String default_role = "customer";
    
    ConstantBean constants = new ConstantBean();

    public RegisterBean() {
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
    public void createUser(ApplicationModule am, ViewObject vo, String email, String encrypted_password) {
        Row newUserRow = vo.createRow();
        newUserRow.setAttribute("Password", encrypted_password);
        newUserRow.setAttribute("Email", email);
        newUserRow.setAttribute("Role", default_role);
        vo.insertRow(newUserRow);
        try{
            am.getTransaction().commit();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String registerUser() {
        try {
            if (password.equals(confirm_password)) {
                String encryptedPassword = encryptPassword(password);
                ApplicationModule am = getApplicationModule();

                if (am != null) {
                    ViewObject vo = am.findViewObject(usersVO_name);
                    createUser(am, vo, email, encryptedPassword);
                    FacesContext.getCurrentInstance().addMessage(null,
                                                                 new FacesMessage("User registration successfully"));
                    return constants.getLogin_page_navigation();

                } else {
                    System.out.println("Application Module is null");
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Password Doesn't Match"));
                return constants.getError_page_navigation();
            }
        } catch (Exception e) {
            // TODO: Add catch code
            e.printStackTrace();
            System.out.println(e);
        }
        return null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    public String getConfirm_password() {
        return confirm_password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
