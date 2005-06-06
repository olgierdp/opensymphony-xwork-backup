/*
 * Created on Aug 13, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.entities;

import com.opensymphony.webwork.config.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Describe XworkView
 */
public class XworkView implements View {

    private static final Log LOG = LogFactory.getLog(XworkView.class);
    private File file = null;
    private Set targets = null;
    private static String actionRegex = "([A-Za-z0-9\\._\\-\\!]+\\." + Configuration.get("webwork.action.extension") + ")";
    private static Pattern actionPattern = Pattern.compile(actionRegex);

    public XworkView() {
    }

    public XworkView(File file) {
        this.file = file;
    }

    private File getFile() {
        return this.file;
    }

    public String getName() {
        String name = null;
        if (getFile() != null) {
            name = getFile().getName();
        }
        return name;
    }

    public static List findActionLinks(String text) {
        List actionLinkList = new ArrayList();
        Matcher linkMatcher = actionPattern.matcher(text);
        while (linkMatcher.find()) {
            String actionName = linkMatcher.group(1);
            if (actionName != null)
                actionLinkList.add(actionName);
        }
        return actionLinkList;
    }

    public Set getTargets() {
        if (this.targets == null) {
            this.targets = new HashSet();
            try {
                BufferedReader input = new BufferedReader(new FileReader(getFile()));
                String line = null;
                while ((line = input.readLine()) != null) {
                    List actionNameList = findActionLinks(line);
                    if (actionNameList.size() > 0) {
                        for (Iterator iter = actionNameList.iterator(); iter.hasNext();) {
                            String actionName = (String) iter.next();
                            this.targets.add(actionName);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                LOG.error("FileNotFoundException: " + getFile());
            } catch (IOException e) {
                LOG.error("IOException");
            }
        }
        return this.targets;
    }

    public String toString() {
        return this.getName();
    }
}