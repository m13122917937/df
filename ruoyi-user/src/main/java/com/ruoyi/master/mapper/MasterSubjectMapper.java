package com.ruoyi.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.master.domain.MasterSubject;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经营主体数据访问接口。
 */
@Mapper
public interface MasterSubjectMapper extends BaseMapper<MasterSubject> {

    /**
     * 按吉客云主体 ID 幂等写入经营主体。
     *
     * @param subject 经营主体
     * @return 受影响行数
     */
    int upsertByJkySubjectId(MasterSubject subject);
}
