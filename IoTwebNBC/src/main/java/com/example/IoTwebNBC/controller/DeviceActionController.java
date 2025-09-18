package com.example.IoTwebNBC.controller;


import com.example.IoTwebNBC.entity.DataSensorEntity;
import com.example.IoTwebNBC.entity.DeviceActionEntity;
import com.example.IoTwebNBC.repository.DataSensorEntityRepository;
import com.example.IoTwebNBC.request.DeviceActionFilterRequest;
import com.example.IoTwebNBC.service.DataSensorService;
import com.example.IoTwebNBC.service.DeviceActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequiredArgsConstructor
@RequestMapping("/device_action")
@Slf4j
public class DeviceActionController {
    private final DeviceActionService deviceActionService;

    @GetMapping("")
    public String show(@ModelAttribute("filter") DeviceActionFilterRequest filter
            ,@RequestParam(defaultValue = "0") int page
            ,@RequestParam(defaultValue = "10") int size
            , Model model ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());

        Page<DeviceActionEntity> da = deviceActionService.findByFilter(filter, pageable);
        model.addAttribute("filter", filter);

        // Ensure requested page is within bounds - if out of bounds, clamp and re-query so the
        // returned content matches the page we display.
        int requestedPage = page;
        int totalPages = da.getTotalPages();
        if(totalPages == 0) totalPages = 1; // avoid zero pages in UI
        if(requestedPage < 0) requestedPage = 0;
        if(requestedPage >= totalPages) requestedPage = totalPages - 1;

        if(requestedPage != page){
            // re-create pageable with the clamped page and fetch again
            pageable = PageRequest.of(requestedPage, size, Sort.by("timestamp").descending());
            da = deviceActionService.findByFilter(filter, pageable);
        }

        model.addAttribute("deviceActions", da.getContent());
        model.addAttribute("page", requestedPage);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", da.getTotalElements());

        return "user/device_action";
    }


}
