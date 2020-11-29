package com.zte.spring_rpc;

import com.zte.annotation.RpcServiceAPI;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * 用于Spring动态注入rpc接口,类似于spring-mybatis整合,当被@RpcProxyFactory注解的interface使用@Autowired时会注入其代理类
 */
@Component
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {
    private static Set<Class<?>> classCache = new HashSet<>();
    private final static String RESOURCE_PATTERN = "/**/*.class";
    private static final String BASE_PACKAGE = "com.zte";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        getAPIClz();
        classCache.forEach(
                beanClazz -> {
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClazz);
                    GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

                    //在这里，我们可以给该对象的属性注入对应的实例。
                    //比如mybatis，就在这里注入了dataSource和sqlSessionFactory，
                    // 注意，如果采用definition.getPropertyValues()方式的话，
                    // 类似definition.getPropertyValues().add("interfaceType", beanClazz);
                    // 则要求在FactoryBean（本应用中即ServiceFactory）提供setter方法，否则会注入失败
                    // 如果采用definition.getConstructorArgumentValues()，
                    // 则FactoryBean中需要提供包含该属性的构造方法，否则会注入失败
                    definition.getConstructorArgumentValues().addGenericArgumentValue(beanClazz);

                    //注意，这里的BeanClass是生成Bean实例的工厂，不是Bean本身。
                    // FactoryBean是一种特殊的Bean，其返回的对象不是指定类的一个实例，
                    // 其返回的是该工厂Bean的getObject方法所返回的对象。
                    definition.setBeanClass(RpcClientProxyFactory.class);

                    //这里采用的是byType方式注入，类似的还有byName等
                    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                    registry.registerBeanDefinition(beanClazz.getSimpleName(), definition);
                }
        );
    }

    /**
     *
     */
    public void getAPIClz() {

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(BASE_PACKAGE)
                    + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    //扫描到的class
                    String className = reader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    //判断是否有指定注解
                    RpcServiceAPI annotation = clazz.getAnnotation(RpcServiceAPI.class);
                    if (annotation != null) {
                        //这个类使用了自定义注解
                        classCache.add(clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}