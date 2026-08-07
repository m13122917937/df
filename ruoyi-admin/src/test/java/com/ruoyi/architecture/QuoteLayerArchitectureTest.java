package com.ruoyi.architecture;

import com.ruoyi.biz.quote.QuoteManageBizService;
import com.ruoyi.quote.facade.impl.QuotePriceTierFacade;
import com.ruoyi.quote.facade.impl.QuoteProductFacade;
import com.ruoyi.web.controller.quote.QuoteManageController;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 报价模块分层架构约束测试。
 */
class QuoteLayerArchitectureTest {

    private static final Class<?>[] CONTROLLERS = {QuoteManageController.class};
    private static final Class<?>[] BIZ_SERVICES = {QuoteManageBizService.class};
    private static final Class<?>[] FACADES = {QuotePriceTierFacade.class, QuoteProductFacade.class};

    /**
     * Controller 只能注入 Biz 层。
     */
    @Test
    void controllerMustOnlyInjectBizLayer() {
        assertInjectedFieldsInPackage(CONTROLLERS, "com.ruoyi.biz.quote");
    }

    /**
     * Biz 只能注入 Facade 层。
     */
    @Test
    void bizMustOnlyInjectFacadeLayer() {
        assertInjectedFieldsInPackage(BIZ_SERVICES, "com.ruoyi.quote.facade");
    }

    /**
     * Facade 只能注入 Service 层。
     */
    @Test
    void facadeMustOnlyInjectServiceLayer() {
        assertInjectedFieldsInPackage(FACADES, "com.ruoyi.quote.service");
    }

    /**
     * Controller 不得暴露领域对象。
     */
    @Test
    void controllerMustNotExposeDomainLayerObjects() {
        for (Class<?> controller : CONTROLLERS) {
            for (Method method : controller.getDeclaredMethods()) {
                assertFalse(isDomainModel(method.getReturnType()),
                        method + " 返回了领域对象");
                Arrays.stream(method.getParameterTypes()).forEach(type ->
                        assertFalse(isDomainModel(type), method + " 接收了领域对象"));
            }
        }
    }

    /**
     * Controller、Biz、Facade 均不得声明事务。
     */
    @Test
    void transactionMustNotBeDeclaredAboveServiceLayer() {
        assertNoTransaction(CONTROLLERS);
        assertNoTransaction(BIZ_SERVICES);
        assertNoTransaction(FACADES);
    }

    private void assertInjectedFieldsInPackage(final Class<?>[] types, final String allowedPackage) {
        for (Class<?> type : types) {
            for (Field field : type.getDeclaredFields()) {
                assertTrue(field.getType().getName().startsWith(allowedPackage),
                        type.getName() + " 跨层依赖了 " + field.getType().getName());
            }
        }
    }

    private boolean isDomainModel(final Class<?> type) {
        String name = type.getName();
        return name.startsWith("com.ruoyi.quote.domain")
                || name.startsWith("com.ruoyi.quote.model");
    }

    private void assertNoTransaction(final Class<?>[] types) {
        for (Class<?> type : types) {
            assertFalse(type.isAnnotationPresent(Transactional.class),
                    type.getName() + " 不应声明 @Transactional");
        }
    }
}
