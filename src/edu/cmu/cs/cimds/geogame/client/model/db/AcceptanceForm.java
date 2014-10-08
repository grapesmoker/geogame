/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 *
 * @author ajuarez
 */
@Entity
@Table(name="acceptance_form")
public class AcceptanceForm extends PersistentEntity {

	private static final long serialVersionUID = -1952796347438970086L;
	
	private String name;
	private String content;
	private int order;
	private boolean active;
	private String button1Text;
	private String button2Text;
	private List<AcceptedForm> acceptedForms;

	@Column
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	@Column
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	@Column(name="order_num")
	public int getOrder() { return order; }
	public void setOrder(int order) { this.order = order; }

	@Column
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }

	@Column(name="button1_text")
	public String getButton1Text() { return button1Text; }
	public void setButton1Text(String button1Text) { this.button1Text = button1Text; }

	@Column(name="button2_text")
	public String getButton2Text() { return button2Text; }
	public void setButton2Text(String button2Text) { this.button2Text = button2Text; }

	@OneToMany(mappedBy="acceptanceForm", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	public List<AcceptedForm> getAcceptedForms() { return acceptedForms; }
	public void setAcceptedForms(List<AcceptedForm> acceptedForms) { this.acceptedForms = acceptedForms; }
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass()==AcceptedForm.class) {
			return this==((AcceptedForm)o).getAcceptanceForm();
		} else {
			return super.equals(o);
		}
	}
}