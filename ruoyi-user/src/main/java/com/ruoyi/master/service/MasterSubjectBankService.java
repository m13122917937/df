package com.ruoyi.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.master.domain.MasterSubjectBank;
import com.ruoyi.master.mapper.MasterSubjectBankMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 主体银行卡维护服务（原 PayerService）。
 *
 * @author ruoyi
 * @date 2025-11-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasterSubjectBankService extends ServiceImpl<MasterSubjectBankMapper, MasterSubjectBank> {
}
