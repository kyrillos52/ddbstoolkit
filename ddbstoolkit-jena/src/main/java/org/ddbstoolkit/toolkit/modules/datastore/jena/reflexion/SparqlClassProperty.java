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
    
	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	public void setNamespaceURL(String namespaceURL) {
		this.namespaceURL = namespaceURL;
	}

	public void setUri(boolean isUri) {
		this.isUri = isUri;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public boolean isPrimitiveArray() {
		return isArray && (ddbsToolkitSupportedEntity.equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY) || ddbsToolkitSupportedEntity.equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY) || ddbsToolkitSupportedEntity.equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY) || ddbsToolkitSupportedEntity.equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY));
	}
}
