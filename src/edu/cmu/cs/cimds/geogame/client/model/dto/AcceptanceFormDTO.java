package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.db.AcceptanceForm;

/**
 *
 * @author ajuarez
 */

public class AcceptanceFormDTO implements Serializable {

	private static final long serialVersionUID = 5258923881544085056L;
	
	private long id;
	private int order;
	private boolean active;
	private String button1Text;
	private String button2Text;
	private String name;
	private String content;
	
	public AcceptanceFormDTO() {}
	public AcceptanceFormDTO(AcceptanceForm acceptanceForm) {
		this.id = acceptanceForm.getId();
		this.order = acceptanceForm.getOrder();
		this.active = acceptanceForm.isActive();
		this.button1Text = acceptanceForm.getButton1Text();
		this.button2Text = acceptanceForm.getButton2Text();
		this.name = acceptanceForm.getName();
		this.content = acceptanceForm.getContent();
	}
	
	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public int getOrder() { return order; }
	public void setOrder(int order) { this.order = order; }

	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }

	public String getButton1Text() { return button1Text; }
	public void setButton1Text(String button1Text) { this.button1Text = button1Text; }

	public String getButton2Text() { return button2Text; }
	public void setButton2Text(String button2Text) { this.button2Text = button2Text; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
}