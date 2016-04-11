package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import java.sql.Timestamp;
import java.util.Arrays;

import org.ddbstoolkit.toolkit.core.annotations.EntityName;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.model.interfaces.FilmBase;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

@Service(url="http://data.linkedmdb.org/sparql")
@DefaultNamespace(name="movie",url="http://data.linkedmdb.org/resource/movie/")
public class FilmDatastore extends FilmBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Optional
	@EntityName(name="film_id")
    private Integer filmID;

	@Optional
	@EntityName(name="film_name")
    private String filmName;

	@Optional
    private Integer duration;

	@Optional
    private Timestamp creationDate;

	@Optional
    private Long longField;

	@Optional
    private Float floatField;

    private ActorBase[] actors;
    
    @URI
    private String film_uri;

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

	public String getFilm_uri() {
		return film_uri;
	}

	public void setFilm_uri(String film_uri) {
		this.film_uri = film_uri;
	}

	@Override
	public String toString() {
		return "FilmDatastore [filmID=" + filmID + ", filmName=" + filmName
				+ ", duration=" + duration + ", creationDate=" + creationDate
				+ ", longField=" + longField + ", floatField=" + floatField
				+ ", actors=" + Arrays.toString(actors) + ", film_uri="
				+ film_uri + "]";
	}
	
}
