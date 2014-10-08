package edu.cmu.cs.cimds.geogame.client.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="item_synonyms")
public class Synonym extends PersistentEntity {

	private static final long serialVersionUID = 1602622232533591244L;

	private String synonym;
	
	@Column(name="synonym", nullable=false)
	public String getSynonym() { return this.synonym; }
	public void setSynonym(String newSynonym) { this.synonym = newSynonym; }
	
}
