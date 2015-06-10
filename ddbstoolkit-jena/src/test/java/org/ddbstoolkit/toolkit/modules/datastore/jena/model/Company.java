package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Optional;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

/**
 * Entity representing a company
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
@DefaultNamespace(name="business",url="http://cyril-grandjean.co.uk/business/")
public class Company extends DistributedEntity {

    @URI
    public String company_uri;

    @Id
    public int company_ID;

    public String company_name;

    @Optional
    public String[] boss;

    public int[] mark;

    public long[] number1;

    public float[] number2;

    public int surface;

    public long longField;

    public float floatField;

    public Employee[] employee;
}
