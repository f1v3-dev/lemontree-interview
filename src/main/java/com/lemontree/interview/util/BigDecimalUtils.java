package com.lemontree.interview.util;

import java.math.BigDecimal;

/**
 * BigDecimal 관련 유틸리티 클래스입니다.
 *
 * @author 정승조
 * @version 2024. 08. 09.
 */
public final class BigDecimalUtils {

    private BigDecimalUtils() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스를 금지합니다.");
    }

    public static BigDecimalWrapper is(BigDecimal value) {
        return new BigDecimalWrapper(value);
    }

    public static class BigDecimalWrapper {
        private final BigDecimal value;

        private BigDecimalWrapper(BigDecimal value) {
            this.value = value;
        }

        public boolean equals(BigDecimal target) {
            return value.compareTo(target) == 0;
        }

        public boolean greaterThan(BigDecimal target) {
            return value.compareTo(target) > 0;
        }

        public boolean lessThan(BigDecimal target) {
            return value.compareTo(target) < 0;
        }

        public boolean greaterThanOrEquals(BigDecimal target) {
            return value.compareTo(target) >= 0;
        }

        public boolean lessThanOrEquals(BigDecimal target) {
            return value.compareTo(target) <= 0;
        }
    }
}
