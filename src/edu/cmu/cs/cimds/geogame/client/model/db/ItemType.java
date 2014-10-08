/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;


/**
 *
 * @author ajuarez
 */
@Entity
@Table(name="item_type")
public class ItemType extends PersistentEntity {

	private static final long serialVersionUID = -4124629165887460915L;
	
	private String name;
	private String description;
	private String iconFilename;
	private int basePrice;
	private boolean replenishable;
	private List<Synonym> synonyms;
	
	@Column(name="name", nullable=false)
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@Column(name="description", nullable=false)
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	@Column(name="icon_filename", nullable=false)
	public String getIconFilename() { return iconFilename; }
	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }
	
	@Column(name="base_price", nullable=false)
	public int getBasePrice() { return basePrice; }
	public void setBasePrice(int basePrice) { this.basePrice = basePrice; }

	@Column(name="replenishable", nullable=false)
	public boolean isReplenishable() { return replenishable; }
	public void setReplenishable(boolean replenishable) { this.replenishable = replenishable; }

	@Override
	public boolean equals(Object o) {
		if(o.getClass()==Item.class) {
			return this.equals(((Item) o).getItemType());
		} else {
			return super.equals(o);
		}
	}
	
	@ManyToMany(targetEntity=edu.cmu.cs.cimds.geogame.client.model.db.Synonym.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(
			name="synonyms_per_item",
			joinColumns = @JoinColumn(name="item_type_id"),
			inverseJoinColumns = @JoinColumn(name="synonym_id"))
	public List<Synonym> getSynonyms() { return this.synonyms; }
	public void setSynonyms (List<Synonym> synonyms) { this.synonyms = synonyms; }
	
	@Transient
	public List<String> getSynonymNames() {
		List<String> synonymNames = new ArrayList<String>();
		
		for (Synonym synonym : this.synonyms) {
			synonymNames.add(synonym.getSynonym());
		}
		return synonymNames;
	}
}