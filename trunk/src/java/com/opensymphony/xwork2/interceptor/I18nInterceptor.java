/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor that handles setting the locale specified in a session as the locale for the current action request.
 * In addition, this interceptor will look for a specific HTTP request parameter and set the locale to whatever value is
 * provided. This means that this interceptor can be used to allow for your application to dynamically change the locale
 * for the user's session. This is very useful for applications that require multi-lingual support and want the user to
 * be able to set his or her language preference at any point. The locale parameter is removed during the execution of
 * this interceptor, ensuring that properties aren't set on an action (such as request_locale) that have no typical
 * corresponding setter in your action.
 *
 * <p/>For example, using the default parameter name, a request to <b>foo.action?request_locale=en_US</b>, then the
 * locale for US English is saved in the user's session and will be used for all future requests.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>parameterName (optional) - the name of the HTTP request parameter that dictates the locale to switch to and save
 * in the session. By default this is <b>request_locale</b></li>
 *
 * <li>attributeName (optional) - the name of the session key to store the selected locale. By default this is
 * <b>WW_TRANS_I18N_LOCALE</b></li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extensions points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="i18n"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Aleksei Gopachenko
 */
public class I18nInterceptor extends AbstractInterceptor {
    protected static final Logger LOG = LoggerFactory.getLogger(I18nInterceptor.class);

    public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_PARAMETER = "request_locale";

    protected String parameterName = DEFAULT_PARAMETER;
    protected String attributeName = DEFAULT_SESSION_ATTRIBUTE;

    public I18nInterceptor() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("new I18nInterceptor()");
        }
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("intercept '"
                    + invocation.getProxy().getNamespace() + "/"
                    + invocation.getProxy().getActionName() + "' { ");
        }
        //get requested locale
        Map<String, Object> params = invocation.getInvocationContext().getParameters();
        Object requested_locale = params.remove(parameterName);
        if (requested_locale != null && requested_locale.getClass().isArray()
                && ((Object[]) requested_locale).length == 1) {
            requested_locale = ((Object[]) requested_locale)[0];

            if (LOG.isDebugEnabled()) {
                LOG.debug("requested_locale=" + requested_locale);
            }
        }

        //save it in session
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        Locale locale = null;
        if (requested_locale != null) {
            locale = (requested_locale instanceof Locale) ?
                    (Locale) requested_locale : LocalizedTextUtil.localeFromString(requested_locale.toString(), null);
            if (locale != null && LOG.isDebugEnabled()) {
                LOG.debug("applied request locale=" + locale);
            }
        }
        if (session != null) {
            synchronized (session) {
                if (locale == null) {
                    // check session for saved locale
                    Object sessionLocale = session.get(attributeName);
                    if (sessionLocale != null && sessionLocale instanceof Locale) {
                        locale = (Locale) sessionLocale;
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("applied session locale=" + locale);
                        }
                    } else {
                        // no overriding locale definition found, stay with current invokation (=browser) locale
                        locale = invocation.getInvocationContext().getLocale();
                        if (locale != null && LOG.isDebugEnabled()) {
                            LOG.debug("applied invocation context locale=" + locale);
                        }
                    }
                }
                session.put(attributeName, locale);
            }
        }
        saveLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("before Locale=" + invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();
        if (LOG.isDebugEnabled()) {
            LOG.debug("after Locale=" + invocation.getStack().findValue("locale"));
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("intercept } ");
        }

        return result;
    }

    /**
     * Save the given locale to the ActionInvocation.
     *
     * @param invocation The ActionInvocation.
     * @param locale The locale to save.
     */
    protected void saveLocale(ActionInvocation invocation, Locale locale) {
        invocation.getInvocationContext().setLocale(locale);
    }

}
