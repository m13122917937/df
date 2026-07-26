package com.ruoyi.master.convert;

import com.ruoyi.jky.rep.company.CompanyQueryRep;
import com.ruoyi.master.domain.MasterSubject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 经营主体对象转换测试。
 */
class MasterSubjectConvertTest {

    /**
     * 吉客云公司字段应转换为经营主体字段。
     */
    @Test
    void shouldConvertJkyCompanyToMasterSubject() {
        CompanyQueryRep.CompanyInfoRep source = new CompanyQueryRep.CompanyInfoRep();
        source.setCompanyId(1001L);
        source.setCompanyCode("SUBJECT-001");
        source.setCompanyName("经营主体一号");
        source.setCompanyShortName("主体一号");

        MasterSubject subject = MasterSubjectConvert.INSTANCE.toDomain(source);

        assertEquals(1001L, subject.getJkySubjectId());
        assertEquals("SUBJECT-001", subject.getSubjectCode());
        assertEquals("经营主体一号", subject.getSubjectName());
        assertEquals("主体一号", subject.getSubjectShortName());
    }
}
