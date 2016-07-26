package com.mpsdevelopment.biopotential.server.db.pojo;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseObject implements Serializable {

	private static final long serialVersionUID = 1380193823299805010L;

	public static final String UUID_GENERATOR = "uuidGenerator";

	public static final String GENERATOR_STRATEGY = "com.mpsdevelopment.biopotential.server.db.UuidGenerator";

	public static final String TEXT_DEFINITION = "text";

	public static final String ID_FIELD = "id";

	public static final String TEXT_FIELD = "text";

	protected boolean isJustCreated = true;

	@Expose
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = UUID_GENERATOR)
	@GenericGenerator(name = UUID_GENERATOR, strategy = GENERATOR_STRATEGY)
	@Column(name = ID_FIELD)
	private Long id;

	public Long getId() {
		return this.id;
	}

	public BaseObject setId(Long id) {
		this.id = id;
		return this;
	}

	public boolean isJustCreated() {
		return isJustCreated;
	}

	public void setJustCreated(boolean isJustCreated) {
		this.isJustCreated = isJustCreated;
	}

	@Override
	public boolean equals(Object object) {
		return object != null && object instanceof BaseObject && ((BaseObject) object).getId() != null && getId() != null && ((BaseObject) object).getId().equals(getId());
	}

	// protected abstract T returnThis();
}
