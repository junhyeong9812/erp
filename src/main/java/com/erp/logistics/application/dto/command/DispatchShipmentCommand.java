package com.erp.logistics.application.dto.command;

public record DispatchShipmentCommand(Long shipmentId, String driverId, String trackingNumber) {}