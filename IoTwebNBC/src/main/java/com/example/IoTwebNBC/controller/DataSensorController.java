package com.example.IoTwebNBC.controller;

import com.example.IoTwebNBC.entity.DataSensorEntity;
import com.example.IoTwebNBC.repository.DataSensorEntityRepository;
//import com.example.IoTwebNBC.repository.DataSensorEntityRepositoryImpl;
import com.example.IoTwebNBC.request.DataSensorFilterRequest;
import com.example.IoTwebNBC.service.DataSensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/data_sensor")
@Slf4j
public class DataSensorController {
    private final DataSensorEntityRepository dataSensorEntityRepository;

    private final DataSensorService dataSensorService;

    @GetMapping("")
    public String show(@ModelAttribute("filter")  DataSensorFilterRequest filter
                        ,@RequestParam(defaultValue = "0") int page
                        ,@RequestParam(defaultValue = "10") int size
                        , Model model ) {

            // Keep original behaviour: always sort by timestamp desc on server-side
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

            Page<DataSensorEntity> ds = dataSensorService.findByFilter(filter, pageable);

            model.addAttribute("filter", filter);

            // Ensure requested page is within bounds - mirror device_action behaviour
            int requestedPage = page;
            int totalPages = ds.getTotalPages();
            if (totalPages == 0) totalPages = 1; // keep UI showing a single page when no results
            if (requestedPage < 0) requestedPage = 0;
            if (requestedPage >= totalPages) requestedPage = totalPages - 1;

            if (requestedPage != page) {
                pageable = PageRequest.of(requestedPage, size, Sort.by("timestamp").descending());
                ds = dataSensorService.findByFilter(filter, pageable);
            }

            model.addAttribute("dataSensorEntities", ds.getContent());
            model.addAttribute("page", requestedPage);
            model.addAttribute("size", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", ds.getTotalElements());

            return "user/data_sensor";
    }
    

}
