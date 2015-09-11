package org.ddbstoolkit.toolkit.model.interfaces;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.IEntity;

/**
 * Entity representing a film
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public interface FilmInterface extends IEntity {

	public Integer getFilmID();

	public void setFilmID(Integer filmID);

	public String getFilmName();
	
	public void setFilmName(String filmName);

	public Integer getDuration();

	public void setDuration(Integer duration);
	
	public Timestamp getCreationDate();

	public void setCreationDate(Timestamp creationDate);

	public Long getLongField();

	public void setLongField(Long longField);

	public Float getFloatField();

	public void setFloatField(Float floatField);

	public ActorInterface[] getActors();

	public void setActors(ActorInterface[] actors);
    
}
