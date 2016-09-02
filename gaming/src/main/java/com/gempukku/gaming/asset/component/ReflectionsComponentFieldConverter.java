package com.gempukku.gaming.asset.component;

import com.gempukku.gaming.asset.JavaPackageProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RegisterSystem(
        shared = ComponentFieldConverter.class)
public class ReflectionsComponentFieldConverter implements ComponentFieldConverter, LifeCycleSystem {
    @Inject
    private JavaPackageProvider javaPackageProvider;

    private Map<Class<?>, ComponentFieldTypeConverter> converterMap;

    public void init() {
        if (converterMap == null) {
            converterMap = new HashMap<>();
            Set<URL> contextLocations = new HashSet<>();
            for (String javaPackage : javaPackageProvider.getJavaPackages()) {
                contextLocations.addAll(ClasspathHelper.forPackage(javaPackage, ClasspathHelper.contextClassLoader()));
            }

            Configuration scanConfiguration = new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner())
                    .setUrls(contextLocations);

            Reflections reflections = new Reflections(scanConfiguration);
            Set<Class<? extends ComponentFieldTypeConverter>> fieldConverters = reflections.getSubTypesOf(ComponentFieldTypeConverter.class);
            for (Class<? extends ComponentFieldTypeConverter> fieldConverter : fieldConverters) {
                for (Type type : fieldConverter.getGenericInterfaces()) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type rawType = parameterizedType.getRawType();
                        if (rawType == ComponentFieldTypeConverter.class) {
                            Type paramType = parameterizedType.getActualTypeArguments()[0];
                            if (paramType instanceof Class) {
                                try {
                                    converterMap.put((Class) paramType, fieldConverter.newInstance());
                                } catch (InstantiationException | IllegalAccessException e) {
                                    throw new IllegalStateException("Unable to create converter of type " + fieldConverter.getSimpleName(), e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T> String convertFrom(T value, Class<T> clazz) {
        System.out.println("Converter called");
        init();
        ComponentFieldTypeConverter converter = converterMap.get(clazz);
        if (converter == null)
            throw new IllegalStateException("Unable to find converter for type " + clazz.getSimpleName());
        return converter.convertFrom(value);
    }

    @Override
    public <T> T convertTo(String value, Class<T> clazz) {
        System.out.println("Converter called");
        init();
        ComponentFieldTypeConverter converter = converterMap.get(clazz);
        if (converter == null)
            throw new IllegalStateException("Unable to find converter for type " + clazz.getSimpleName());
        return (T) converter.convertTo(value);
    }
}
