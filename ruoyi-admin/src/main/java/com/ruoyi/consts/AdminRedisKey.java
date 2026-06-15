package com.ruoyi.consts;

public interface AdminRedisKey {


    String ADMIN_BASE = "fy:admin:";

    interface Jky {

        String ROOT_KEY = ADMIN_BASE + "jky:";

        String ORDER_LAST_SYNC_TIME = ROOT_KEY + "order:lastSyncTime";

        String ORDER_SYNC_LOCK = ROOT_KEY + "order:sync:lock";

        String REFUND_LAST_SYNC_TIME = ROOT_KEY + "refund:lastSyncTime";

        String REFUND_SYNC_LOCK = ROOT_KEY + "refund:sync:lock";

        String GOODS_LAST_SYNC_TIME = ROOT_KEY + "goods:lastSyncTime";

        String GOODS_SYNC_LOCK = ROOT_KEY + "goods:sync:lock";

        String VENDOR_SYNC_LOCK = ROOT_KEY + "vendor:sync:lock";

    }

}
