package org.ddbstoolkit.toolkit.jdbc.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;

/**
 * Entity representing an actor
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
public class Actor extends DistributedEntity {

    @Id
    public int actor_id;

    public String actor_name;

    public int film_ID;
}
