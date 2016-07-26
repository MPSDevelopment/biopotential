package com.mpsdevelopment.biopotential.server.db;

import com.mpsdevelopment.plasticine.commons.IdGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class UuidGenerator implements IdentifierGenerator {

	/**
	 * This method will generate a random number and return it, hibernate can use this id as it generator class id.
	 */
	// @Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		return IdGenerator.nextId();
	}
}
