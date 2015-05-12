package org.ddbstoolkit.toolkit.jdbc.model;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.Id;

/**
 * Entity representing a film
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
public class Film extends DistributedEntity {

    @Id
    public int film_ID;

    public String film_name;

    public int duration;

    public Timestamp creationDate;

    public long longField;

    public float floatField;

    public Actor[] actors;
}
