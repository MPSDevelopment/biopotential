package com.mpsdevelopment.biopotential.server.db.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mpsdevelopment.biopotential.server.db.pojo.User;

import java.util.List;

public class UserDao extends GenericDao<User, Long> {

    public User getByLogin(String value) {
        Criteria query = getSession().createCriteria(User.class).setCacheable(false);
        query.add(Restrictions.eq(User.LOGIN_FIELD, value));
        return (User) query.uniqueResult();
    }

    public User getByLoginAndPassword(String login, String password) {
        Criteria query = getSession().createCriteria(User.class).setCacheable(false);
        query.add(Restrictions.eq(User.LOGIN_FIELD, login));
        query.add(Restrictions.eq(User.PASSWORD_FIELD, password));
        return (User) query.uniqueResult();
    }

    public Long getAllCount() {
        Criteria query = getSession().createCriteria(User.class).setCacheable(false);
        query.setProjection(Projections.rowCount());
        return (Long) query.uniqueResult();
    }

    public List<User> getUsers(Integer pageSize, Integer pageNumber) {
        Criteria query = getSession().createCriteria(User.class).setCacheable(false);
        if (pageSize != null && pageNumber != null) {
            query.setFirstResult(pageNumber * pageSize);
        }
        if (pageSize != null) {
            query.setMaxResults(pageSize);
        }
        return query.list();
    }
}
