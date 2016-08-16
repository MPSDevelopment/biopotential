package com.mpsdevelopment.biopotential.server.db.dao;

import com.mpsdevelopment.biopotential.server.db.pojo.Folders;
import com.mpsdevelopment.biopotential.server.db.pojo.Patterns;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;


public class FoldersDao extends GenericDao<Folders, Long> {

    public List<Patterns> getAllPatternsByFolder(Folders folder) {
        Criteria query = getSession().createCriteria(Patterns.class).setCacheable(false);
        query.add(Restrictions.eq(Patterns.ID_PATTERN, folder));
        return query.list();
    }

    public Folders getById(int value) {
        Criteria query = getSession().createCriteria(Folders.class).setCacheable(false);
        query.add(Restrictions.eq(Folders.ID_FOLDER, value));
        return (Folders) query.uniqueResult();
    }

    /*public Folders getByName(String value) {
        Criteria query = getSession().createCriteria(Folders.class).setCacheable(false);
        query.add(Restrictions.eq(Folders.ID_FOLDER, value));
        return (Folders) query.uniqueResult();
    }*/

}
