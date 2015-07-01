package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;


/**
 * Class representing a book
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
@Service(url="http://www.factforge.net/sparql")
@DefaultNamespace(name="fb",url="http://rdf.freebase.com/ns/")
public class Book extends DistributedEntity {

    @Id
    @Optional
    public int book_id;

    @URI
    public String book_uri;

    @Namespace(name = "rdfs", url = "http://www.w3.org/2000/01/rdf-schema#")
    @EntityName(name = "label")
    public String title;

    @Namespace(name = "rdfs", url = "http://www.w3.org/2000/01/rdf-schema#")
    @EntityName(name = "comment")
    public String summary;

    @Namespace(name = "dbp-ont", url = "http://dbpedia.org/ontology/")
    public Author[] author;

    @Namespace(name = "fb", url = "http://rdf.freebase.com/ns/")
    @EntityName(name = "book.book.genre")
    public Genre[] genre;

    @Namespace(name = "fb", url = "http://rdf.freebase.com/ns/")
    @EntityName(name = "book.book.characters")
    public Character[] character;

    @Optional
    public Link_Book_Genre[] linkedGenre;
}


