package com.example.IoTwebNBC.controller;


import com.example.IoTwebNBC.repository.DataSensorEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/data_sensor")
@RequiredArgsConstructor
public class StreamController {

    private final DataSensorEntityRepository repo;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @GetMapping(value = "/stream/room1", produces = "text/event-stream")
    public SseEmitter stream() {
        String room = "room1";
        SseEmitter emitter = new SseEmitter(0L); // không timeout

        emitter.onError(e -> System.out.println("SSE error: " + e));
        emitter.onCompletion(() -> System.out.println("SSE complete"));
        emitter.onTimeout(() -> System.out.println("SSE timeout"));

        // Gửi snapshot ngay khi connect (nếu có)
        repo.findFirstByRoomOrderByIdDesc("room1").ifPresent(e -> {
            try {
                emitter.send(SseEmitter.event().name("sensor").data(e));
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        });

        // Poll DB 1s/lần và gửi record mới nhất (không check gì thêm)
        scheduler.scheduleAtFixedRate(() -> {
            try {
                repo.findFirstByRoomOrderByIdDesc("room1").ifPresent(e -> {
                    try {
                        emitter.send(SseEmitter.event().name("sensor").data(e));
                    } catch (IOException ex) {
                        emitter.complete();
                    }
                });
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        }, 1, 2, TimeUnit.SECONDS);

        return emitter;
    }
}


