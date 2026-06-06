package com.erp.promotion.application.dto.command;

import java.time.LocalDate;

public record EarnPointCommand(Long customerId, int amount, LocalDate expireOn) {}