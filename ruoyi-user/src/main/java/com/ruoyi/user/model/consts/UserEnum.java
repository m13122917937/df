package com.ruoyi.user.model.consts;

public class UserEnum {


    public enum UserDisable {
        /**
         * 0:正常
         */
        NORMAL(0),
        /**
         * 1:禁用
         */
        DISABLE(1);

        private Integer value;

        UserDisable(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }


    public enum UserDeleted {
        /**
         * 0:正常
         */
        NORMAL(0),
        /**
         * 1:删除
         */
        DELETED(1);

        private Integer value;

        UserDeleted(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }


    public enum UserOwner {
        /**
         * 0:主账号
         */
        MASTER(0),
        /**
         * 1:
         */
        PEOPLE(1);

        private Integer value;

        UserOwner(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
