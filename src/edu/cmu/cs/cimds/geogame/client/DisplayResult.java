package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;

/**
 * @author ajuarez
 */
public class DisplayResult implements Serializable {

	private static final long serialVersionUID = 1831207439049188989L;

	private AcceptanceFormDTO acceptanceForm;
	private boolean showGame;
	private long peekFormId;
	
	public DisplayResult() {}

	public AcceptanceFormDTO getAcceptanceForm() { return acceptanceForm; }
	public void setAcceptanceForm(AcceptanceFormDTO acceptanceForm) { this.acceptanceForm = acceptanceForm; }

	public boolean isShowGame() { return showGame; }
	public void setShowGame(boolean showGame) { this.showGame = showGame; }

	public long getPeekFormId() { return peekFormId; }
	public void setPeekFormId(long peekFormId) { this.peekFormId = peekFormId; }
}