package org.ddbstoolkit.toolkit.modules.datastore.jena.model;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.Id;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Namespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.URI;

/**
 * Entity representing an employee
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
@DefaultNamespace(name="business",url="http://cyril-grandjean.co.uk/business/")
public class Employee extends DistributedEntity {

    @URI
    public String employee_uri;

    @Id
    private int employee_id;

    @Namespace(name="foaf",url="http://xmlns.com/foaf/0.1/")
    public String name;
}
