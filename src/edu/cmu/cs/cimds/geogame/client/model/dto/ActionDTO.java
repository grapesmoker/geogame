//package edu.cmu.cs.cimds.geogame.client.model.dto;
//
//import java.io.Serializable;
//import java.util.Date;
//
//import edu.cmu.cs.cimds.geogame.client.model.db.Action;
//import edu.cmu.cs.cimds.geogame.client.model.enums.ActionType;
//
//public class ActionDTO implements Serializable {
//
//	private static final long serialVersionUID = 7273369980900197502L;
//	
//	private ActionType actionType;
//	private UserDTO user;
//	private Date timeAction;
//	private UserDTO destinationUser;
//	private LocationDTO sourceLocation;
//	private LocationDTO destinationLocation;
//	private ItemDTO item;
//	private ItemTypeDTO itemType;
//
//	public ActionDTO(Action action) {
//		this.actionType = action.getActionType();
//		if(action.getUser()!=null) {
//			this.user = new UserDTO(action.getUser(),true);
//		}
//		this.timeAction = action.getTimeAction();
//		if(action.getDestinationUser()!=null) {
//			this.destinationUser = new UserDTO(action.getDestinationUser(),true);
//		}
//		if(action.getSourceLocation()!=null) {
//			this.sourceLocation = new LocationDTO(action.getSourceLocation());
//		}
//		if(action.getDestinationLocation()!=null) {
//			this.destinationLocation = new LocationDTO(action.getDestinationLocation());
//		}
//		if(action.getItem()!=null) {
//			this.item = new ItemDTO(action.getItem());
//		}
//		if(action.getItemType()!=null) {
//			this.itemType = new ItemTypeDTO(action.getItemType());
//		}
//	}
//
//	public ActionType getActionType() { return actionType; }
//	public void setActionType(ActionType actionType) { this.actionType = actionType; }
//
//	public UserDTO getUser() { return user; }
//	public void setUser(UserDTO user) { this.user = user; }
//
//	public Date getTimeAction() { return timeAction; }
//	public void setTimeAction(Date timeAction) { this.timeAction = timeAction; }
//
//	public UserDTO getDestinationUser() { return destinationUser; }
//	public void setDestinationUser(UserDTO destinationUser) { this.destinationUser = destinationUser; }
//
//	public LocationDTO getSourceLocation() { return sourceLocation; }
//	public void setSourceLocation(LocationDTO sourceLocation) { this.sourceLocation = sourceLocation; }
//
//	public LocationDTO getDestinationLocation() { return destinationLocation; }
//	public void setDestinationLocation(LocationDTO destinationLocation) { this.destinationLocation = destinationLocation; }
//
//	public ItemDTO getItem() { return item; }
//	public void setItem(ItemDTO item) { this.item = item; }
//
//	public ItemTypeDTO getItemType() {  return itemType; }
//	public void setItemType(ItemTypeDTO itemType) { this.itemType = itemType; }
//}