package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folders;
import com.mpsdevelopment.biopotential.server.db.pojo.Patterns;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class PatternsDao  extends GenericDao<Patterns,Long>{

    public Patterns getById(int value) {
        Criteria query = getSession().createCriteria(Patterns.class).setCacheable(false);
        query.add(Restrictions.eq(Patterns.ID_PATTERN, value));
        return (Patterns) query.uniqueResult();
    }
}
