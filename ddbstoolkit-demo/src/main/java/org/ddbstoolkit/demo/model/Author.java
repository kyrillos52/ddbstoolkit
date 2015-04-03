package org.ddbstoolkit.demo.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.Id;
import org.ddbstoolkit.toolkit.modules.datastore.jena.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.URI;

/**
 * Class representing an author
 * User: Cyril GRANDJEAN
 * Date: 26/06/2012
 * Time: 11:09
 *
 * @version Creation of the class
 */
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


