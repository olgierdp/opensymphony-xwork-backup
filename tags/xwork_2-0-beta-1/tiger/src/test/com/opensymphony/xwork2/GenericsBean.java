package com.opensymphony.xwork2;

import java.util.List;

/**
 * <code>GenericsBean</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class GenericsBean {

    private List<Double> doubles;

    /**
     * @return Returns the doubles.
     */
    public List<Double> getDoubles() {
        return doubles;
    }

    /**
     * @param doubles The doubles to set.
     */
    public void setDoubles(List<Double> doubles) {
        this.doubles = doubles;
    }
}
