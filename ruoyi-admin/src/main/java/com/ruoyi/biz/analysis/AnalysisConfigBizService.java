package com.ruoyi.biz.analysis;

import com.alibaba.excel.EasyExcel;
import com.ruoyi.analysis.facade.AnalysisConfigFacade;
import com.ruoyi.analysis.model.bo.AnalysisCostConfigBO;
import com.ruoyi.analysis.model.bo.AnalysisImportLogBO;
import com.ruoyi.analysis.model.param.AnalysisCostConfigParam;
import com.ruoyi.analysis.model.query.AnalysisQuery;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.web.convert.analysis.AnalysisConfigWebConvert;
import com.ruoyi.web.vo.analysis.AnalysisConfigExcelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * 经营核算配置业务编排。
 */
@Component
public class AnalysisConfigBizService {
    @Autowired
    private AnalysisConfigFacade configFacade;

    /**
     * 查询核算配置。
     *
     * @param query 查询条件
     * @return 配置列表
     */
    public List<AnalysisCostConfigBO> list(AnalysisQuery query) {
        return configFacade.list(query);
    }

    /**
     * 保存核算配置。
     *
     * @param param 配置参数
     * @param operatorId 当前操作人主键
     * @return 配置主键
     */
    public Long save(AnalysisCostConfigParam param, Long operatorId) {
        param.setOperatorId(operatorId);
        return configFacade.save(param);
    }

    /**
     * 删除核算配置。
     *
     * @param id 配置主键
     */
    public void delete(Long id) {
        configFacade.delete(id);
    }

    /**
     * 下载核算配置导入模板。
     */
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        prepareResponse(response, "经营核算配置导入模板.xlsx");
        EasyExcel.write(response.getOutputStream(), AnalysisConfigExcelVO.class)
                .sheet("导入模板").doWrite(new ArrayList<AnalysisConfigExcelVO>());
    }

    /**
     * 导出核算配置。
     */
    public void export(AnalysisQuery query, HttpServletResponse response) throws IOException {
        List<AnalysisConfigExcelVO> rows = AnalysisConfigWebConvert.INSTANCE.toExcelList(list(query));
        prepareResponse(response, "经营核算配置.xlsx");
        EasyExcel.write(response.getOutputStream(), AnalysisConfigExcelVO.class)
                .sheet("核算配置").doWrite(rows);
    }

    /**
     * 导入核算配置。
     */
    @SuppressWarnings("unchecked")
    public int importExcel(String configType, boolean overwrite, MultipartFile file, Long operatorId) {
        try {
            List<AnalysisConfigExcelVO> rows = (List<AnalysisConfigExcelVO>) (List<?>) EasyExcel
                    .read(file.getInputStream(), AnalysisConfigExcelVO.class, null).sheet().doReadSync();
            List<AnalysisCostConfigParam> params = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            for (int index = 0; index < rows.size(); index++) {
                AnalysisConfigExcelVO row = rows.get(index);
                AnalysisCostConfigParam param = AnalysisConfigWebConvert.INSTANCE.toParam(row);
                param.setConfigType(configType);
                param.setOperatorId(operatorId);
                if (isValidRow(param, index + 2, errors)) {
                    params.add(param);
                }
            }
            String validationMessage = invalidRowsMessage(errors);
            if (validationMessage != null) {
                configFacade.recordImportFailure(configType, file.getOriginalFilename(), overwrite,
                        rows.size(), validationMessage, operatorId);
                throw new ServiceException(validationMessage);
            }
            return configFacade.importConfigs(params, overwrite, file.getOriginalFilename(), operatorId);
        } catch (IOException exception) {
            String errorMessage = "读取 Excel 失败：" + exception.getMessage();
            configFacade.recordImportFailure(configType, file.getOriginalFilename(), overwrite, 0,
                    errorMessage, operatorId);
            throw new ServiceException(errorMessage);
        }
    }

    private void validateRow(AnalysisCostConfigParam param, int rowNumber) {
        if (param.getBusinessDate() == null && param.getMonthValue() == null
                && param.getStartDate() == null) {
            throw new ServiceException("第" + rowNumber + "行缺少发生日期、月份或生效时间");
        }
        if (param.getAmount() == null && param.getCoefficient() == null) {
            throw new ServiceException("第" + rowNumber + "行金额和系数不能同时为空");
        }
    }

    /**
     * 查询最近的配置导入记录。
     *
     * @param limit 最大条数
     * @return 导入记录
     */
    public List<AnalysisImportLogBO> importLogs(int limit) {
        return configFacade.importLogs(limit);
    }

    private boolean isValidRow(AnalysisCostConfigParam param, int rowNumber, List<String> errors) {
        try {
            validateRow(param, rowNumber);
            return true;
        } catch (ServiceException exception) {
            errors.add(exception.getMessage());
            return false;
        }
    }

    private String invalidRowsMessage(List<String> errors) {
        if (errors.isEmpty()) {
            return null;
        }
        StringJoiner joiner = new StringJoiner("；");
        for (String error : errors) {
            joiner.add(error);
        }
        return "导入校验失败：" + joiner;
    }

    private void prepareResponse(HttpServletResponse response, String fileName) throws IOException {
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + encoded);
    }
}
