package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import org.hibernate.Criteria;

import java.util.List;

public class VisitDao extends GenericDao<Visit, Long> {

    public List<Visit> getVisits(Integer pageSize, Integer pageNumber) {
        Criteria query = getSession().createCriteria(Visit.class).setCacheable(false);
        if (pageSize != null && pageNumber != null) {
            query.setFirstResult(pageNumber * pageSize);
        }
        if (pageSize != null) {
            query.setMaxResults(pageSize);
        }
        return query.list();
    }

}
