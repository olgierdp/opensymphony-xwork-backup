/*
 * Created on Aug 12, 2004 by mgreer
 */
package com.opensymphony.webwork.webFlow.renderers;

import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.webFlow.XWorkConfigRetriever;
import com.opensymphony.webwork.webFlow.entities.View;
import com.opensymphony.xwork.ActionChainResult;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Renders flow diagram to the console at info level
 */
public class DOTRenderer implements Renderer {

    private static final Log LOG = LogFactory.getLog(DOTRenderer.class);

    private String output;

    public DOTRenderer(String output) {
        this.output = output;
    }

    public void render() {
        DotGraph graph = new DotGraph();
        graph.attribute("action", "color", "coral1");
        graph.attribute("view", "color", "darkseagreen2");
        graph.attribute("start", "color", "gold");
        graph.attribute("start", "shape", "octagon");

        HashMap viewMap = new HashMap();

        Set namespaces = XWorkConfigRetriever.getNamespaces();
        for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
            String namespace = (String) iter.next();

            Set actionNames = XWorkConfigRetriever.getActionNames(namespace);
            for (Iterator iterator = actionNames.iterator(); iterator.hasNext();) {
                String actionName = (String) iterator.next();
                ActionConfig actionConfig = XWorkConfigRetriever.getActionConfig(namespace,
                        actionName);
                String action = namespace + "/" + actionName + "." + Configuration.get("webwork.action.extension");

                graph.add_node("action", action, actionName);

                Set resultNames = actionConfig.getResults().keySet();
                for (Iterator iterator2 = resultNames.iterator(); iterator2.hasNext();) {
                    String resultName = (String) iterator2.next();
                    ResultConfig resultConfig = ((ResultConfig) actionConfig.getResults().get(resultName));
                    String resultClassName = resultConfig.getClassName();

                    if (resultClassName.equals(ActionChainResult.class.getName())) {

                    } else if (resultClassName.indexOf("Dispatcher") != -1
                            || resultClassName.indexOf("Velocity") != -1
                            || resultClassName.indexOf("Freemarker") != -1) {
                        if (resultConfig.getParams().get("location") == null) {
                            continue;
                        }

                        String location = getViewLocation((String) resultConfig.getParams().get("location"), namespace);
                        if (location.endsWith((String) Configuration.get("webwork.action.extension"))) {
                            addLink(action, location.substring(1), resultConfig.getName(), graph);
                        } else {
                            graph.add_node("view", location, null);
                            graph.add_link(action, location, resultConfig.getName());

                            View viewFile = XWorkConfigRetriever.getView(namespace, actionName, resultName);
                            if (viewFile != null) {
                                viewMap.put(location, viewFile);
                            }
                        }
                    } else if (resultClassName.indexOf("Jasper") != -1) {

                    } else if (resultClassName.indexOf("XSLT") != -1) {

                    } else if (resultClassName.indexOf("Redirect") != -1 ) {
                        // check if the redirect is to an action -- if so, link it
                        String location = getViewLocation((String) resultConfig.getParams().get("location"), namespace);
                        if (location.endsWith((String) Configuration.get("webwork.action.extension"))) {
                            addLink(action, location.substring(1), resultConfig.getName(), graph);
                        }
                    }
                }
            }
        }

        for (Iterator iterator = viewMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String view = (String) entry.getKey();
            View viewFile = (View) entry.getValue();
            Set targets = viewFile.getTargets();
            for (Iterator iterator1 = targets.iterator(); iterator1.hasNext();) {
                String viewTarget = (String) iterator1.next();

                try {
                    // if the target isn't absolute, assume the same namespce as this
                    if (!viewTarget.startsWith("/") && view.lastIndexOf('/') >= 1) {
                        viewTarget = view.substring(1, view.lastIndexOf('/')) + "/" + viewTarget;
                    }

                    addLink(view, viewTarget, "", graph);
                } catch (Throwable e) {
                    System.out.println("Problem with view " + view + " and target " + viewTarget);
                }
            }
        }

        try {
            FileWriter out = new FileWriter(output + "/out.dot");
            out.write(graph.to_s(true));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addLink(String view, String target, String label, DotGraph graph) {
        int bang = target.indexOf('!');
        if (bang != -1) {
            // map the link back to the action using the command as the label
            String command = target.substring(bang + 1, target.lastIndexOf('.'));
            String action = target.substring(0, bang) + target.substring(target.lastIndexOf('.'));
            graph.add_link(view, "/" + action, label + "\\n(!" + command + ")");
        } else {
            graph.add_link(view, "/" + target, label);
        }
    }

    private String getViewLocation(String location, String namespace) {
        String view = null;
        if (!location.startsWith("/")) {
            view = namespace + "/" + location;
        } else {
            view = location;
        }

        if (view.indexOf('?') != -1) {
            view = view.substring(0, view.indexOf('?'));
        }

        return view;
    }

    /**
     * @author Joe Walnes <joe@truemesh.com>
     */
    static class DotGraph {
        Map clusters; // Map of String -> (Map of String -> Object[] { Map attrs, String type })
        Map links; // Map of Link->Map of params
        Map attributes;

        public DotGraph() {
            clusters = new HashMap();
            clusters.put("default", new HashMap());
            links = new HashMap();
            attributes = new HashMap();
        }

        public void add_node(String type, String name, String label) {

            String[] ret = split_cluster(name);
            String cluster = ret[0];
            name = ret[1];

            Map nodes = (Map) clusters.get(cluster);
            if (nodes == null) {
                nodes = new HashMap();
                clusters.put(cluster, nodes);
            }

            if (label == null) {
                label = name;
            }

            Map nodeParams = new HashMap(1);
            nodes.put(name, new Object[]{type, nodeParams});
            nodeParams.put("label", label);
        }

        public void add_link(String source, String dest, String label) {
            Map attrs = new HashMap();
            attrs.put("label", label);

            links.put(new String[]{x(source), x(dest)}, new Object[]{"link", attrs});
        }

        public String x(String y) {
            String[] cluster = split_cluster(y);
            return cluster[0] + "/" + cluster[1];
        }

        public void attribute(String type, String name, String value) {
            Map params = null;
            if (attributes.get(type) == null) {
                params = new HashMap();
                attributes.put(type, params);
            } else {
                params = (Map) attributes.get(type);
            }

            params.put(name, value);
        }

        public String to_s(boolean include_attributes) {
            StringBuffer result = new StringBuffer();

            result.append("digraph mygraph {\n");
            if (include_attributes) {
                //result << "  rankdir=LR;\n"
                result.append("  fontsize=10;\n");
                result.append("  fontname=helvetica;\n");
                result.append("  node [fontsize=10, fontname=helvetica, style=filled, shape=rectangle]\n");
                result.append("  edge [fontsize=10, fontname=helvetica]\n");
            }

            Map defaultCluster = (Map) clusters.get("default");
            for (Iterator iterator = defaultCluster.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String name = (String) entry.getKey();
                Object[] attributes = (Object[]) entry.getValue();

                result.append("  default_" + c(name) + " ");
                if (include_attributes) {
                    result.append(write_attributes((Map) attributes[1], (String) attributes[0]));
                }
                result.append(";\n");
            }

            for (Iterator iterator = clusters.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String cluster = (String) entry.getKey();
                Map nodes = (Map) entry.getValue();

                if (!"default".equals(cluster)) {
                    result.append("  subgraph cluster_" + c(cluster) + " {\n");
                    if (include_attributes) {
                        result.append("    color=grey;\n");
                        result.append("    fontcolor=grey;\n");
                        result.append("    label=\"" + cluster + "\";\n");
                    }

                    for (Iterator iterator1 = nodes.entrySet().iterator(); iterator1.hasNext();) {
                        Map.Entry entry1 = (Map.Entry) iterator1.next();
                        String name = (String) entry1.getKey();
                        Object[] attributes = (Object[]) entry1.getValue();

                        result.append("    " + c(cluster) + "_" + c(name) + " ");
                        if (include_attributes) {
                            result.append(write_attributes((Map) attributes[1], (String) attributes[0]));
                        }
                        result.append(";\n");
                    }

                    result.append("  }\n");
                }
            }

            for (Iterator iterator = links.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String[] link = (String[]) entry.getKey();
                Object[] attributes = (Object[]) entry.getValue();

                result.append("  " + c(link[0]) + " -> " + c(link[1]) + " ");
                if (include_attributes) {
                    result.append(write_attributes((Map) attributes[1], (String) attributes[0]));
                }
                result.append(";\n");
            }

            result.append("}\n");

            return result.toString();
        }

        String[] split_cluster(String name) {
            String[] cluster = new String[2];

            // name[/\//]
            if (name.startsWith("/")) {
                name = name.substring(1);
            }

            if (name.matches(".*\\/.*")) {
                cluster = name.split("\\/", 2);
            } else {
                cluster[0] = "default";
                cluster[1] = name;
            }

            return cluster;
        }

        String c(String str) { // replace dot unfriendly chars
            return str.replaceAll("[\\.\\/\\-\\$\\{\\}]", "_");
        }

        String write_attributes(Map attributes, String type) {
            StringBuffer result = new StringBuffer();
            result.append('[');

            for (Iterator iterator = attributes.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                if (value != null) {
                    result.append(key + "=\"" + value + "\",");
                }
            }

            Map extra_attributes = (Map) this.attributes.get(type);
            if (extra_attributes != null) {
                for (Iterator iterator = extra_attributes.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String key = (String) entry.getKey();
                    String value = (String) entry.getValue();

                    if (value != null) {
                        result.append(key + "=\"" + value + "\",");
                    }
                }
            }

            result.deleteCharAt(result.length() - 1);
            result.append(']');
            String toString = result.toString();

            if (toString.equals("]")) {
                toString = "";
            }

            return toString;
        }
    }
}