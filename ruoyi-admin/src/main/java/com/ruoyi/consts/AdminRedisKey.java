package com.ruoyi.consts;

public interface AdminRedisKey {


    String ADMIN_BASE = "fy:admin:";

    interface Jky {

        String ROOT_KEY = ADMIN_BASE + "jky:";

        String ORDER_LAST_SYNC_TIME = ROOT_KEY + "order:lastSyncTime";

        String ORDER_SYNC_LOCK = ROOT_KEY + "order:sync:lock";

    }

}
