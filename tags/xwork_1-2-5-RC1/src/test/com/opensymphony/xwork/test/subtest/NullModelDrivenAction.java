package com.opensymphony.xwork.test.subtest;

import com.opensymphony.xwork.ModelDrivenAction;

/**
 * Extends ModelDrivenAction to return a null model.
 *
 * @author Mark Woon
 */
public class NullModelDrivenAction extends ModelDrivenAction {

    /**
     * @return the model to be pushed onto the ValueStack instead of the Action itself
     */
    public Object getModel() {
        return null;
    }
}
