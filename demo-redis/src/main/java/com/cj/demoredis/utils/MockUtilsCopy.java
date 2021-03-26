package com.cj.demoredis.utils;

import com.forte.util.exception.MockException;
import com.forte.util.factory.MockMapperFactory;
import com.forte.util.factory.MockObjectFactory;
import com.forte.util.factory.MockProxyHandlerFactory;
import com.forte.util.factory.MockProxyHandlerFactoryImpl;
import com.forte.util.loader.DefaultMockMethodLoader;
import com.forte.util.loader.MethodLoader;
import com.forte.util.mockbean.*;
import com.forte.util.parser.ParameterParser;
import com.forte.util.utils.ClassScanner;
import com.forte.util.utils.MockUtil;
import com.forte.util.utils.ProxyUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MockUtilsCopy {
    private static final Map<Class, MockNormalObject> MOCK_OBJECT = new ConcurrentHashMap(4);
    private static final Map<String, MockMapObject> MOCK_MAP = new ConcurrentHashMap(4);
    private static final Map<String, Method> MOCK_METHOD;

    public MockUtilsCopy() {
    }

    public static <T> MockBean<T> setResult(Class<T> objClass, Map<String, Object> map, boolean reset) {
        if (!reset && MOCK_OBJECT.get(objClass) != null) {
            throw new MockException("此映射已存在！");
        } else {
            MockBean<T> parser = ParameterParser.parser(objClass, map);
            MOCK_OBJECT.put(objClass, new MockNormalObject(parser));
            System.gc();
            return parser;
        }
    }

    public static void clearMock() {
        if (MOCK_OBJECT.size() > 5000) {
            MOCK_OBJECT.clear();
        }
    }


    /**
     * mapToBean
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return obj;
    }

    public static Map<?, ?> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(obj);
    }



    public static MockMapBean setResult(String resultName, Map<String, Object> map, boolean reset) {
        if (!reset && MOCK_MAP.get(resultName) != null) {
            throw new MockException("此映射已存在！this mock result has already exists.");
        } else {
            MockMapBean parser = ParameterParser.parser(map);
            MOCK_MAP.put(resultName, MockObjectFactory.createMapObj(parser));
            System.gc();
            return parser;
        }
    }

    public static <T> void set(Class<T> objClass, Map<String, Object> map) {
        setResult(objClass, map, false);
    }

    public static <T> void set(Class<T> objClass) {
        Map<String, Object> mapper = MockMapperFactory.getMapper(objClass);
        setResult(objClass, mapper, false);
    }

    public static <T> void setWithOther(Class<T> objClass, Map<String, Object> other) {
        Map<String, Object> mapper = MockMapperFactory.getMapper(objClass, other);
        setResult(objClass, mapper, false);
    }

    public static void set(String resultName, Map<String, Object> map) {
        setResult(resultName, map, false);
    }

    public static <T> void reset(Class<T> objClass, Map<String, Object> map) {
        setResult(objClass, map, true);
    }

    public static <T> void reset(Class<T> objClass) {
        Map<String, Object> mapper = MockMapperFactory.getMapper(objClass);
        setResult(objClass, mapper, true);
    }

    public static <T> void resetWithOther(Class<T> objClass, Map<String, Object> other) {
        Map<String, Object> mapper = MockMapperFactory.getMapper(objClass, other);
        setResult(objClass, mapper, true);
    }

    public static <T> void reset(String resultName, Map<String, Object> map) {
        setResult(resultName, map, true);
    }

    public static <T> MockObject<T> get(Class<T> objClass) {
        return (MockObject) Optional.ofNullable(MOCK_OBJECT.get(objClass)).orElse(null);
    }

    public static <T> MockObject<Map> get(String resultName) {
        return (MockObject) Optional.ofNullable(MOCK_MAP.get(resultName)).orElse(null);
    }

    public static Set<Class<?>> scan(ClassLoader classLoader, Function<Class<?>, Map<String, Object>> withOther, boolean reset, String... packages) throws Exception {
        if (packages.length == 0) {
            return new HashSet();
        } else {
            ClassScanner scanner = classLoader == null ? new ClassScanner() : new ClassScanner(classLoader);
            String[] var5 = packages;
            int var6 = packages.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String p = var5[var7];
                scanner.find(p, (c) -> {
                    return c.getAnnotation(com.forte.util.mapper.MockBean.class) != null;
                });
            }

            Set<Class<?>> classes = scanner.get();
            classes.forEach((c) -> {
                if (withOther != null) {
                    if (reset) {
                        resetWithOther(c, (Map) withOther.apply(c));
                    } else {
                        setWithOther(c, (Map) withOther.apply(c));
                    }
                } else if (reset) {
                    reset(c);
                } else {
                    set(c);
                }

            });
            return classes;
        }
    }

    public static Set<Class<?>> scan(Function<Class<?>, Map<String, Object>> withOther, boolean reset, String... packages) throws Exception {
        return scan((ClassLoader) null, withOther, reset, packages);
    }

    public static Set<Class<?>> scan(boolean reset, String... packages) throws Exception {
        return scan((ClassLoader) null, (Function) null, reset, packages);
    }

    public static Set<Class<?>> scan(String... packages) throws Exception {
        return scan((ClassLoader) null, (Function) null, false, packages);
    }

    public static <T> T proxy(Class<T> type, MockProxyHandlerFactory factory) {
        if (!Modifier.isInterface(type.getModifiers())) {
            throw new IllegalArgumentException("type [" + type + "] is not a interface type.");
        } else {
            InvocationHandler proxyHandler = factory.getHandler((returnType, name) -> {
                MockObject<?> mockObject = null;
                if (name != null) {
                    mockObject = get(name);
                }

                if (mockObject == null) {
                    mockObject = get(returnType);
                }

                return mockObject;
            });
            return ProxyUtils.proxy(type, proxyHandler);
        }
    }

    public static <T, C extends Class<T>> T proxy(C type) {
        return proxy(type, new MockProxyHandlerFactoryImpl());
    }

    public static MethodLoader mockMethodLoader() {
        return new DefaultMockMethodLoader(MOCK_METHOD);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Map<String, Method> _getMockMethod() {
        return getMockMethods();
    }

    public static Map<String, Method> getMockMethods() {
        return new HashMap(MOCK_METHOD);
    }

    static {
        Class<MockUtil> mockUtilClass = MockUtil.class;
        Method[] methods = mockUtilClass.getMethods();
        Map<String, Method> mockUtilMethods = (Map) Arrays.stream(methods).filter((m) -> {
            return Arrays.stream(Object.class.getMethods()).noneMatch((om) -> {
                return om.equals(m);
            });
        }).flatMap((m) -> {
            Map<String, Method> methodMap = new HashMap();
            String key = m.getName() + "(" + (String) Arrays.stream(m.getParameterTypes()).map(Class::getName).collect(Collectors.joining(",")) + ")";
            methodMap.put(key, m);
            return methodMap.entrySet().stream();
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        MOCK_METHOD = mockUtilMethods;
    }
}
