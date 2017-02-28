/*
 * HPE SNAP 2015
 */
package com.hp.snap.evaluation.imdb.business.cases.couchbase.cluster;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

/**
 * @author 
 */
public final class DomainObjectUtils {
    private DomainObjectUtils() {
    }

    private static final String mbeanName = ManagementFactory.getRuntimeMXBean().getName();

    public static String generateSessionID(String id) {
        return new StringBuilder().append(mbeanName).append(";").append(id).toString();
    }

    /*
    public static String toStringDomainObject(Object domainObject) {
        Class<?>[] clazzs = domainObject.getClass().getInterfaces();
        if (null != clazzs && clazzs.length > 0) {
            Class<?> domainInterface = null;
            loop: for (Class<?> clazz : clazzs) {
                Annotation[] annotations = clazz.getDeclaredAnnotations();
                if (null != annotations && annotations.length > 0) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof PersistenceCapable) {
                            domainInterface = clazz;
                            break loop;
                        }
                    }
                }
            }

            if (null != domainInterface) {
                Method[] methods = domainInterface.getMethods();
                return toStringDomainObject(domainObject, domainInterface, methods);
            }
        }

        return "unknown domain object";
    }
*/
    public static String toStringDomainObject(Object domainObject, Class<?> domainInterface) {
        Method[] methods = domainInterface.getMethods();
        return toStringDomainObject(domainObject, domainInterface, methods);
    }

    public static String toStringDomainObject(Object domainObject, Class<?> domainInterface, Method[] methods) {
        StringBuilder sb = new StringBuilder();

        sb.append(domainInterface.getSimpleName()).append("[");
        for (Method method : methods) {
            String name = method.getName();
            boolean isGet = name.startsWith("get");
            boolean isIs = name.startsWith("is");
            if (isGet || isIs) {
                try {
                    Object value = method.invoke(domainObject);
                    String field = name;
                    if (isGet) {
                        field = name.substring(3);
                    } else if (isIs) {
                        field = name.substring(2);
                    }
                    sb.append(field).append("=").append(value).append(", ");
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
