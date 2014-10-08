package edu.cmu.cs.cimds.geogame.client.model.db;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@MappedSuperclass
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public abstract class PersistentEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = -1908722029165619482L;
	
	private Long id;
	private Date created;
//	private Date modified;

	protected PersistentEntity() {
		this.created = new Date();
	}

	// this cannot be made unique since it is only unique per table
	@Id @GeneratedValue
	@Column(name="id", nullable=false, updatable=false)
	public Long getId() { return id; }

	// for use by Hibernate only
	@SuppressWarnings("unused")
	private void setId(Long id) { this.id = id; }

	@Column(nullable=false, updatable=false)
	public Date getCreated() { return created; }

	// for use by Hibernate only
	@SuppressWarnings("unused")
	private void setCreated(Date created) { this.created = created; }
//
//	@Column(insertable=false, updatable=true)
//	public Date getModified() { return modified; }
//
//	// for use by Hibernate only
//	@SuppressWarnings("unused")
//	private void setModified(Date modified) { this.modified = modified; }
	
	public static class IdComparator<T extends PersistentEntity> implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			return o1.getId().compareTo(o2.getId());
		}
	}
}