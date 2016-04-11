package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.annotations.EntityName;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.model.interfaces.ActorBase;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

@Service(url="http://www.factforge.net/sparql")
@DefaultNamespace(name="fb",url="http://rdf.freebase.com/ns/")
public class ActorDatastore extends ActorBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    @Id
    @EntityName(name="actor_id")
    private Integer actorId;

    @Optional
    @EntityName(name="actor_name")
    private String actorName;

    @Optional
    @EntityName(name="film_id")
    private Integer filmId;
    
	@URI
    private String actor_uri;

	public Integer getActorId() {
		return actorId;
	}

	public void setActorId(Integer actorId) {
		this.actorId = actorId;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	public Integer getFilmId() {
		return filmId;
	}

	public void setFilmId(Integer filmId) {
		this.filmId = filmId;
	}

	public String getActor_uri() {
		return actor_uri;
	}

	public void setActor_uri(String actor_uri) {
		this.actor_uri = actor_uri;
	}

	@Override
	public String toString() {
		return "ActorDatastore [actorId=" + actorId + ", actorName="
				+ actorName + ", filmId=" + filmId + ", actor_uri=" + actor_uri
				+ "]";
	}
}
