package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class PatternsDao  extends GenericDao<Pattern,Long>{

    public Pattern getById(int value) {
        Criteria query = getSession().createCriteria(Pattern.class).setCacheable(false);
        query.add(Restrictions.eq(Pattern.ID_PATTERN, value));
        return (Pattern) query.uniqueResult();
    }

    public List<Pattern> getPatterns(Integer pageSize, Integer pageNumber) {
        Criteria query = getSession().createCriteria(Pattern.class).setCacheable(false);
        if (pageSize != null && pageNumber != null) {
            query.setFirstResult(pageNumber * pageSize);
        }
        if (pageSize != null) {
            query.setMaxResults(pageSize);
        }
        return query.list();
    }



}
