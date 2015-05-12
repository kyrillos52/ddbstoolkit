package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * Sparql Class Id Property
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlClassIdProperty extends SparqlClassProperty {

	public SparqlClassIdProperty(boolean isArray, String name, String type,
			DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity,
			Object value, String propertyName, String namespaceName,
			String namespaceURL, boolean uri, boolean optional) {
		super(isArray, name, type, ddbsToolkitSupportedEntity, value, propertyName,
				namespaceName, namespaceURL, uri, optional);
	}

}
