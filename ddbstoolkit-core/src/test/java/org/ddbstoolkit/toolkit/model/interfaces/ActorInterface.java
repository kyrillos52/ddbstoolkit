package org.ddbstoolkit.toolkit.model.interfaces;

import org.ddbstoolkit.toolkit.core.IEntity;


/**
 * Entity representing an actor
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public interface ActorInterface extends IEntity {

	public Integer getActorId();
	
	public void setActorId(Integer actorId);
	
	public String getActorName();
	
	public void setActorName(String actorName);
	
	public Integer getFilmId();
	
	public void setFilmId(Integer filmId);
}
