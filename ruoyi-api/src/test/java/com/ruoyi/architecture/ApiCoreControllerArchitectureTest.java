package com.ruoyi.architecture;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 用户、财务和订单 API 控制器分层约束测试。
 */
class ApiCoreControllerArchitectureTest {
    private static final String[] CONTROLLER_PACKAGES = {
            "com.ruoyi.web.controller.user", "com.ruoyi.web.controller.bill",
            "com.ruoyi.web.controller.order"
    };

    @Test
    void controllerMustOnlyInjectBizLayer() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        for (String packageName : CONTROLLER_PACKAGES) {
            for (org.springframework.beans.factory.config.BeanDefinition bean :
                    scanner.findCandidateComponents(packageName)) {
                assertControllerFields(Class.forName(bean.getBeanClassName()));
            }
        }
    }

    private void assertControllerFields(Class<?> controller) {
        for (Field field : controller.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            assertTrue(field.getType().getName().startsWith("com.ruoyi.biz."),
                    controller.getName() + " 跨层依赖了 " + field.getType().getName());
        }
    }
}
