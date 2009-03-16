package com.opensymphony.xwork;

/**
 * Contains constants used in {@link Configuration}'s parameters. eg.
 *
 * <pre>
 *    &lt;xwork&gt;
 *       &lt;parameters&gt;
 *          &lt;parameter name="useOgnlEnhancement" value="false" /&gt;
 *       &lt;/parameters&gt;
 *    &lt;/xwork&gt;
 * </pre>
 *
 * The "useOgnlEnhancement" parameter name is a constant in this class.
 *
 * @author tmjee
 * @version $Date$ $Id$
 */
public class XWorkConstants {
    
    /**
     * Parameter name of Ognl Enhancement. eg.
     * <pre>
     *  &lt;xwork&gt;
     *       &lt;parameters&gt;
     *          &lt;parameter name="useOgnlEnhancement" value="false" /&gt;
     *       &lt;/parameters&gt;
     *    &lt;/xwork&gt;
     * </pre>
     */
    public static final String XWORK_USE_OGNL_ENHANCEMENT = "useOgnlEnhancement";
}
