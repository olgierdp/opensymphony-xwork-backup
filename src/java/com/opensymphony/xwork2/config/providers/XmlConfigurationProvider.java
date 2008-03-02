/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.ConfigurationUtil;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.impl.LocatableFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ClassPathFinder;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * Looks in the classpath for an XML file, "xwork.xml" by default,
 * and uses it for the XWork configuration.
 *
 * @author tmjee
 * @author Rainer Hermanns
 * @author Neo
 * @version $Revision$
 */
public class XmlConfigurationProvider implements ConfigurationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(XmlConfigurationProvider.class);

    private List<Document> documents;
    private Set<String> includedFileNames;
    private String configFileName;
    private ObjectFactory objectFactory;

    private Set<String> loadedFileUrls = new HashSet<String>();
    private boolean errorIfMissing;
    private Map<String, String> dtdMappings;
    private Configuration configuration;
    private boolean throwExceptionOnDuplicateBeans = true;
    
    public XmlConfigurationProvider() {
        this("xwork.xml", true);
    }

    public XmlConfigurationProvider(String filename) {
        this(filename, true);
    }

    public XmlConfigurationProvider(String filename, boolean errorIfMissing) {
        this.configFileName = filename;
        this.errorIfMissing = errorIfMissing;

        Map<String, String> mappings = new HashMap<String, String>();
        mappings.put("-//OpenSymphony Group//XWork 2.0//EN", "xwork-2.0.dtd");
        mappings.put("-//OpenSymphony Group//XWork 1.1.1//EN", "xwork-1.1.1.dtd");
        mappings.put("-//OpenSymphony Group//XWork 1.1//EN", "xwork-1.1.dtd");
        mappings.put("-//OpenSymphony Group//XWork 1.0//EN", "xwork-1.0.dtd");
        setDtdMappings(mappings);
    }
    
    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    public void setDtdMappings(Map<String, String> mappings) {
        this.dtdMappings = Collections.unmodifiableMap(mappings);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    /**
     * Returns an unmodifiable map of DTD mappings
     */
    public Map<String, String> getDtdMappings() {
        return dtdMappings;
    }

    public void init(Configuration configuration) {
        this.configuration = configuration;
        this.includedFileNames = configuration.getLoadedFileNames();
        loadDocuments(configFileName);
    }

    public void destroy() {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof XmlConfigurationProvider)) {
            return false;
        }

        final XmlConfigurationProvider xmlConfigurationProvider = (XmlConfigurationProvider) o;

        if ((configFileName != null) ? (!configFileName.equals(xmlConfigurationProvider.configFileName)) : (xmlConfigurationProvider.configFileName != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return ((configFileName != null) ? configFileName.hashCode() : 0);
    }

    private void loadDocuments(String configFileName) {
        try {
            loadedFileUrls.clear();
            documents = loadConfigurationFiles(configFileName, null);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Error loading configuration file " + configFileName, e);
        }
    }

    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        LOG.info("Parsing configuration file [" + configFileName + "]");
        Map<String, Node> loadedBeans = new HashMap<String, Node>();
        for (Document doc : documents) {
            Element rootElement = doc.getDocumentElement();
            NodeList children = rootElement.getChildNodes();
            int childSize = children.getLength();

            for (int i = 0; i < childSize; i++) {
                Node childNode = children.item(i);

                if (childNode instanceof Element) {
                    Element child = (Element) childNode;

                    final String nodeName = child.getNodeName();

                    if (nodeName.equals("bean")) {
                        String type = child.getAttribute("type");
                        String name = child.getAttribute("name");
                        String impl = child.getAttribute("class");
                        String onlyStatic = child.getAttribute("static");
                        String scopeStr = child.getAttribute("scope");
                        boolean optional = "true".equals(child.getAttribute("optional"));
                        Scope scope = Scope.SINGLETON;
                        if ("default".equals(scopeStr)) {
                            scope = Scope.DEFAULT;
                        } else if ("request".equals(scopeStr)) {
                            scope = Scope.REQUEST;
                        } else if ("session".equals(scopeStr)) {
                            scope = Scope.SESSION;
                        } else if ("singleton".equals(scopeStr)) {
                            scope = Scope.SINGLETON;
                        } else if ("thread".equals(scopeStr)) {
                            scope = Scope.THREAD;
                        }

                        if (!TextUtils.stringSet(name)) {
                            name = Container.DEFAULT_NAME;
                        }

                        try {
                            Class cimpl = ClassLoaderUtil.loadClass(impl, getClass());
                            Class ctype = cimpl;
                            if (TextUtils.stringSet(type)) {
                                ctype = ClassLoaderUtil.loadClass(type, getClass());
                            }
                            if ("true".equals(onlyStatic)) {
                                // Force loading of class to detect no class def found exceptions
                                cimpl.getDeclaredClasses();
                                containerBuilder.injectStatics(cimpl);
                            } else {
                                if (containerBuilder.contains(ctype, name)) {
                                    Location loc = LocationUtils.getLocation(loadedBeans.get(ctype.getName() + name));
                                    if (throwExceptionOnDuplicateBeans) {
                                        throw new ConfigurationException("Bean type " + ctype + " with the name " +
                                                name + " has already been loaded by " + loc, child);
                                    } 
                                }

                                // Force loading of class to detect no class def found exceptions
                                cimpl.getDeclaredConstructors();

                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Loaded type:" + type + " name:" + name + " impl:" + impl);
                                }
                                containerBuilder.factory(ctype, name, new LocatableFactory(name, ctype, cimpl, scope, childNode), scope);
                            }
                            loadedBeans.put(ctype.getName() + name, child);
                        } catch (Throwable ex) {
                            if (!optional) {
                                throw new ConfigurationException("Unable to load bean: type:" + type + " class:" + impl, ex, childNode);
                            } else {
                                LOG.debug("Unable to load optional class: " + ex);
                            }
                        }
                    } else if (nodeName.equals("constant")) {
                        String name = child.getAttribute("name");
                        String value = child.getAttribute("value");
                        props.setProperty(name, value, childNode);
                    }
                }
            }
        }
    }

    public void loadPackages() throws ConfigurationException {
        List<Element> reloads = new ArrayList<Element>();
        for (Document doc : documents) {
            Element rootElement = doc.getDocumentElement();
            NodeList children = rootElement.getChildNodes();
            int childSize = children.getLength();

            for (int i = 0; i < childSize; i++) {
                Node childNode = children.item(i);

                if (childNode instanceof Element) {
                    Element child = (Element) childNode;

                    final String nodeName = child.getNodeName();

                    if (nodeName.equals("package")) {
                        PackageConfig cfg = addPackage(child);
                        if (cfg.isNeedsRefresh()) {
                            reloads.add(child);
                        }
                    }
                }
            }
            loadExtraConfiguration(doc);
        }

        if (reloads.size() > 0) {
            reloadRequiredPackages(reloads);
        }

        for (Document doc : documents) {
            loadExtraConfiguration(doc);
        }

        documents.clear();
        configuration = null;
    }

    private void reloadRequiredPackages(List<Element> reloads) {
        if (reloads.size() > 0) {
            List<Element> result = new ArrayList<Element>();
            for (Element pkg : reloads) {
                PackageConfig cfg = addPackage(pkg);
                if (cfg.isNeedsRefresh()) {
                    result.add(pkg);
                }
            }
            if ((result.size() > 0) && (result.size() != reloads.size())) {
                reloadRequiredPackages(result);
                return;
            }

            // Print out error messages for all misconfigured inheritence packages
            if (result.size() > 0) {
                for (Element rp : result) {
                    String parent = rp.getAttribute("extends");
                    if (parent != null) {
                        List parents = ConfigurationUtil.buildParentsFromString(configuration, parent);
                        if (parents != null && parents.size() <= 0) {
                            LOG.error("Unable to find parent packages " + parent);
                        }
                    }
                }
            }
        }
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration. This method should only be called
     * if ConfigurationManager.isReloadingConfigs() is true.
     *
     * @return true if the file has been changed since the last time we read it
     */
    public boolean needsReload() {

        for (String url : loadedFileUrls) {
            if (FileManager.fileNeedsReloading(url)) {
                return true;
            }
        }
        return false;
    }

    protected void addAction(Element actionElement, PackageConfig.Builder packageContext) throws ConfigurationException {
        String name = actionElement.getAttribute("name");
        String className = actionElement.getAttribute("class");
        String methodName = actionElement.getAttribute("method");
        Location location = DomHelper.getLocationObject(actionElement);

        if (location == null) {
            LOG.warn("location null for " + className);
        }
        //methodName should be null if it's not set
        methodName = (methodName.trim().length() > 0) ? methodName.trim() : null;

        // if there isnt a class name specified for an <action/> then try to
        // use the default-class-ref from the <package/>
        if (!TextUtils.stringSet(className)) {
            // if there is a package default-class-ref use that, otherwise use action support
           /* if (TextUtils.stringSet(packageContext.getDefaultClassRef())) {
                className = packageContext.getDefaultClassRef();
            } else {
                className = ActionSupport.class.getName();
            }*/
        
        } else {
        	if (!verifyAction(className, name, location)) {
                return;
            }
        }

        

        Map results;
        try {
            results = buildResults(actionElement, packageContext);
        } catch (ConfigurationException e) {
            throw new ConfigurationException("Error building results for action " + name + " in namespace " + packageContext.getNamespace(), e, actionElement);
        }

        List interceptorList = buildInterceptorList(actionElement, packageContext);

        List exceptionMappings = buildExceptionMappings(actionElement, packageContext);

        ActionConfig actionConfig = new ActionConfig.Builder(packageContext.getName(), name, className)
                .methodName(methodName)
                .addResultConfigs(results)
                .addInterceptors(interceptorList)
                .addExceptionMappings(exceptionMappings)
                .addParams(XmlHelper.getParams(actionElement))
                .location(location)
                .build();
        packageContext.addActionConfig(name, actionConfig);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Loaded " + (TextUtils.stringSet(packageContext.getNamespace()) ? (packageContext.getNamespace() + "/") : "") + name + " in '" + packageContext.getName() + "' package:" + actionConfig);
        }
    }

    protected boolean verifyAction(String className, String name, Location loc) {
        if (className.indexOf('{') > -1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action class [" + className + "] contains a wildcard " +
                        "replacement value, so it can't be verified");
            }
            return true;
        }
        try {
            Class clazz = objectFactory.getClassInstance(className);
            if (objectFactory.isNoArgConstructorRequired()) {
                if (!Modifier.isPublic(clazz.getModifiers())) {
                    throw new ConfigurationException("Action class [" + className + "] is not public", loc);
                }
                clazz.getConstructor(new Class[]{});
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Action class [" + className + "] not found", loc);
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException("Action class [" + className + "] does not have a public no-arg constructor", e, loc);

            // Probably not a big deal, like request or session-scoped Spring 2 beans that need a real request
        } catch (RuntimeException ex) {
            LOG.info("Unable to verify action class [" + className + "] exists at initialization");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Action verification cause", ex);
            }

            // Default to failing fast
        } catch (Exception ex) {
            throw new ConfigurationException(ex, loc);
        }
        return true;
    }

    /**
     * Create a PackageConfig from an XML element representing it.
     */
    protected PackageConfig addPackage(Element packageElement) throws ConfigurationException {
        PackageConfig.Builder newPackage = buildPackageContext(packageElement);

        if (newPackage.isNeedsRefresh()) {
            return newPackage.build();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Loaded " + newPackage);
        }

        // add result types (and default result) to this package
        addResultTypes(newPackage, packageElement);

        // load the interceptors and interceptor stacks for this package
        loadInterceptors(newPackage, packageElement);

        // load the default interceptor reference for this package
        loadDefaultInterceptorRef(newPackage, packageElement);

        // load the default class ref for this package
        loadDefaultClassRef(newPackage, packageElement);

        // load the global result list for this package
        loadGlobalResults(newPackage, packageElement);

        // load the global exception handler list for this package
        loadGobalExceptionMappings(newPackage, packageElement);

        // get actions
        NodeList actionList = packageElement.getElementsByTagName("action");

        for (int i = 0; i < actionList.getLength(); i++) {
            Element actionElement = (Element) actionList.item(i);
            addAction(actionElement, newPackage);
        }

        // load the default action reference for this package
        loadDefaultActionRef(newPackage, packageElement);

        PackageConfig cfg = newPackage.build();
        configuration.addPackageConfig(cfg.getName(), cfg);
        return cfg;
    }

    protected void addResultTypes(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("result-type");

        for (int i = 0; i < resultTypeList.getLength(); i++) {
            Element resultTypeElement = (Element) resultTypeList.item(i);
            String name = resultTypeElement.getAttribute("name");
            String className = resultTypeElement.getAttribute("class");
            String def = resultTypeElement.getAttribute("default");

            Location loc = DomHelper.getLocationObject(resultTypeElement);

            Class clazz = verifyResultType(className, loc);
            if (clazz != null) {
                String paramName = null;
                try {
                    paramName = (String) clazz.getField("DEFAULT_PARAM").get(null);
                }
                catch (Throwable t) {
                    // if we get here, the result type doesn't have a default param defined.
                }
                ResultTypeConfig.Builder resultType = new ResultTypeConfig.Builder(name, className).defaultResultParam(paramName)
                        .location(DomHelper.getLocationObject(resultTypeElement));

                Map params = XmlHelper.getParams(resultTypeElement);

                if (!params.isEmpty()) {
                    resultType.addParams(params);
                }
                packageContext.addResultTypeConfig(resultType.build());

                // set the default result type
                if ("true".equals(def)) {
                    packageContext.defaultResultType(name);
                }
            }
        }
    }

    protected Class verifyResultType(String className, Location loc) {
        try {
            return objectFactory.getClassInstance(className);
        } catch (ClassNotFoundException e) {
            LOG.warn("Result class [" + className + "] doesn't exist (ClassNotFoundException) at " +
                    loc.toString() + ", ignoring", e);
        } catch (NoClassDefFoundError e) {
            LOG.warn("Result class [" + className + "] doesn't exist (NoClassDefFoundError) at " +
                    loc.toString() + ", ignoring", e);
        }

        return null;
    }

    protected List buildInterceptorList(Element element, PackageConfig.Builder context) throws ConfigurationException {
        List interceptorList = new ArrayList();
        NodeList interceptorRefList = element.getElementsByTagName("interceptor-ref");

        for (int i = 0; i < interceptorRefList.getLength(); i++) {
            Element interceptorRefElement = (Element) interceptorRefList.item(i);

            if (interceptorRefElement.getParentNode().equals(element) || interceptorRefElement.getParentNode().getNodeName().equals(element.getNodeName())) {
                List interceptors = lookupInterceptorReference(context, interceptorRefElement);
                interceptorList.addAll(interceptors);
            }
        }

        return interceptorList;
    }

    /**
     * This method builds a package context by looking for the parents of this new package.
     * <p/>
     * If no parents are found, it will return a root package.
     */
    protected PackageConfig.Builder buildPackageContext(Element packageElement) {
        String parent = packageElement.getAttribute("extends");
        String abstractVal = packageElement.getAttribute("abstract");
        boolean isAbstract = Boolean.valueOf(abstractVal).booleanValue();
        String name = TextUtils.noNull(packageElement.getAttribute("name"));
        String namespace = TextUtils.noNull(packageElement.getAttribute("namespace"));


        if (TextUtils.stringSet(packageElement.getAttribute("externalReferenceResolver"))) {
            throw new ConfigurationException("The 'externalReferenceResolver' attribute has been removed.  Please use " +
                    "a custom ObjectFactory or Interceptor.", packageElement);
        }

        PackageConfig.Builder cfg = new PackageConfig.Builder(name)
                .namespace(namespace)
                .isAbstract(isAbstract)
                .location(DomHelper.getLocationObject(packageElement));


        if (TextUtils.stringSet(TextUtils.noNull(parent))) { // has parents, let's look it up

            List parents = ConfigurationUtil.buildParentsFromString(configuration, parent);

            if (parents.size() <= 0) {
                cfg.needsRefresh(true);
            } else {
                cfg.addParents(parents);
            }
        }

        return cfg;
    }

    /**
     * Build a map of ResultConfig objects from below a given XML element.
     */
    protected Map buildResults(Element element, PackageConfig.Builder packageContext) {
        NodeList resultEls = element.getElementsByTagName("result");

        Map results = new LinkedHashMap();

        for (int i = 0; i < resultEls.getLength(); i++) {
            Element resultElement = (Element) resultEls.item(i);

            if (resultElement.getParentNode().equals(element) || resultElement.getParentNode().getNodeName().equals(element.getNodeName())) {
                String resultName = resultElement.getAttribute("name");
                String resultType = resultElement.getAttribute("type");

                // if you don't specify a name on <result/>, it defaults to "success"
                if (!TextUtils.stringSet(resultName)) {
                    resultName = Action.SUCCESS;
                }

                // there is no result type, so let's inherit from the parent package
                if (!TextUtils.stringSet(resultType)) {
                    resultType = packageContext.getFullDefaultResultType();

                    // now check if there is a result type now
                    if (!TextUtils.stringSet(resultType)) {
                        // uh-oh, we have a problem
                        throw new ConfigurationException("No result type specified for result named '"
                                + resultName + "', perhaps the parent package does not specify the result type?", resultElement);
                    }
                }


                ResultTypeConfig config = packageContext.getResultType(resultType);

                if (config == null) {
                    throw new ConfigurationException("There is no result type defined for type '" + resultType + "' mapped with name '" + resultName + "'", resultElement);
                }

                String resultClass = config.getClazz();

                // invalid result type specified in result definition
                if (resultClass == null) {
                    throw new ConfigurationException("Result type '" + resultType + "' is invalid");
                }

                Map<String, String> resultParams = XmlHelper.getParams(resultElement);

                if (resultParams.size() == 0) // maybe we just have a body - therefore a default parameter
                {
                    // if <result ...>something</result> then we add a parameter of 'something' as this is the most used result param
                    if (resultElement.getChildNodes().getLength() >= 1) {
                        resultParams = new LinkedHashMap();

                        String paramName = config.getDefaultResultParam();
                        if (paramName != null) {
                            StringBuffer paramValue = new StringBuffer();
                            for (int j = 0; j < resultElement.getChildNodes().getLength(); j++) {
                                if (resultElement.getChildNodes().item(j).getNodeType() == Node.TEXT_NODE) {
                                    String val = resultElement.getChildNodes().item(j).getNodeValue();
                                    if (val != null) {
                                        paramValue.append(val);
                                    }
                                }
                            }
                            String val = paramValue.toString().trim();
                            if (val.length() > 0) {
                                resultParams.put(paramName, val);
                            }
                        } else {
                            LOG.warn("no default parameter defined for result of type " + config.getName());
                        }
                    }
                }

                // create new param map, so that the result param can override the config param
                Map params = new LinkedHashMap();
                Map configParams = config.getParams();
                if (configParams != null) {
                    params.putAll(configParams);
                }
                params.putAll(resultParams);

                ResultConfig resultConfig = new ResultConfig.Builder(resultName, resultClass)
                    .addParams(params)
                    .location(DomHelper.getLocationObject(element))
                    .build();
                results.put(resultConfig.getName(), resultConfig);
            }
        }

        return results;
    }

    /**
     * Build a map of ResultConfig objects from below a given XML element.
     */
    protected List buildExceptionMappings(Element element, PackageConfig.Builder packageContext) {
        NodeList exceptionMappingEls = element.getElementsByTagName("exception-mapping");

        List exceptionMappings = new ArrayList();

        for (int i = 0; i < exceptionMappingEls.getLength(); i++) {
            Element ehElement = (Element) exceptionMappingEls.item(i);

            if (ehElement.getParentNode().equals(element) || ehElement.getParentNode().getNodeName().equals(element.getNodeName())) {
                String emName = ehElement.getAttribute("name");
                String exceptionClassName = ehElement.getAttribute("exception");
                String exceptionResult = ehElement.getAttribute("result");

                Map params = XmlHelper.getParams(ehElement);

                if (!TextUtils.stringSet(emName)) {
                    emName = exceptionResult;
                }

                ExceptionMappingConfig ehConfig = new ExceptionMappingConfig.Builder(emName, exceptionClassName, exceptionResult)
                    .addParams(params)
                    .location(DomHelper.getLocationObject(ehElement))
                    .build();
                exceptionMappings.add(ehConfig);
            }
        }

        return exceptionMappings;
    }


    protected void loadDefaultInterceptorRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-interceptor-ref");

        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element) resultTypeList.item(0);
            packageContext.defaultInterceptorRef(defaultRefElement.getAttribute("name"));
        }
    }

    protected void loadDefaultActionRef(PackageConfig.Builder packageContext, Element element) {
        NodeList resultTypeList = element.getElementsByTagName("default-action-ref");

        if (resultTypeList.getLength() > 0) {
            Element defaultRefElement = (Element) resultTypeList.item(0);
            packageContext.defaultActionRef(defaultRefElement.getAttribute("name"));
        }
    }

    /**
     * Load all of the global results for this package from the XML element.
     */
    protected void loadGlobalResults(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalResultList = packageElement.getElementsByTagName("global-results");

        if (globalResultList.getLength() > 0) {
            Element globalResultElement = (Element) globalResultList.item(0);
            Map results = buildResults(globalResultElement, packageContext);
            packageContext.addGlobalResultConfigs(results);
        }
    }

    protected void loadDefaultClassRef(PackageConfig.Builder packageContext, Element element) {
        NodeList defaultClassRefList = element.getElementsByTagName("default-class-ref");
        if (defaultClassRefList.getLength() > 0) {
            Element defaultClassRefElement = (Element) defaultClassRefList.item(0);
            packageContext.defaultClassRef(defaultClassRefElement.getAttribute("class"));
        }
    }

    /**
     * Load all of the global results for this package from the XML element.
     */
    protected void loadGobalExceptionMappings(PackageConfig.Builder packageContext, Element packageElement) {
        NodeList globalExceptionMappingList = packageElement.getElementsByTagName("global-exception-mappings");

        if (globalExceptionMappingList.getLength() > 0) {
            Element globalExceptionMappingElement = (Element) globalExceptionMappingList.item(0);
            List exceptionMappings = buildExceptionMappings(globalExceptionMappingElement, packageContext);
            packageContext.addGlobalExceptionMappingConfigs(exceptionMappings);
        }
    }

    //    protected void loadIncludes(Element rootElement, DocumentBuilder db) throws Exception {
    //        NodeList includeList = rootElement.getElementsByTagName("include");
    //
    //        for (int i = 0; i < includeList.getLength(); i++) {
    //            Element includeElement = (Element) includeList.item(i);
    //            String fileName = includeElement.getAttribute("file");
    //            includedFileNames.add(fileName);
    //            loadConfigurationFile(fileName, db);
    //        }
    //    }
    protected InterceptorStackConfig loadInterceptorStack(Element element, PackageConfig.Builder context) throws ConfigurationException {
        String name = element.getAttribute("name");

        InterceptorStackConfig.Builder config = new InterceptorStackConfig.Builder(name)
                .location(DomHelper.getLocationObject(element));
        NodeList interceptorRefList = element.getElementsByTagName("interceptor-ref");

        for (int j = 0; j < interceptorRefList.getLength(); j++) {
            Element interceptorRefElement = (Element) interceptorRefList.item(j);
            List interceptors = lookupInterceptorReference(context, interceptorRefElement);
            config.addInterceptors(interceptors);
        }

        return config.build();
    }

    protected void loadInterceptorStacks(Element element, PackageConfig.Builder context) throws ConfigurationException {
        NodeList interceptorStackList = element.getElementsByTagName("interceptor-stack");

        for (int i = 0; i < interceptorStackList.getLength(); i++) {
            Element interceptorStackElement = (Element) interceptorStackList.item(i);

            InterceptorStackConfig config = loadInterceptorStack(interceptorStackElement, context);

            context.addInterceptorStackConfig(config);
        }
    }

    protected void loadInterceptors(PackageConfig.Builder context, Element element) throws ConfigurationException {
        NodeList interceptorList = element.getElementsByTagName("interceptor");

        for (int i = 0; i < interceptorList.getLength(); i++) {
            Element interceptorElement = (Element) interceptorList.item(i);
            String name = interceptorElement.getAttribute("name");
            String className = interceptorElement.getAttribute("class");

            Map params = XmlHelper.getParams(interceptorElement);
            InterceptorConfig config = new InterceptorConfig.Builder(name, className)
                .addParams(params)
                .location(DomHelper.getLocationObject(interceptorElement))
                .build();

            context.addInterceptorConfig(config);
        }

        loadInterceptorStacks(element, context);
    }

    //    protected void loadPackages(Element rootElement) throws ConfigurationException {
    //        NodeList packageList = rootElement.getElementsByTagName("package");
    //
    //        for (int i = 0; i < packageList.getLength(); i++) {
    //            Element packageElement = (Element) packageList.item(i);
    //            addPackage(packageElement);
    //        }
    //    }
    private List loadConfigurationFiles(String fileName, Element includeElement) {
        List<Document> docs = new ArrayList<Document>();
        if (!includedFileNames.contains(fileName)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Loading action configurations from: " + fileName);
            }

            includedFileNames.add(fileName);

            Iterator<URL> urls = null;
            Document doc = null;
            InputStream is = null;

            IOException ioException = null;
            try {
                urls = getConfigurationUrls(fileName);
            } catch (IOException ex) {
                ioException = ex;
            }

            if (urls == null || !urls.hasNext()) {
                if (errorIfMissing) {
                    throw new ConfigurationException("Could not open files of the name " + fileName, ioException);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Unable to locate configuration files of the name "
                                + fileName + ", skipping");
                    }
                    return docs;
                }
            }

            URL url = null;
            while (urls.hasNext()) {
                try {
                    url = urls.next();
                    is = FileManager.loadFile(url);

                    InputSource in = new InputSource(is);

                    in.setSystemId(url.toString());

                    doc = DomHelper.parse(in, dtdMappings);
                } catch (XWorkException e) {
                    if (includeElement != null) {
                        throw new ConfigurationException("Unable to load "+url, e, includeElement);
                    } else {
                        throw new ConfigurationException("Unable to load "+url, e);
                    }
                } catch (Exception e) {
                    final String s = "Caught exception while loading file " + fileName;
                    throw new ConfigurationException(s, e, includeElement);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            LOG.error("Unable to close input stream", e);
                        }
                    }
                }

                Element rootElement = doc.getDocumentElement();
                NodeList children = rootElement.getChildNodes();
                int childSize = children.getLength();

                for (int i = 0; i < childSize; i++) {
                    Node childNode = children.item(i);

                    if (childNode instanceof Element) {
                        Element child = (Element) childNode;

                        final String nodeName = child.getNodeName();

                        if (nodeName.equals("include")) {
                            String includeFileName = child.getAttribute("file");
                            if(includeFileName.indexOf('*') != -1 ) {
                                // handleWildCardIncludes(includeFileName, docs, child);
                            	ClassPathFinder wildcardFinder = new ClassPathFinder();
                            	wildcardFinder.setPattern(includeFileName);
                            	Vector<String> wildcardMatches = wildcardFinder.findMatches();
                            	for (String match : wildcardMatches) {
                            		docs.addAll(loadConfigurationFiles(match, child));
                            	}
                            }
                            else {
                            	
                                docs.addAll(loadConfigurationFiles(includeFileName, child));    
                            }    
                    }
                }
                }
                docs.add(doc);
                loadedFileUrls.add(url.toString());
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Loaded action configuration from: " + fileName);
            }
        }
        return docs;
    }

    private void handleWildCardIncludes(String includeFileName, List<Document> docs, Element child) {
        // check for star*, pedantic
        if( includeFileName.indexOf('*') == -1 ) {
            throw new XWorkException("handleWildCardIncludes called with name not containing wildcard");
        }
        LOG.info("encountered wildcard include, checking for - " + includeFileName);
        URL [] curDirUrls ;

           // curDirUrls = ClassLoaderUtil.getResources("/", this.getClass(), true);
           ClassLoader cl = XmlConfigurationProvider.class.getClassLoader();
           URLClassLoader ucl ;
           if (cl instanceof URLClassLoader) {
               ucl = (URLClassLoader) cl;
           }
           else {
               throw new XWorkException("cannot create an URLClassLoader from current classloader");
           }
           curDirUrls = ucl.getURLs();

        String fileNamePrefix = null;
        if (! includeFileName.startsWith("*")) {
            fileNamePrefix = includeFileName.substring(0, includeFileName.indexOf('*'));    
        }
        
        String fileNameSuffix = null;
        if( ! includeFileName.endsWith("*")) {
            fileNameSuffix = includeFileName.substring(includeFileName.lastIndexOf('*') +1 );
        }
        
        String relativeDir = null;
        
        if(includeFileName.indexOf("/") != -1) {
            if(LOG.isDebugEnabled() ) {
                LOG.debug("includeFileName contains a /");
            }
            if(includeFileName.lastIndexOf('/') > includeFileName.indexOf('*')) {
                throw new XWorkException("wildcard includes does not support wildcard directories");
            }
            fileNamePrefix = includeFileName.substring(includeFileName.lastIndexOf('/') +1, includeFileName.indexOf('*'));
            relativeDir = includeFileName.substring(0,includeFileName.lastIndexOf('/'));
            if(LOG.isDebugEnabled() ) {
                LOG.debug("relativeDir = " + relativeDir + ", fileNameMask = " + fileNamePrefix);
            }
        }
        
        for(URL baseSearchURL : curDirUrls ) {
            if (! baseSearchURL.getProtocol().equals("file")) {
                continue;
            }
            
            File searchDir ;
            
            if (relativeDir != null ) {
                if (relativeDir.startsWith("/")) {
                    relativeDir = relativeDir.substring(relativeDir.indexOf('/') + 1);
                }
                File parent ;
                try {
                    parent = new File(baseSearchURL.toURI());
                } catch (URISyntaxException e) {
                    throw new XWorkException("bad URI for searchDir - " + baseSearchURL.toString());
                }
                if( ! parent.isDirectory()) {
                    continue;
                }
                searchDir = new File(parent, relativeDir);
            }
            else {
                try {
                    searchDir = new File(baseSearchURL.toURI());
                } catch (URISyntaxException e) {
                    throw new XWorkException("bad URI for searchDir - " + baseSearchURL.toString());
                }
            }
            if(LOG.isDebugEnabled() ) {
                LOG.debug("using - " + searchDir.toURI().toString() + ", as searchDir");
            }
            if( searchDir != null && searchDir.isDirectory() ) {
                if(LOG.isDebugEnabled() ) {
                    LOG.debug("getting searchDir file list");
                }
                String [] filesInDir = searchDir.list();
                if (filesInDir == null ) {
                    throw new XWorkException("unable to find any files in include directory");
                }
                for (String fileInDir: filesInDir) {
                    if(LOG.isDebugEnabled() ) {
                        LOG.debug("checking - " + fileInDir);
                    }
                    boolean fileMatches = false ;
                    if (fileNameSuffix != null || fileNamePrefix != null) {
                        fileMatches = ( fileNameSuffix != null && fileNamePrefix != null &&
                                        fileNameSuffix.length() + fileNamePrefix.length() < fileInDir.length() && 
                                        fileInDir.startsWith(fileNamePrefix) && fileInDir.endsWith(fileNameSuffix ) ) ;
                        fileMatches = fileMatches || 
                                    ( fileNamePrefix == null &&
                                      fileInDir.endsWith(fileNameSuffix) );
                        fileMatches = fileMatches || 
                                    ( fileNameSuffix == null &&
                                      fileInDir.startsWith(fileNamePrefix) );
                    }
                    
                    if( fileMatches ) {
                        if (relativeDir != null ) {
                            if (!relativeDir.endsWith("/")) {
                                relativeDir = relativeDir.concat("/");
                            }
                            if(LOG.isDebugEnabled() ) {
                                LOG.debug("calling load on - " + relativeDir + fileInDir);
                            }
                            docs.addAll(loadConfigurationFiles(relativeDir + fileInDir, child));
                        }
                        else {
                            if(LOG.isDebugEnabled() ) {
                                LOG.debug("calling load on - " + fileInDir);
                            }
                            docs.addAll(loadConfigurationFiles(fileInDir, child));
                        }
                    }
                }
            }
        }
    }
    
    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        return ClassLoaderUtil.getResources(fileName, XmlConfigurationProvider.class, false);
    }

    /**
     * Allows subclasses to load extra information from the document
     *
     * @param doc The configuration document
     */
    protected void loadExtraConfiguration(Document doc) {
        // no op
    }

    /**
     * Looks up the Interceptor Class from the interceptor-ref name and creates an instance, which is added to the
     * provided List, or, if this is a ref to a stack, it adds the Interceptor instances from the List to this stack.
     *
     * @param interceptorRefElement Element to pull interceptor ref data from
     * @param context               The PackageConfig to lookup the interceptor from
     * @return A list of Interceptor objects
     */
    private List lookupInterceptorReference(PackageConfig.Builder context, Element interceptorRefElement) throws ConfigurationException {
        String refName = interceptorRefElement.getAttribute("name");
        Map refParams = XmlHelper.getParams(interceptorRefElement);

        Location loc = LocationUtils.getLocation(interceptorRefElement);
        return InterceptorBuilder.constructInterceptorReference(context, refName, refParams, loc, objectFactory);
    }

}
