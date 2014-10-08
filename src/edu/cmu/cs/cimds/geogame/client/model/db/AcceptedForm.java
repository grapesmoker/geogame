package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author ajuarez
 */
@Entity
@Table(name="forms_per_user")
public class AcceptedForm extends PersistentEntity {

	private static final long serialVersionUID = -839968041597647124L;
	
	private AcceptanceForm acceptanceForm;
	private User user;
	private String resultString;
	private Boolean success;
	private Date timestamp;

	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name="form_id")
	public AcceptanceForm getAcceptanceForm() { return acceptanceForm; }
	public void setAcceptanceForm(AcceptanceForm acceptanceForm) { this.acceptanceForm = acceptanceForm; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	
	@Column(name="timestamp")
	public Date getTimestamp() { return timestamp; }
	public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

	@Column(name="result_string")
	public String getResultString() { return resultString; }
	public void setResultString(String resultString) { this.resultString = resultString; }
	
	@Column(name="is_success")
	public Boolean isSuccess() { return success; }
	public void setSuccess(Boolean success) { this.success = success; }
}