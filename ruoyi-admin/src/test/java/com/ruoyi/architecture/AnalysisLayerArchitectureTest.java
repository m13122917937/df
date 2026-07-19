package com.ruoyi.architecture;

import com.ruoyi.analysis.facade.impl.AnalysisConfigFacadeImpl;
import com.ruoyi.analysis.facade.impl.AnalysisDashboardFacadeImpl;
import com.ruoyi.analysis.facade.impl.AnalysisRebateFacadeImpl;
import com.ruoyi.analysis.facade.impl.AnalysisSyncFacadeImpl;
import com.ruoyi.biz.analysis.AnalysisConfigBizService;
import com.ruoyi.biz.analysis.AnalysisDashboardBizService;
import com.ruoyi.biz.analysis.AnalysisRebateBizService;
import com.ruoyi.biz.analysis.AnalysisSyncBizService;
import com.ruoyi.web.controller.analysis.AnalysisConfigController;
import com.ruoyi.web.controller.analysis.AnalysisDashboardController;
import com.ruoyi.web.controller.analysis.AnalysisRebateController;
import com.ruoyi.web.controller.analysis.AnalysisSyncController;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 经营分析模块分层架构约束测试。
 */
class AnalysisLayerArchitectureTest {
    private static final Class<?>[] CONTROLLERS = {
            AnalysisConfigController.class, AnalysisDashboardController.class,
            AnalysisRebateController.class, AnalysisSyncController.class
    };
    private static final Class<?>[] BIZ_SERVICES = {
            AnalysisConfigBizService.class, AnalysisDashboardBizService.class,
            AnalysisRebateBizService.class, AnalysisSyncBizService.class
    };
    private static final Class<?>[] FACADES = {
            AnalysisConfigFacadeImpl.class, AnalysisDashboardFacadeImpl.class,
            AnalysisRebateFacadeImpl.class, AnalysisSyncFacadeImpl.class
    };

    @Test
    void controllerMustOnlyInjectBizLayer() {
        assertInjectedFieldsInPackage(CONTROLLERS, "com.ruoyi.biz.analysis");
    }

    @Test
    void bizMustOnlyInjectFacadeLayer() {
        assertInjectedFieldsInPackage(BIZ_SERVICES, "com.ruoyi.analysis.facade");
    }

    @Test
    void facadeMustOnlyInjectServiceLayer() {
        assertInjectedFieldsInPackage(FACADES, "com.ruoyi.analysis.service");
    }

    @Test
    void controllerMustNotExposeDomainLayerObjects() {
        for (Class<?> controller : CONTROLLERS) {
            for (Method method : controller.getDeclaredMethods()) {
                assertFalse(isDomainModel(method.getReturnType()), method + " 返回了领域对象");
                Arrays.stream(method.getParameterTypes()).forEach(type ->
                        assertFalse(isDomainModel(type), method + " 接收了领域对象"));
            }
        }
    }

    @Test
    void transactionMustNotBeDeclaredAboveServiceLayer() {
        assertNoTransaction(CONTROLLERS);
        assertNoTransaction(BIZ_SERVICES);
        assertNoTransaction(FACADES);
    }

    private void assertInjectedFieldsInPackage(Class<?>[] types, String allowedPackage) {
        for (Class<?> type : types) {
            for (Field field : type.getDeclaredFields()) {
                assertTrue(field.getType().getName().startsWith(allowedPackage),
                        type.getName() + " 跨层依赖了 " + field.getType().getName());
            }
        }
    }

    private void assertNoTransaction(Class<?>[] types) {
        for (Class<?> type : types) {
            assertFalse(type.isAnnotationPresent(Transactional.class), type.getName() + " 不应声明事务");
            for (Method method : type.getDeclaredMethods()) {
                assertFalse(method.isAnnotationPresent(Transactional.class), method + " 不应声明事务");
            }
        }
    }

    private boolean isDomainModel(Class<?> type) {
        return type.getName().startsWith("com.ruoyi.analysis.model");
    }
}
