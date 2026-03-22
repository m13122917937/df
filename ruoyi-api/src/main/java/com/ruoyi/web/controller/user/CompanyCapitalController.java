package com.ruoyi.web.controller.user;

import com.ruoyi.capital.facade.ICompanyCapitalFacade;
import com.ruoyi.capital.model.bo.CompanyCapitalBO;
import com.ruoyi.capital.model.bo.CompanyCapitalLogBO;
import com.ruoyi.capital.model.consts.CompanyCapitalConsts;
import com.ruoyi.capital.model.query.CompanyCapitalLogQuery;
import com.ruoyi.capital.model.query.CompanyCapitalQuery;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.model.page.PageBO;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.mapper.user.CapitalConvert;
import com.ruoyi.web.form.user.CompanyCapitalLogForm;
import com.ruoyi.web.util.ExcelUtil;
import com.ruoyi.web.vo.user.CompanyCapitalLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j

@Controller
@RequestMapping("company/capital")
public class CompanyCapitalController extends BaseController {

    @Autowired
    ICompanyCapitalFacade companyCapitalFacade;



    @GetMapping("/type/info")
    @ResponseBody
    public AjaxResult typeInfo() {

        CompanyCapitalConsts.LogTypes[] logTypes = CompanyCapitalConsts.LogTypes.values();
        Map<Integer, String> values = new HashMap<>();
        for (CompanyCapitalConsts.LogTypes value : logTypes) {
            values.put(value.getCode(), value.getMsg());
        }

        return AjaxResult.success(values);
    }



    @GetMapping("/info")
    @ResponseBody
    public AjaxResult info() {

        CompanyCapitalBO companyCapitalBO = companyCapitalFacade.queryOne(new CompanyCapitalQuery().setCompanyId(getDeptId()).setServiceType(CompanyCapitalConsts.Types.DEPOSIT.getCode()));
        return Objects.isNull(companyCapitalBO) ? AjaxResult.success(BigDecimal.ZERO) : AjaxResult.success(companyCapitalBO.getAvailableAmount());
    }


    @PostMapping("/list")
    @ResponseBody

    public TableDataInfo list(@RequestBody CompanyCapitalLogForm logParam) {
        CompanyCapitalLogQuery query = CapitalConvert.INSTANCE.toLogQuery(logParam).setCompanyId(getDeptId());
        ;
        PageBO<CompanyCapitalLogBO> pageBO = companyCapitalFacade.pageLog(query, startParamV2("create_time desc"));

        return getDataTable(CapitalConvert.INSTANCE.toVO(pageBO.getData()), pageBO.getTotal());
    }



    @GetMapping("/export")
    public void export(CompanyCapitalLogForm logParam) throws IOException {

        List<CompanyCapitalLogBO> companyCapitalLogBOS = companyCapitalFacade.listLog(CapitalConvert.INSTANCE.toLogQuery(logParam).setCompanyId(getDeptId()));
        List<CompanyCapitalLogVO> voList = CapitalConvert.INSTANCE.toVO(companyCapitalLogBOS);
        for (CompanyCapitalLogVO companyCapitalLogVO : voList) {
            companyCapitalLogVO.setTypeName(CompanyCapitalConsts.LogTypes.fromValue(companyCapitalLogVO.getType()).getMsg());
        }
        HttpServletResponse response = ServletUtils.getResponse();

        ServletUtils.renderExcel(response, "保证金详情");
        ExcelUtil.write(response.getOutputStream(), voList, CompanyCapitalLogVO.class);

    }


}
