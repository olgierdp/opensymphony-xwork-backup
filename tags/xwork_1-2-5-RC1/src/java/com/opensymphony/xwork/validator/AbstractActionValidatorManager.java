package com.opensymphony.xwork.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

import com.opensymphony.xwork.validator.validators.VisitorFieldValidator;

/**
 * <p/>An abstract implementation of {@link ActionValidatorManager} interface that contains the logics of
 * validating action objects and utlity methods common to all subclass eg. method to build validator
 * key ( {@link #buildValidatorKey(Class, String)} ) that subclass will be interested in when eg.
 * saving the {@link com.opensymphony.xwork.validator.ValidatorConfig} in a Map cache.
 *
 * <p/>This class could serves as the base class for example {@link com.opensymphony.xwork.validator.DefaultActionValidatorManager}
 * which reads in the validation configuration information through &lt;action-class-name&gt;-&lt;action-alias&gt;-validation.xml or
 * &lt;action-class-name&gt;-validation.xml xml file format or a custom {@link com.opensymphony.xwork.validator.ActionValidatorManager}
 * that reads in the validation configuration through annotation where the diference between them is just how to get
 * the validation configuration information (in this example, through xml and annotation respectively). 
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public abstract class AbstractActionValidatorManager implements ActionValidatorManager {

    private static final Log LOG = LogFactory.getLog(AbstractActionValidatorManager.class);

    /**
     * Validates the given object using action and its context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @throws ValidationException if an error happens when validating the action.
     */
    public void validate(Object object, String context) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext);
    }

    /**
     * Validates an action give its context and a validation context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param validatorContext
     * @throws ValidationException if an error happens when validating the action.
     */
    public void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        List validators = getValidators(object.getClass(), context);
        validate(object, validators, validatorContext);
    }

    /**
     * Validates an action through a series of <code>validators</code> with
     * the given <code>validatorContext</code>
     *
     * @param object
     * @param validators
     * @param validatorContext
     * @throws ValidationException
     */
    public void validate(Object object, List validators, ValidatorContext validatorContext) throws ValidationException {
    	Set shortcircuitedFields = null;

        for (Iterator iterator = validators.iterator(); iterator.hasNext();) {
            final Validator validator = (Validator) iterator.next();
            try {
                validator.setValidatorContext(validatorContext);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Running validator: " + validator + " for object " + object);
                }

                FieldValidator fValidator = null;
                String fullFieldName = null;

                if (validator instanceof FieldValidator) {
                    fValidator = (FieldValidator) validator;
                    fullFieldName = new InternalValidatorContextWrapper(fValidator.getValidatorContext()).getFullFieldName(fValidator.getFieldName());

                    if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Short-circuited, skipping");
                        }

                        continue;
                    }
                }

                if (validator instanceof ShortCircuitableValidator && ((ShortCircuitableValidator) validator).isShortCircuit()) {
                    // get number of existing errors
                    List errs = null;

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection fieldErrors = (Collection) validatorContext.getFieldErrors().get(fullFieldName);

                            if (fieldErrors != null) {
                                errs = new ArrayList(fieldErrors);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection actionErrors = validatorContext.getActionErrors();

                        if (actionErrors != null) {
                            errs = new ArrayList(actionErrors);
                        }
                    }

                    validator.validate(object);

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection errCol = (Collection) validatorContext.getFieldErrors().get(fullFieldName);

                            if ((errCol != null) && !errCol.equals(errs)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Short-circuiting on field validation");
                                }

                                if (shortcircuitedFields == null) {
                                    shortcircuitedFields = new TreeSet();
                                }

                                shortcircuitedFields.add(fullFieldName);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection errCol = validatorContext.getActionErrors();

                        if ((errCol != null) && !errCol.equals(errs)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Short-circuiting");
                            }

                            break;
                        }
                    }

                    continue;
                }

                validator.validate(object);
            }
            finally {
                validator.setValidatorContext(null);
            }
        }
    }

    /**
     * Builds a key for validators - used when caching validators.
     *
     * @param clazz the action.
     * @param context the action's context.
     * @return a validator key which is the class name plus context.
     */
    protected String buildValidatorKey(Class clazz, String context) {
        StringBuffer sb = new StringBuffer(clazz.getName());
        sb.append("/");
        sb.append(context);
        return sb.toString();
    }


    protected List buildValidatorsFromValidatorConfig(List validatorConfigs) {
        ArrayList validators = new ArrayList(validatorConfigs.size());
        for (Iterator iterator = validatorConfigs.iterator(); iterator.hasNext(); ) {
            ValidatorConfig cfg = (ValidatorConfig) iterator.next();
            Validator validator = ValidatorFactory.getValidator(cfg);
            validator.setValidatorType(cfg.getType());
            validators.add(validator);
        }
        return validators;
    }


    /**
     * An {@link com.opensymphony.xwork.validator.ValidatorContext} wrapper that
     * returns the full field name
     * {@link com.opensymphony.xwork.validator.AbstractActionValidatorManager.InternalValidatorContextWrapper#getFullFieldName(String)}
     * by consulting it's parent if its an {@link com.opensymphony.xwork.validator.validators.VisitorFieldValidator.AppendingValidatorContext}.
     * <p/>
     * Eg. if we have nested Visitor
     * AddressVisitor nested inside PersonVisitor, when using the normal #getFullFieldName, we will get
     * "address.somefield", we lost the parent, with this wrapper, we will get "person.address.somefield".
     * This is so that the key is used to register errors, so that we don't screw up short-curcuit feature
     * when using nested visitor. See XW-571 (nested visitor validators break short-circuit functionality)
     * at http://jira.opensymphony.com/browse/XW-571
     */
    protected class InternalValidatorContextWrapper {
        private ValidatorContext validatorContext = null;

        InternalValidatorContextWrapper(ValidatorContext validatorContext) {
            this.validatorContext = validatorContext;
        }

        /**
         * Get the full field name by consulting the parent, so that when we are using nested visitors (
         * visitor nested inside visitor etc.) we still get the full field name including its parents.
         * See XW-571 for more details.
         * @param field
         * @return String
         */
        public String getFullFieldName(String field) {
            if (validatorContext instanceof VisitorFieldValidator.AppendingValidatorContext) {
                VisitorFieldValidator.AppendingValidatorContext appendingValidatorContext =
                        (VisitorFieldValidator.AppendingValidatorContext) validatorContext;
                return appendingValidatorContext.getFullFieldNameFromParent(field);
            }
            return validatorContext.getFullFieldName(field);
        }

    }
}
