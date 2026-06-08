package com.erp.notification.presentation.api;

import com.erp.notification.application.dto.command.SendNotificationCommand;
import com.erp.notification.application.port.inbound.NotificationUseCase;
import com.erp.notification.presentation.dto.request.SendNotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationUseCase useCase;

    public NotificationController(NotificationUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public ResponseEntity<Long> send(@RequestBody SendNotificationRequest req) {
        return ResponseEntity.ok(useCase.send(new SendNotificationCommand(
                req.recipientId(), req.title(), req.body(), req.channel())));
    }
}