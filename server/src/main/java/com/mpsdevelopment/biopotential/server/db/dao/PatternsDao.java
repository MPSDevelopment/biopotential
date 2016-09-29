package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folders;
import com.mpsdevelopment.biopotential.server.db.pojo.Patterns;
import com.mpsdevelopment.biopotential.server.db.pojo.Visit;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class PatternsDao  extends GenericDao<Patterns,Long>{

    public Patterns getById(int value) {
        Criteria query = getSession().createCriteria(Patterns.class).setCacheable(false);
        query.add(Restrictions.eq(Patterns.ID_PATTERN, value));
        return (Patterns) query.uniqueResult();
    }

    public List<Patterns> getPatterns(Integer pageSize, Integer pageNumber) {
        Criteria query = getSession().createCriteria(Patterns.class).setCacheable(false);
        if (pageSize != null && pageNumber != null) {
            query.setFirstResult(pageNumber * pageSize);
        }
        if (pageSize != null) {
            query.setMaxResults(pageSize);
        }
        return query.list();
    }
}
