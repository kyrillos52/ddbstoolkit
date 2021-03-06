package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;

/**
 * Class representing a link between a book and a genre
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
public class Link_Book_Genre extends DistributedEntity {

    @Id
    public int link_id;

    public int book_id;

    public int genre_id;
}


