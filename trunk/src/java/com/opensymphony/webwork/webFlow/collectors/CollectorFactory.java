/*
 * Created on Aug 12, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.collectors;


/**
 * PatternBox: "Creator" implementation.
 * <ul>
 * <li>declares the factory method, which returns an object of type Product. Creator may also define a default implementation of the factory method that returns a default ConcreteProduct object.</li>
 * <li>may call the factory method to create a Product object.</li>
 * </ul>
 *
 * @author <a href="mailto:dirk.ehms@patternbox.com">Dirk Ehms</a>
 * @author mgreer
 */
public abstract class CollectorFactory {

    /**
     * The ConcreteCreator implementation have to return a ConcreteProduct.
     */
    public static Collector createCollector(int type) {
        Collector collector = null;
        if (type == Collector.TYPE_WEBWORK2)
            collector = new WebWork2Collector();
        return collector;
    }

}
