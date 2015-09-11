package org.ddbstoolkit.toolkit.model.interfaces;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;
import org.ddbstoolkit.toolkit.core.annotations.Id;

/**
 * Entity representing a film
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@EntityName(name="Film")
public class FilmBase extends DistributedEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@EntityName(name="film_id")
    private Integer filmID;

	@EntityName(name="film_name")
    private String filmName;

    private Integer duration;

    private Timestamp creationDate;

    private Long longField;

    private Float floatField;

    private ActorBase[] actors;

	public Integer getFilmID() {
		return filmID;
	}

	public void setFilmID(Integer filmID) {
		this.filmID = filmID;
	}

	public String getFilmName() {
		return filmName;
	}

	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Long getLongField() {
		return longField;
	}

	public void setLongField(Long longField) {
		this.longField = longField;
	}

	public Float getFloatField() {
		return floatField;
	}

	public void setFloatField(Float floatField) {
		this.floatField = floatField;
	}

	public ActorBase[] getActors() {
		return actors;
	}

	public void setActors(ActorBase[] actors) {
		this.actors = actors;
	}
}
