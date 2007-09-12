package com.opensymphony.xwork2.config.providers;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.DefaultTextProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.reflection.ReflectionContextFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import ognl.MethodAccessor;
import ognl.PropertyAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.AnnotationActionValidatorManager;
import com.opensymphony.xwork2.validator.DefaultActionValidatorManager;
import com.opensymphony.xwork2.ognl.ObjectProxy;
import com.opensymphony.xwork2.ognl.OgnlReflectionContextFactory;
import com.opensymphony.xwork2.ognl.OgnlReflectionProvider;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectAccessor;
import com.opensymphony.xwork2.ognl.accessor.ObjectProxyPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkEnumerationAcccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkIteratorPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkListPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMapPropertyAccessor;
import com.opensymphony.xwork2.ognl.accessor.XWorkMethodAccessor;

public class XWorkConfigurationProvider implements ConfigurationProvider {

    public void destroy() {
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public void loadPackages() throws ConfigurationException {
    }

    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {

        builder.factory(com.opensymphony.xwork2.ObjectFactory.class)
               .factory(ActionProxyFactory.class, DefaultActionProxyFactory.class)
               .factory(ObjectTypeDeterminer.class, DefaultObjectTypeDeterminer.class)
               .factory(XWorkConverter.class)
               .factory(ValueStackFactory.class, OgnlValueStackFactory.class)
               .factory(ReflectionProvider.class, OgnlReflectionProvider.class)
               .factory(ReflectionContextFactory.class, OgnlReflectionContextFactory.class)
               .factory(PropertyAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class)
               .factory(PropertyAccessor.class, Object.class.getName(), ObjectAccessor.class)
               .factory(PropertyAccessor.class, Iterator.class.getName(), XWorkIteratorPropertyAccessor.class)
               .factory(PropertyAccessor.class, Enumeration.class.getName(), XWorkEnumerationAcccessor.class)
               .factory(PropertyAccessor.class, List.class.getName(), XWorkListPropertyAccessor.class)
               .factory(PropertyAccessor.class, Map.class.getName(), XWorkMapPropertyAccessor.class)
               .factory(PropertyAccessor.class, Collection.class.getName(), XWorkCollectionPropertyAccessor.class)
               .factory(PropertyAccessor.class, Set.class.getName(), XWorkCollectionPropertyAccessor.class)
               .factory(PropertyAccessor.class, ObjectProxy.class.getName(), ObjectProxyPropertyAccessor.class)
               .factory(MethodAccessor.class, Object.class.getName(), XWorkMethodAccessor.class)
               .factory(MethodAccessor.class, CompoundRoot.class.getName(), CompoundRootAccessor.class)
               .factory(NullHandler.class, Object.class.getName(), InstantiatingNullHandler.class)
               .factory(ActionValidatorManager.class, AnnotationActionValidatorManager.class)
               .factory(ActionValidatorManager.class, "no-annotations", DefaultActionValidatorManager.class)
               .factory(TextProvider.class, DefaultTextProvider.class)
               .constant("devMode", Boolean.FALSE.toString());
    }

}
