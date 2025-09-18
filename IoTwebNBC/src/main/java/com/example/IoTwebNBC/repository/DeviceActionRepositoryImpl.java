package com.example.IoTwebNBC.repository;


import com.example.IoTwebNBC.entity.DeviceActionEntity;
import com.example.IoTwebNBC.request.DeviceActionFilterRequest;
import com.example.IoTwebNBC.utils.DataTypeUltil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeviceActionRepositoryImpl implements DeviceActionRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    // no unused fields

    @Override
    public Page<DeviceActionEntity> findByFilter(DeviceActionFilterRequest filter, Pageable pageable) {
        String sql = "SELECT  d.id, d.room, d.device, d.status, d.timestamp FROM iot.deviceaction d WHERE 1=1 ";
        String where = "";
        if(filter.getDevice() != null && !filter.getDevice().equals("")) {
            where += " AND device = '" + filter.getDevice() + "' ";
        }

        if(filter.getStatus() != null && !filter.getStatus().equals("")) {
            where += " AND status = '" + filter.getStatus() + "' ";
        }

        if(filter.getInputSearch() != null && !filter.getInputSearch().equals("")) {
            if(DataTypeUltil.isTimestamp(filter.getInputSearch(), "yyyy-MM-dd HH:mm:ss")) {
                where += " AND timestamp = '" + filter.getInputSearch() + "' " ;
            }
            else {
                where += "AND (device = '" + filter.getInputSearch() + "' OR status = '" + filter.getInputSearch() + "' OR id = '" + filter.getInputSearch() + "' OR id = ' " + filter.getInputSearch() + "') ";
            }
        }

        sql += where + " ORDER BY id DESC ";

        // build count SQL using same WHERE clause so total reflects filters
        String countSql = "SELECT COUNT(id) FROM iot.deviceaction d WHERE 1=1 " + where;
        Query query = em.createNativeQuery(sql, DeviceActionEntity.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
    @SuppressWarnings("unchecked")
    List<DeviceActionEntity> resultList = query.getResultList();

        // Truy vấn tổng số bản ghi (theo filter)
        Query countQuery = em.createNativeQuery(countSql);
        Object countResult = countQuery.getSingleResult();
        long total = 0L;
        if(countResult instanceof Number){
            total = ((Number) countResult).longValue();
        } else {
            try{
                total = Long.parseLong(String.valueOf(countResult));
            }catch(Exception e){
                total = 0L;
            }
        }

    return new PageImpl<DeviceActionEntity>(resultList, pageable, total);
    }
}
