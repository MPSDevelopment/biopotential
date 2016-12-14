package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Pattern;
import com.mpsdevelopment.biopotential.server.db.pojo.PatternsFolders;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class PatternsFoldersDao extends GenericDao<PatternsFolders,Long> {

    public PatternsFolders getByPattern(Pattern pattern) {
        Criteria query = getSession().createCriteria(PatternsFolders.class).setCacheable(false);
        query.add(Restrictions.eq(PatternsFolders.PATTERNS, pattern));
        PatternsFolders patternsFolders = (PatternsFolders) query.uniqueResult();
        if (!(patternsFolders == null)) {return patternsFolders;}
        else return new PatternsFolders();
    }
}
