package com.ruoyi.consts;

public interface AdminRedisKey {


    String ADMIN_BASE = "fy:admin:";

    interface Jky {

        String ROOT_KEY = ADMIN_BASE + "jky:";

        String ORDER_LAST_SYNC_TIME = ROOT_KEY + "order:lst";

        String ORDER_SYNC_LOCK = ROOT_KEY + "order:lock";

        String REFUND_LAST_SYNC_TIME = ROOT_KEY + "refund:lst";

        String REFUND_SYNC_LOCK = ROOT_KEY + "refund:lock";

        String GOODS_LAST_SYNC_TIME = ROOT_KEY + "goods:lst";

        String GOODS_SYNC_LOCK = ROOT_KEY + "goods:lock";

        String VENDOR_SYNC_LOCK = ROOT_KEY + "vendor:lock";

        String LOGISTICS_TASK_LOCK = ROOT_KEY + "logistics:lock";

    }

    interface Order {

        String LOCK_PREFIX = ADMIN_BASE + "order:lock:";

    }

    interface SalesReturn {

        String ROOT_KEY = ADMIN_BASE + "salesreturn:";

        /**
         * 退货单号每日流水 key，使用前需拼接 yyyyMMdd
         */
        String NO_DAILY_SEQ = ROOT_KEY + "dseq:";

    }

    interface Contract {

        String ROOT_KEY = ADMIN_BASE + "contract:";

        /**
         * 合同编号每日流水 key，使用前需拼接 yyyyMMdd
         */
        String NO_DAILY_SEQ = ROOT_KEY + "dseq:";

        /**
         * 状态拉取兜底任务锁
         */
        String STATUS_SYNC_LOCK = ROOT_KEY + "status:lock";

    }

}
