package com.example.IoTwebNBC.repository;

import com.example.IoTwebNBC.entity.DataSensorEntity;
import com.example.IoTwebNBC.request.DataSensorFilterRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DataSensorEntityRepositoryImpl implements DataSensorRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Page<DataSensorEntity> findByFilter(String where, Pageable pageable) {
        String jpql = " SELECT d.id, d.room, d.temperature, d.humidity, d.light_level, d.timestamp FROM datasensor d ";
        jpql += where;
//        jpql += " ORDER BY d.timestamp DESC";

        String countJpql = "SELECT COUNT(id) FROM datasensor d " + where;

        // Truy vấn dữ liệu
        Query query = em.createNativeQuery(jpql, DataSensorEntity.class);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List resultList = query.getResultList();

        // Truy vấn tổng số bản ghi
        Query countQuery = em.createNativeQuery(countJpql);
        Object countResult = countQuery.getSingleResult();
        long total = 0L;
        if (countResult instanceof Number) {
            total = ((Number) countResult).longValue();
        } else {
            try {
                total = Long.parseLong(String.valueOf(countResult));
            } catch (Exception e) {
                total = 0L;
            }
        }

        @SuppressWarnings("unchecked")
        List<DataSensorEntity> typed = (List<DataSensorEntity>) resultList;
        return new PageImpl<DataSensorEntity>(typed, pageable, total);
    }

}
