package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.Id;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

/**
 * Class representing an author
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
@Service(url="http://www.factforge.net/sparql")
@DefaultNamespace(name="fb",url="http://rdf.freebase.com/ns/")
public class Author extends DistributedEntity {

    @Id
    @Optional
    public int author_id;

    @URI
    @Namespace(name = "dbp-ont", url = "http://dbpedia.org/ontology/")
    public String author_uri;

    @Namespace(name = "foaf", url = "http://xmlns.com/foaf/0.1/")
    public String name;

    @Optional
    public int book_id;
}


