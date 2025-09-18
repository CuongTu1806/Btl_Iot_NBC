package com.example.IoTwebNBC.controller;

import com.example.IoTwebNBC.entity.DataSensorEntity;
import com.example.IoTwebNBC.repository.DataSensorEntityRepository;
import com.example.IoTwebNBC.service.impl.CommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/dashboard")
public class DashboardController {
    private final DataSensorEntityRepository repo;
    private final CommandService commandService;

    @GetMapping("/{room}")
    public String dashboard(@PathVariable String room, Model model) {
        // Lấy bản ghi mới nhất trong room
        DataSensorEntity latest = repo.findFirstByRoomOrderByTimestampDesc(room)
                .orElse(null);
        model.addAttribute("room", room);
        model.addAttribute("sensor", latest);

        // Preload latest 10 records for charts (oldest -> newest)
        var latestList = repo.findLatest(room, org.springframework.data.domain.PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("timestamp").descending()));
        java.util.List<java.util.Map<String, Object>> preloaded = new java.util.ArrayList<>();
        for (DataSensorEntity e : latestList) {
            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", e.getId());
            m.put("temperature", e.getTemperature());
            m.put("humidity", e.getHumidity());
            m.put("lightLevel", e.getLightLevel());
            m.put("timestamp", e.getTimestamp());
            preloaded.add(m);
        }
        // ensure ascending order for charting (oldest first)
        preloaded.sort((a,b) -> {
            java.time.temporal.Temporal t1 = (java.time.temporal.Temporal) a.get("timestamp");
            java.time.temporal.Temporal t2 = (java.time.temporal.Temporal) b.get("timestamp");
            // fallback to string compare if not temporal
            try{
                java.time.Instant i1 = java.time.Instant.from((java.time.temporal.TemporalAccessor) t1);
                java.time.Instant i2 = java.time.Instant.from((java.time.temporal.TemporalAccessor) t2);
                return i1.compareTo(i2);
            }catch(Exception ex){
                return String.valueOf(a.get("timestamp")).compareTo(String.valueOf(b.get("timestamp")));
            }
        });
        model.addAttribute("latestTenForDashboard", preloaded);

        return "/user/dashboard"; // -> resources/templates/dashboard.html
    }




    @PostMapping("/{room}/{device}/{action}")
    public ResponseEntity<?> send(@PathVariable String room,
                                  @PathVariable String device,
                                  @PathVariable String action) {
        commandService.sendCommand(room, device, action);
        return ResponseEntity.ok(Map.of("status", "PUBLISHED"));
    }
}
