package com.example.IoTwebNBC.service.impl;

import com.example.IoTwebNBC.entity.DataSensorEntity;
import com.example.IoTwebNBC.enums.CompareOpEnum;
import com.example.IoTwebNBC.repository.DataSensorEntityRepository;
import com.example.IoTwebNBC.repository.DataSensorEntityRepositoryImpl;
import com.example.IoTwebNBC.request.DataSensorFilterRequest;
import com.example.IoTwebNBC.service.DataSensorService;
import com.example.IoTwebNBC.utils.DataTypeUltil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DataSensorServiceImpl implements DataSensorService {

    @Autowired
    private DataSensorEntityRepository repo;



    @Override
    public Page<DataSensorEntity> findByFilter(DataSensorFilterRequest filter, Pageable pageable) {
        if(filter.getInputSearch() == null && filter.getThresholdOps() == null) {
            return repo.findByFilter(" where 1 = 1 ", pageable);
        }

        CompareOpEnum operator; // loại ngưỡng
        StringBuilder where = new StringBuilder();
        where.append(" where 1 = 1 ");

        if(filter.getThresholdOps() != null) {
            for(String sensor : filter.getThresholdOps().keySet()){
                operator = CompareOpEnum.fromText(filter.getThresholdOps().get(sensor));
                if (operator == CompareOpEnum.RANGE) {
                    String[] ss2 = filter.getThresholdValues().get(sensor).split(";");
                    String valueFrom = ss2[0];
                    String valueTo = ss2[1];
                    where.append(" AND ").append(sensor).append(" BETWEEN ");

                    if(sensor.equals("timestamp"))
                        where.append(" '").append(valueFrom).append("' ").append(" AND ").append(" '").append(valueTo).append("' ");
                    else{
                        where.append(valueFrom).append(" AND ").append(valueTo).append(" ");
                    }
                }
                else {
                    if(sensor.equals("timestamp"))
                        where.append(" AND ").append(sensor).append(" ").append(operator.getSymbol()).append(" '").append(filter.getThresholdValues().get(sensor)).append("' ");
                    else
                        where.append(" AND ").append(sensor).append(" ").append(operator.getSymbol()).append(" ").append(filter.getThresholdValues().get(sensor)).append(" ");
                }
            }
        }
        if(filter.getInputSearch() != null && !filter.getInputSearch().equals("")) {
            if(DataTypeUltil.isTimestamp(filter.getInputSearch(), "yyyy-MM-dd HH:mm:ss")) {
                where.append(" AND timestamp = '").append(filter.getInputSearch()).append("' ");
            }
            else {
                where.append("AND (id = '").append(filter.getInputSearch()).append("' OR temperature = '").append(filter.getInputSearch()).append("' OR humidity = '").append(filter.getInputSearch())
                        .append("' OR light_level = '").append(filter.getInputSearch()).append("') ");
            }
        }
        return repo.findByFilter(where.toString(), pageable);
    }
}
