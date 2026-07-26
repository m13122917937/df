package com.ruoyi.master.convert;

import com.ruoyi.jky.rep.company.CompanyQueryRep;
import com.ruoyi.master.domain.MasterSubject;
import com.ruoyi.master.model.bo.MasterSubjectBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 经营主体领域对象转换器。
 */
@Mapper
public interface MasterSubjectConvert {

    MasterSubjectConvert INSTANCE = Mappers.getMapper(MasterSubjectConvert.class);

    /**
     * 将吉客云公司信息转换为经营主体。
     *
     * @param source 吉客云公司信息
     * @return 经营主体实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jkySubjectId", source = "companyId")
    @Mapping(target = "subjectCode", source = "companyCode")
    @Mapping(target = "subjectName", source = "companyName")
    @Mapping(target = "subjectShortName", source = "companyShortName")
    @Mapping(target = "sourceCreatedTime", source = "gmtCreate")
    @Mapping(target = "sourceModifiedTime", source = "gmtModified")
    @Mapping(target = "defaultPayerId", ignore = true)
    @Mapping(target = "lastSyncTime", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    MasterSubject toDomain(CompanyQueryRep.CompanyInfoRep source);

    /**
     * 将经营主体实体转换为业务对象。
     *
     * @param domain 经营主体实体
     * @return 经营主体业务对象
     */
    MasterSubjectBO toBO(MasterSubject domain);

    /**
     * 批量转换经营主体业务对象。
     *
     * @param domains 经营主体实体集合
     * @return 经营主体业务对象集合
     */
    List<MasterSubjectBO> toBOList(List<MasterSubject> domains);
}
