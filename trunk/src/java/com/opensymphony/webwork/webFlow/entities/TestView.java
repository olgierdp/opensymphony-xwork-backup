/*
 * Created on Aug 13, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.entities;

import junit.framework.TestCase;

import java.util.List;

/**
 * TODO Describe TestView
 */
public class TestView extends TestCase {

    public void testXworkViewLinkFinder() {
        String testLine1 = "nothing here";
        List testList1 = XworkView.findActionLinks(testLine1);
        assertTrue("no action links in '" + testLine1 + "'", testList1.size() == 0);
        String testLine2 = "there/is/one.action?in=thisLine";
        List testList2 = XworkView.findActionLinks(testLine2);
        assertTrue("1 action link in '" + testLine2 + "'", testList2.size() == 1);
        String testLine3 = "<ww:url value=\"'registrationShowCourses.action?schoolId='+id\"/>";
        List testList3 = XworkView.findActionLinks(testLine2);
        assertTrue("1 action link in '" + testLine3 + "'", testList3.size() == 1);
    }
}