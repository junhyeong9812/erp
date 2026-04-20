package com.erp.common.support;

import java.time.LocalDateTime;
import java.time.ZoneId;

public interface SystemClock {
    LocalDateTime now();

    SystemClock DEFAULT = () -> LocalDateTime.now(ZoneId.of("Asia/Seoul"));
}