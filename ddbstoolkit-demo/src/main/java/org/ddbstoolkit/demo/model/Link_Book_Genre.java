package org.ddbstoolkit.demo.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.Id;

/**
 * Class representing a link between a book and a genre
 * User: Cyril GRANDJEAN
 * Date: 27/06/2012
 * Time: 09:25
 *
 * @version Creation of the class
 */
public class Link_Book_Genre extends DistributedEntity {

    @Id
    public int link_id;

    public int book_id;

    public int genre_id;
}


