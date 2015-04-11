package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;


/**
 * Information about a property of an object by using SPARQL queries
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class SparqlClassProperty extends DDBSEntityProperty {

    /**
     * Name of the namespace
     */
    private String namespaceName;

    /**
     * URL of the namespace
     */
    private String namespaceURL;

    /**
     * Indicate if it's an URI
     */
    private boolean isUri = false;

    /**
     * Indicate if the field is optional
     */
    private boolean isOptional;

    public SparqlClassProperty(boolean id, boolean array, String name, String type, Object value, String namespaceName, String namespaceURL, boolean uri, boolean optional, String propertyName) {
        super(id, false, array, name, type, value, propertyName);
        this.namespaceName = namespaceName;
        this.namespaceURL = namespaceURL;
        isUri = uri;
        isOptional = optional;
    }

    /**
     * Get the name of the namespace
     * @return Name of the namespace
     */
    public String getNamespaceName() {
        return namespaceName;
    }

    /**
     * Get the URL of the namespace
     * @return URL of the namespace
     */
    public String getNamespaceURL() {
        return namespaceURL;
    }

    /**
     * Indicate if the field is an URI
     * @return Boolean indicating if the field is an URI
     */
    public boolean isUri() {
        return isUri;
    }

    /**
     * Indicate if the field is optional
     * @return Boolean indicating if the field is optional
     */
    public boolean isOptional() {
        return isOptional;
    }
}
