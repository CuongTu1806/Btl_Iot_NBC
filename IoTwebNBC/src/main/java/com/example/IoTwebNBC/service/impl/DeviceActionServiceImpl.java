package com.example.IoTwebNBC.service.impl;

import com.example.IoTwebNBC.entity.DeviceActionEntity;
import com.example.IoTwebNBC.repository.DeviceActionRepository;
import com.example.IoTwebNBC.request.DeviceActionFilterRequest;
import com.example.IoTwebNBC.service.DeviceActionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeviceActionServiceImpl implements DeviceActionService {
    private final DeviceActionRepository deviceActionRepository;

    public DeviceActionServiceImpl(DeviceActionRepository deviceActionRepository) {
        this.deviceActionRepository = deviceActionRepository;
    }

    @Override
    public Page<DeviceActionEntity> findByFilter(DeviceActionFilterRequest filter, Pageable pageable) {
        return deviceActionRepository.findByFilter(filter, pageable);
    }
}
