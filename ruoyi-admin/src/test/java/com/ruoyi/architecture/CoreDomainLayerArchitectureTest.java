package com.ruoyi.architecture;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 用户、财务和订单领域模块的分层约束测试。
 */
class CoreDomainLayerArchitectureTest {
    private static final String[] FACADE_PACKAGES = {
            "com.ruoyi.user.facade.impl", "com.ruoyi.capital.facade.impl",
            "com.ruoyi.product.facade.impl", "com.ruoyi.bill.facade.impl",
            "com.ruoyi.order.facade.impl", "com.ruoyi.rule.facade.impl",
            "com.ruoyi.express.facade.impl"
    };
    private static final String[] CONTROLLER_PACKAGES = {
            "com.ruoyi.web.controller.company", "com.ruoyi.web.controller.bill",
            "com.ruoyi.web.controller.order"
    };

    @Test
    void controllerMustOnlyInjectBizLayer() throws ClassNotFoundException {
        for (Class<?> controller : scanComponents(CONTROLLER_PACKAGES)) {
            for (Field field : controller.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                assertTrue(field.getType().getName().startsWith("com.ruoyi.biz."),
                        controller.getName() + " 跨层依赖了 " + field.getType().getName());
            }
        }
    }

    @Test
    void facadeMustOnlyInjectServiceLayer() throws ClassNotFoundException {
        for (Class<?> facade : scanComponents(FACADE_PACKAGES)) {
            for (Field field : facade.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                assertTrue(field.getType().getName().contains(".service."),
                        facade.getName() + " 跨层依赖了 " + field.getType().getName());
            }
        }
    }

    @Test
    void transactionMustStayOutOfFacadeLayer() throws ClassNotFoundException {
        for (Class<?> facade : scanComponents(FACADE_PACKAGES)) {
            assertFalse(facade.isAnnotationPresent(Transactional.class), facade.getName() + " 不应声明事务");
            for (Method method : facade.getDeclaredMethods()) {
                assertFalse(method.isAnnotationPresent(Transactional.class), method + " 不应声明事务");
            }
        }
    }

    private List<Class<?>> scanComponents(String[] packages) throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        List<Class<?>> result = new ArrayList<>();
        for (String packageName : packages) {
            for (org.springframework.beans.factory.config.BeanDefinition bean :
                    scanner.findCandidateComponents(packageName)) {
                result.add(Class.forName(bean.getBeanClassName()));
            }
        }
        return result;
    }
}
