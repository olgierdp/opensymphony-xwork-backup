package com.opensymphony.xwork.interceptor;

import com.opensymphony.xwork.ActionInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * TODO: Give a description of the Interceptor.
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * TODO: Describe the paramters for this Interceptor.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * TODO: Discuss some possible extension of the Interceptor.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- TODO: Describe how the Interceptor reference will effect execution --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *      TODO: fill in the interceptor reference.
 *     &lt;interceptor-ref name=""/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * I18nInterceptor
 *
 * @author Aleksei Gopachenko
 */
public class I18nInterceptor implements Interceptor {
    protected static final Log log = LogFactory.getLog(I18nInterceptor.class);

    public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_PARAMETER = "request_locale";

    protected String parameterName = DEFAULT_PARAMETER;
    protected String attributeName = DEFAULT_SESSION_ATTRIBUTE;

    /**
     */
    public I18nInterceptor() {
        if (log.isDebugEnabled()) log.debug("new I18nInterceptor()");
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     */
    public void init() {
        if (log.isDebugEnabled()) log.debug("init()");
    }

    /**
     */
    public void destroy() {
        if (log.isDebugEnabled()) log.debug("destroy()");
    }

    /**
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("intercept '"
                    + invocation.getProxy().getNamespace() + "/"
                    + invocation.getProxy().getActionName() + "' { ");
        }

        //get requested locale
        Object requested_locale = invocation.getInvocationContext().getParameters().get(parameterName);
        if (requested_locale != null && requested_locale.getClass().isArray() && ((Object[]) requested_locale).length == 1) {
            requested_locale = ((Object[]) requested_locale)[0];
        }

        if (log.isDebugEnabled()) {
            log.debug("requested_locale=" + requested_locale);
        }

        //save it in session
        Map session = invocation.getInvocationContext().getSession();
        if (session != null) {
            if (requested_locale != null) {
                Locale locale = (requested_locale instanceof Locale) ?
                        (Locale) requested_locale : localeFromString(requested_locale.toString());
                if (log.isDebugEnabled()) {
                    log.debug("store locale=" + locale);
                }

                if (locale != null) {
                    session.put(attributeName, locale);
                }
            }

            //set locale for action
            Object locale = session.get(attributeName);
            if (locale != null && locale instanceof Locale) {
                if (log.isDebugEnabled()) {
                    log.debug("apply locale=" + locale);
                }

                invocation.getInvocationContext().setLocale((Locale) locale);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("before Locale=" + invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();
        if (log.isDebugEnabled()) {
            log.debug("after Locale=" + invocation.getStack().findValue("locale"));
        }

        if (log.isDebugEnabled()) {
            log.debug("intercept } ");
        }

        return result;
    }

    Locale localeFromString(String localeStr) {
        if ((localeStr == null) || (localeStr.trim().length() == 0) || (localeStr.equals("_"))) {
            return Locale.getDefault();
        }

        int index = localeStr.indexOf('_');
        if (index < 0) {
            return new Locale(localeStr);
        }

        String language = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language);
        }

        localeStr = localeStr.substring(index + 1);
        index = localeStr.indexOf('_');
        if (index < 0) {
            return new Locale(language, localeStr);
        }

        String country = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language, country);
        }

        localeStr = localeStr.substring(index + 1);
        return new Locale(language, country, localeStr);
    }

}