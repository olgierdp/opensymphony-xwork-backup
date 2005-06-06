package com.opensymphony.webwork.config_browser;

import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.validator.ActionValidatorManager;

import java.util.Collections;
import java.util.List;

/**
 * ListValidatorsAction loads the validations for a given class and context
 *
 * @author Jason Carreira
 *         Date: May 31, 2004 5:06:16 PM
 */
public class ListValidatorsAction extends ActionSupport {
    private Class clazz;
    private String context;
    List validators = Collections.EMPTY_LIST;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String stripPackage(Class clazz) {
        return clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List getValidators() {
        return validators;
    }

    public String execute() throws Exception {
        loadValidators();
        return super.execute();
    }

    protected void loadValidators() {
        validators = ActionValidatorManager.getValidators(clazz, context);
    }
}
