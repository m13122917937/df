//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ruoyi.kuaidi100.model;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;

public class LogisticsRouteInfo implements Serializable {
    private Info from;
    private Info cur;
    private Info to;

    public Info getFrom() {
        return this.from;
    }

    public Info getCur() {
        return this.cur;
    }

    public Info getTo() {
        return this.to;
    }

    public void setFrom(final Info from) {
        this.from = from;
    }

    public void setCur(final Info cur) {
        this.cur = cur;
    }

    public void setTo(final Info to) {
        this.to = to;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof LogisticsRouteInfo)) {
            return false;
        } else {
            LogisticsRouteInfo other = (LogisticsRouteInfo)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$from = this.getFrom();
                Object other$from = other.getFrom();
                if (this$from == null) {
                    if (other$from != null) {
                        return false;
                    }
                } else if (!this$from.equals(other$from)) {
                    return false;
                }

                Object this$cur = this.getCur();
                Object other$cur = other.getCur();
                if (this$cur == null) {
                    if (other$cur != null) {
                        return false;
                    }
                } else if (!this$cur.equals(other$cur)) {
                    return false;
                }

                Object this$to = this.getTo();
                Object other$to = other.getTo();
                if (this$to == null) {
                    if (other$to != null) {
                        return false;
                    }
                } else if (!this$to.equals(other$to)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof LogisticsRouteInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $from = this.getFrom();
        result = result * 59 + ($from == null ? 43 : $from.hashCode());
        Object $cur = this.getCur();
        result = result * 59 + ($cur == null ? 43 : $cur.hashCode());
        Object $to = this.getTo();
        result = result * 59 + ($to == null ? 43 : $to.hashCode());
        return result;
    }

    public String toString() {
        Info var10000 = this.getFrom();
        return "LogisticsRouteInfo(from=" + var10000 + ", cur=" + this.getCur() + ", to=" + this.getTo() + ")";
    }

    public static class Info implements Serializable {
        private static final int PROVINCE_AREA_CODE_SUB = 8;
        private static final int PROVINCE_CITY_CODE_SUB = 6;
        private static final String NULL_DISTRICT = "00";
        private String number;
        private String name;

        public String getAreaCode(Boolean toArea) {
            return Boolean.TRUE.equals(toArea) ? StrUtil.subPre(this.number, 8) : StrUtil.subPre(this.number, 6);
        }

        public Boolean checkAreaCode() {
            String areaCode = StrUtil.sub(this.number, 6, 8);
            return !"00".equals(areaCode);
        }

        public String getNumber() {
            return this.number;
        }

        public String getName() {
            return this.name;
        }

        public Info setNumber(final String number) {
            this.number = number;
            return this;
        }

        public Info setName(final String name) {
            this.name = name;
            return this;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof Info)) {
                return false;
            } else {
                Info other = (Info)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    Object this$number = this.getNumber();
                    Object other$number = other.getNumber();
                    if (this$number == null) {
                        if (other$number != null) {
                            return false;
                        }
                    } else if (!this$number.equals(other$number)) {
                        return false;
                    }

                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null) {
                        if (other$name != null) {
                            return false;
                        }
                    } else if (!this$name.equals(other$name)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(final Object other) {
            return other instanceof Info;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Object $number = this.getNumber();
            result = result * 59 + ($number == null ? 43 : $number.hashCode());
            Object $name = this.getName();
            result = result * 59 + ($name == null ? 43 : $name.hashCode());
            return result;
        }

        public String toString() {
            String var10000 = this.getNumber();
            return "LogisticsRouteInfo.Info(number=" + var10000 + ", name=" + this.getName() + ")";
        }
    }
}
