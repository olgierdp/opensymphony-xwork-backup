/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.parameters.bytecode;

import java.util.*;
import java.lang.reflect.Constructor;

import org.objectweb.asm.*;
import org.apache.commons.lang.StringUtils;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.inject.Inject;

public class AccessorBytecodeUtil implements Opcodes {
    private static int counter;

    final Map<Key, Setter> settersCache = new HashMap<Key, Setter>();
    final Map<Key, Getter> gettersCache = new HashMap<Key, Getter>();

    protected final String SETTER_INTERFACE = toPathName(Setter.class.getName());
    protected final String GETTER_INTERFACE = toPathName(Getter.class.getName());

    protected final AccessorsClassLoader classLoader = new AccessorsClassLoader(AccessorBytecodeUtil.class.getClassLoader());

    protected ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public Getter getGetter(Class targetType, String propertyName) throws Exception {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        String targetClassName = toPathName(targetType.getName());
        String methodName = "get" + StringUtils.capitalize(propertyName);

        //cache key
        Key key = new Key();
        key.targetClassName = targetClassName;
        key.methodName = methodName;

        //get from cache
        if (gettersCache.containsKey(key))
            return gettersCache.get(key);

        //use reflection to determine the return type of the getter, this will happen only once by accessor
        Class returnType = reflectionProvider.getGetMethod(targetType, propertyName).getReturnType();

        int postfix = counter++;
        String className = targetClassName + postfix;

        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", new String[]{GETTER_INTERFACE});

        {
            fv = cw.visitField(ACC_PRIVATE, "propertyName", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, className, "propertyName", "Ljava/lang/String;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, targetClassName);
            mv.visitMethodInsn(INVOKEVIRTUAL, targetClassName, methodName, "()" +  Type.getDescriptor(returnType));
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getPropertyName", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "propertyName", "Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        //this one needs "." instead of "/" in the name
        String finalClassName = targetType.getName() + postfix;

        Class clazz = classLoader.defineClass(finalClassName, cw.toByteArray());
        Constructor constructor = clazz.getConstructor(new Class[]{String.class});
        Getter getter = (Getter) constructor.newInstance(new Object[]{propertyName});
        gettersCache.put(key, getter);
        return getter;
    }

    public Setter getSetter(Class targetType, Class valueType, String propertyName) throws Exception {
        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        String targetClassName = toPathName(targetType.getName());
        String valueClassName = toPathName(valueType.getName());
        String methodName = "set" + StringUtils.capitalize(propertyName);

        //cache key
        Key key = new Key();
        key.targetClassName = targetClassName;
        key.valueClassName = valueClassName;
        key.methodName = methodName;

        //get from cache
        if (settersCache.containsKey(key))
            return settersCache.get(key);

        int postfix = counter++;
        String className = targetClassName + postfix;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", new String[]{SETTER_INTERFACE});

        {
            fv = cw.visitField(ACC_PRIVATE, "propertyClass", "Ljava/lang/Class;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE, "propertyName", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;Ljava/lang/Class;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, className, "propertyName", "Ljava/lang/String;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitFieldInsn(PUTFIELD, className, "propertyClass", "Ljava/lang/Class;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, targetClassName);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, valueClassName);
            mv.visitMethodInsn(INVOKEVIRTUAL, targetClassName, methodName, "(" + Type.getDescriptor(valueType) + ")V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getPropertyName", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "propertyName", "Ljava/lang/String;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getPropertyClass", "()Ljava/lang/Class;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "propertyClass", "Ljava/lang/Class;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        //this one needs "." instead of "/" in the name
        String finalClassName = targetType.getName() + postfix;

        Class clazz = classLoader.defineClass(finalClassName, cw.toByteArray());
        Constructor constructor = clazz.getConstructor(new Class[]{String.class, Class.class});
        Setter setter = (Setter) constructor.newInstance(new Object[]{propertyName, valueType});
        settersCache.put(key, setter);
        return setter;
    }

    private String toPathName(String name) {
        return name.replace('.', '/');
    }
}

class AccessorsClassLoader extends ClassLoader {
    AccessorsClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}

class Key {
    String targetClassName;
    String valueClassName;
    String methodName;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key setterKey = (Key) o;

        if (methodName != null ? !methodName.equals(setterKey.methodName) : setterKey.methodName != null) return false;
        if (targetClassName != null ? !targetClassName.equals(setterKey.targetClassName) : setterKey.targetClassName != null)
            return false;
        if (valueClassName != null ? !valueClassName.equals(setterKey.valueClassName) : setterKey.valueClassName != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = targetClassName != null ? targetClassName.hashCode() : 0;
        result = 31 * result + (valueClassName != null ? valueClassName.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }
}
