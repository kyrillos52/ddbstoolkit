package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

/**
 * Entity representing a film
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
@Service(url="http://data.linkedmdb.org/sparql")
@DefaultNamespace(name="movie",url="http://data.linkedmdb.org/resource/movie/")
public class Film implements IEntity {

    @Id
    public int filmid;

    @URI
    public String film_uri;

    @Namespace(name="dc",url="http://purl.org/dc/terms/")
    public String title;

    public int runtime;

    public Actor[] actor;
}
