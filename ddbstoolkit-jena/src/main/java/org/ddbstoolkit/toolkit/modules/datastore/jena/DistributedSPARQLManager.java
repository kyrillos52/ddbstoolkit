package org.ddbstoolkit.toolkit.modules.datastore.jena;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.DefaultNamespace;
import org.ddbstoolkit.toolkit.modules.datastore.jena.annotation.Service;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassIdProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassInspector;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlDDBSEntity;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlDDBSToolkitSupportedEntity;

/**
 * Class representing a distributed RDF triple store
 * 
 * @version 1.0 : Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as
 *          "fb:book.book.characters"
 */
public class DistributedSPARQLManager implements DistributableEntityManager {

	/**
	 * Model used by Jena
	 */
	private Dataset myDataset;

	/**
	 * Peer of the data source
	 */
	private Peer myPeer;

	/**
	 * Path of the Jena dataset
	 */
	private String pathDataset;

	/**
	 * Indicate if the model is open
	 */
	private boolean isOpen = false;

	/**
	 * Default constructor used when using SPARQL to query remote endpoints
	 */
	public DistributedSPARQLManager() {
	}

	/**
	 * Constructor when the peer is used to manage data
	 * 
	 * @param datasetPath
	 *            Path of the Jena data sources folder
	 */
	public DistributedSPARQLManager(String datasetPath) {
		this.pathDataset = datasetPath;
	}

	/**
	 * Constructor when the peer is used to manage data
	 * 
	 * @param myPeer
	 *            Peer to connect
	 * @param datasetPath
	 *            Path of the Jena data source folder
	 */
	public DistributedSPARQLManager(Peer myPeer, String datasetPath) {
		this.myPeer = myPeer;
		this.pathDataset = datasetPath;
	}

	@Override
	public void setPeer(Peer myPeer) {
		this.myPeer = myPeer;
	}

	@Override
	public Peer getPeer() {
		return this.myPeer;
	}

	@Override
	public boolean isOpen() {
		return this.isOpen;
	}

	@Override
	public void open() throws DDBSToolkitException {

		if (pathDataset != null) {
			myDataset = TDBFactory.createDataset(pathDataset);
		}
		isOpen = true;
	}

	@Override
	public void close() {

		if (pathDataset != null) {
			myDataset.close();
		}
		isOpen = false;
	}

	/**
	 * Test database connection
	 * 
	 * @throws DDBSToolkitException
	 */
	private <T extends IEntity> void testConnection(T object)
			throws DDBSToolkitException {
		if (!isOpen) {
			throw new DDBSToolkitException(
					"The database connection is not opened");
		}
		if (object == null) {
			throw new IllegalArgumentException(
					"The object passed in parameter is null");
		}
	}

	/**
	 * Get the SPARQL variable corresponding to an object
	 * 
	 * @param object
	 *            Object to inspect
	 * @return variable name in SPARQL
	 */
	public static String getObjectVariable(IEntity object) {

		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(object);

		return sparqlEntity.getObjectVariable(object);
	}

	@Override
	public <T extends IEntity> List<T> listAll(T object,
			List<String> conditionList, String orderBy)
			throws DDBSToolkitException {

		testConnection(object);

		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(object);

		StringBuilder sparqlHeader = new StringBuilder();
		Set<String> listHeaders = new HashSet<String>();

		List<SparqlClassProperty> sparqlClassProperties = sparqlEntity
				.getSupportedPrimaryTypeEntityPropertiesWithoutURI();

		for (SparqlClassProperty sparqlClassProperty : sparqlClassProperties) {

			String header = "prefix " + sparqlClassProperty.getNamespaceName()
					+ ": <" + sparqlClassProperty.getNamespaceURL() + ">\n";

			if (!listHeaders.contains(header)) {
				sparqlHeader.append(header);
			}
			listHeaders.add(header);
		}

		StringBuilder sparqlSelect = new StringBuilder();
		StringBuilder sparqlWhere = new StringBuilder();

		String subject = sparqlEntity.getObjectVariable(object);

		sparqlSelect.append("SELECT ");
		sparqlSelect.append(subject);

		Iterator<SparqlClassProperty> iteratorProperties = sparqlClassProperties
				.iterator();

		// Create the prefixes
		while (iteratorProperties.hasNext()) {

			SparqlClassProperty sparqlClassProperty = iteratorProperties.next();

			sparqlSelect.append(" ?");
			sparqlSelect.append(sparqlClassProperty.getName());

			// If the field is optional
			if (sparqlClassProperty.isOptional()
					|| sparqlClassProperty.isArray()) {
				sparqlWhere.append("OPTIONAL { ");
			}
			sparqlWhere.append(subject);
			sparqlWhere.append(" ");
			sparqlWhere.append(sparqlClassProperty.getNamespaceName());
			sparqlWhere.append(":");
			sparqlWhere.append(sparqlClassProperty.getPropertyName());

			sparqlWhere.append(" ?");
			sparqlWhere.append(sparqlClassProperty.getName());
			// If the field is optional
			if (sparqlClassProperty.isOptional()
					|| sparqlClassProperty.isArray()) {
				sparqlWhere.append("} ");
			}

			if (iteratorProperties.hasNext()) {
				sparqlWhere.append(".\n");
			}
		}

		StringBuilder sparqlRequest = new StringBuilder();
		sparqlRequest.append(sparqlHeader);
		sparqlRequest.append(sparqlSelect);
		sparqlRequest.append(" WHERE { ");
		sparqlRequest.append(sparqlWhere);
		if (conditionList != null && !conditionList.isEmpty()) {
			sparqlRequest.append(".\n");

			for (int i = 0; i < conditionList.size(); i++) {
				sparqlRequest.append(conditionList.get(i));

				if (i < conditionList.size() - 1) {
					sparqlRequest.append(".\n");
				}
			}

		}
		sparqlRequest.append("} ");
		if (orderBy != null) {
			String[] list = orderBy.split(" ");
			if (list.length == 2) {
				String comparatorField = list[0];
				String orderByOrder = list[1];

				sparqlRequest.append("ORDER BY " + orderByOrder + "(?"
						+ comparatorField + ")");
			}

		}

		Query query = QueryFactory.create(sparqlRequest.toString());

		String serviceUrl = "";
		Annotation[] annotations = object.getClass().getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Service) {
				Service myService = (Service) annotation;
				serviceUrl = myService.url();
			}
		}

		ResultSet results = null;
		// Use of remote endpoints
		if (!serviceUrl.isEmpty()) {
			QueryExecution qe = QueryExecutionFactory.sparqlService(serviceUrl,
					query);
			results = qe.execSelect();
			List<T> resultList = conversionResultSet(results, object);
			qe.close();

			return resultList;
		}
		// Use of local datastore
		else {
			myDataset.begin(ReadWrite.READ);

			try {
				QueryExecution qe = QueryExecutionFactory.create(query,
						myDataset.getDefaultModel());
				results = qe.execSelect();

				List<T> resultList = conversionResultSet(results, object);
				qe.close();

				return resultList;
			} finally {
				myDataset.end();
			}

		}
	}

	@Override
	public <T extends IEntity> T read(T object) throws DDBSToolkitException {
		return object;

		/*
		 * if (object != null && (isOpen || pathDataset == null)) { // Explore
		 * the properties of objects List<SparqlClassProperty> listProperties =
		 * SparqlClassInspector
		 * .getClassInspector().explorePropertiesForSPARQL(object);
		 * 
		 * ArrayList<SparqlClassProperty> propertyToRemove = new
		 * ArrayList<SparqlClassProperty>(); String uri = null;
		 * 
		 * StringBuilder sparqlHeader = new StringBuilder(); ArrayList<String>
		 * listHeaders = new ArrayList<String>();
		 * 
		 * for (SparqlClassProperty sparqlClassProperty : listProperties) {
		 * 
		 * // URI is not inside the request if (sparqlClassProperty.isUri()) {
		 * propertyToRemove.add(sparqlClassProperty); uri = (String)
		 * sparqlClassProperty.getValue(); } // Non SPARQL Types are ignored if
		 * (sparqlClassProperty.isArray() &&
		 * !SparqlClassInspector.getClassInspector()
		 * .isSparqlType(sparqlClassProperty)) {
		 * propertyToRemove.add(sparqlClassProperty); } String header =
		 * "prefix " + sparqlClassProperty.getNamespaceName() + ": <" +
		 * sparqlClassProperty.getNamespaceURL() + ">\n";
		 * 
		 * // Avoid duplication of headers if (!listHeaders.contains(header)) {
		 * sparqlHeader.append(header); listHeaders.add(header); } }
		 * 
		 * for (SparqlClassProperty sparqlClassProperties : propertyToRemove) {
		 * listProperties.remove(sparqlClassProperties); }
		 * 
		 * StringBuilder sparqlSelect = new StringBuilder(); StringBuilder
		 * sparqlWhere = new StringBuilder();
		 * 
		 * sparqlSelect.append("SELECT ");
		 * 
		 * // Create the prefixes for (int i = 0; i < listProperties.size();
		 * i++) {
		 * 
		 * SparqlClassProperty sparqlClassProperty = listProperties.get(i);
		 * 
		 * sparqlSelect.append(" ?");
		 * sparqlSelect.append(sparqlClassProperty.getName());
		 * 
		 * // If the field is optional if (sparqlClassProperty.isOptional()) {
		 * sparqlWhere.append("OPTIONAL { "); }
		 * 
		 * sparqlWhere.append("<"); sparqlWhere.append(uri);
		 * sparqlWhere.append("> ");
		 * sparqlWhere.append(sparqlClassProperty.getNamespaceName());
		 * sparqlWhere.append(":");
		 * sparqlWhere.append(sparqlClassProperty.getPropertyName());
		 * sparqlWhere.append(" ?");
		 * sparqlWhere.append(sparqlClassProperty.getName()); // If the field is
		 * optional if (sparqlClassProperty.isOptional()) {
		 * sparqlWhere.append("} "); }
		 * 
		 * if (i < listProperties.size() - 1) { sparqlWhere.append(".\n"); } }
		 * 
		 * StringBuilder sparqlRequest = new StringBuilder();
		 * sparqlRequest.append(sparqlHeader);
		 * sparqlRequest.append(sparqlSelect);
		 * sparqlRequest.append(" WHERE { "); sparqlRequest.append(sparqlWhere);
		 * sparqlRequest.append("}");
		 * 
		 * // System.out.println(sparqlRequest.toString());
		 * 
		 * Query query = QueryFactory.create(sparqlRequest.toString());
		 * 
		 * String serviceUrl = ""; Annotation[] annotations =
		 * object.getClass().getAnnotations(); for (Annotation annotation :
		 * annotations) { if (annotation instanceof Service) { Service myService
		 * = (Service) annotation; serviceUrl = myService.url(); } }
		 * 
		 * ResultSet results = null; if (!serviceUrl.isEmpty()) { QueryExecution
		 * qe = QueryExecutionFactory.sparqlService( serviceUrl, query); results
		 * = qe.execSelect();
		 * 
		 * List<T> resultList = conversionResultSet(results, object);
		 * qe.close(); if (resultList.size() == 1) { return resultList.get(0); }
		 * else { return null; } } else { myDataset.begin(ReadWrite.READ);
		 * 
		 * try { QueryExecution qe = QueryExecutionFactory.create(query,
		 * myDataset.getDefaultModel()); results = qe.execSelect();
		 * 
		 * List<T> resultList = conversionResultSet(results, object); if
		 * (resultList.size() == 1) { qe.close(); return resultList.get(0); }
		 * else { qe.close(); return null; }
		 * 
		 * } finally { myDataset.end(); }
		 * 
		 * } } else { if (!isOpen()) { throw new DDBSToolkitException(
		 * "The database connection is not opened"); } else { throw new
		 * DDBSToolkitException( "The object passed in parameter is null"); } }
		 */
	}

	/**
	 * Last element (Compatible SPARQL 1.0) : Need the Id annotation
	 * 
	 * @param object
	 *            object to read
	 * @return last element added
	 * @throws Exception
	 */
	@Override
	public <T extends IEntity> T readLastElement(T object)
			throws DDBSToolkitException {
		return object;

		/*
		 * if (object != null && (isOpen || pathDataset == null)) { try {
		 * 
		 * // Explore the properties of objects List<SparqlClassProperty>
		 * listProperties = SparqlClassInspector
		 * .getClassInspector().explorePropertiesForSPARQL(object);
		 * 
		 * StringBuilder sparqlHeader = new StringBuilder(); ArrayList<String>
		 * listHeaders = new ArrayList<String>();
		 * 
		 * listHeaders .add("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		 * sparqlHeader
		 * .append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		 * 
		 * SparqlClassProperty idProperty = null; SparqlClassProperty
		 * uriProperty = null;
		 * 
		 * for (SparqlClassProperty sparqlClassProperty : listProperties) {
		 * 
		 * if (sparqlClassProperty instanceof SparqlClassIdProperty) {
		 * idProperty = sparqlClassProperty; } if (sparqlClassProperty.isUri())
		 * { uriProperty = sparqlClassProperty; } String header = "prefix " +
		 * sparqlClassProperty.getNamespaceName() + ": <" +
		 * sparqlClassProperty.getNamespaceURL() + ">\n";
		 * 
		 * // Avoid duplication of headers if (!listHeaders.contains(header)) {
		 * sparqlHeader.append(header); listHeaders.add(header); } }
		 * 
		 * StringBuilder sparqlRequest = new StringBuilder();
		 * sparqlRequest.append(sparqlHeader); sparqlRequest
		 * .append("SELECT ?element ?entity_id WHERE { ?element ");
		 * sparqlRequest.append(idProperty.getNamespaceName());
		 * sparqlRequest.append(":");
		 * sparqlRequest.append(idProperty.getPropertyName()); sparqlRequest
		 * .append(" ?entity_id } ORDER BY DESC(?entity_id) LIMIT 1");
		 * 
		 * // System.out.println(sparqlRequest.toString());
		 * 
		 * Query query = QueryFactory .create(
		 * "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> prefix business: <http://cyril-grandjean.co.uk/business/> SELECT ?element ?entity_id WHERE { ?element business:company_ID ?entity_id } ORDER BY DESC(?entity_id) LIMIT 1"
		 * );
		 * 
		 * String serviceUrl = ""; Annotation[] annotations =
		 * object.getClass().getAnnotations(); for (Annotation annotation :
		 * annotations) { if (annotation instanceof Service) { Service myService
		 * = (Service) annotation; serviceUrl = myService.url(); } }
		 * 
		 * String lastUri = null;
		 * 
		 * ResultSet results = null; if (!serviceUrl.isEmpty()) { QueryExecution
		 * qe = QueryExecutionFactory.sparqlService( serviceUrl, query); results
		 * = qe.execSelect();
		 * 
		 * // For each object while (results.hasNext()) {
		 * 
		 * QuerySolution myResult = results.next();
		 * 
		 * lastUri = myResult.get("element").toString(); }
		 * 
		 * qe.close(); } else { myDataset.begin(ReadWrite.READ);
		 * 
		 * try { QueryExecution qe = QueryExecutionFactory.create(query,
		 * myDataset.getDefaultModel()); results = qe.execSelect();
		 * 
		 * // For each object while (results.hasNext()) {
		 * 
		 * QuerySolution myResult = results.next();
		 * 
		 * lastUri = myResult.get("element").toString(); }
		 * 
		 * qe.close(); } finally { myDataset.end(); }
		 * 
		 * }
		 * 
		 * if (lastUri != null) {
		 * 
		 * Field f = object.getClass().getField(uriProperty.getName());
		 * 
		 * f.set(object, lastUri);
		 * 
		 * return read(object); } else { return null; } } catch
		 * (IllegalAccessException iae) { throw new DDBSToolkitException(
		 * "Illegal access exception using reflection", iae); } catch
		 * (NoSuchFieldException nsfe) { throw new DDBSToolkitException(
		 * "No such field exception using reflection", nsfe); } } else { if
		 * (!isOpen()) { throw new DDBSToolkitException(
		 * "The database connection is not opened"); } else { throw new
		 * DDBSToolkitException( "The object passed in parameter is null"); } }
		 */
	}

	@Override
	public boolean add(IEntity objectToAdd) throws DDBSToolkitException {
		return isOpen;

		/*
		 * // Connection must be opened and object non null if (isOpen &&
		 * objectToAdd != null) {
		 * 
		 * // Start a writing transaction myDataset.begin(ReadWrite.WRITE); try
		 * {
		 * 
		 * // Get the model Model myModel = myDataset.getDefaultModel();
		 * 
		 * // Explore the properties of the object to add
		 * List<SparqlClassProperty> listProperties = SparqlClassInspector
		 * .getClassInspector().explorePropertiesForSPARQL( objectToAdd);
		 * 
		 * // Get the URI String uri = ""; for (SparqlClassProperty
		 * sparqlClassProperty : listProperties) { if
		 * (sparqlClassProperty.isUri()) { uri = (String)
		 * sparqlClassProperty.getValue(); break; } }
		 * 
		 * // If there is an URI if (!uri.equals("")) {
		 * 
		 * Annotation[] classAnnotations = objectToAdd.getClass()
		 * .getAnnotations();
		 * 
		 * // Get the default namespace String defaultNamespaceUrl = ""; for
		 * (Annotation annotation : classAnnotations) { if (annotation
		 * instanceof DefaultNamespace) { DefaultNamespace ns =
		 * (DefaultNamespace) annotation;
		 * 
		 * defaultNamespaceUrl = ns.url(); } }
		 * 
		 * Resource resourceToAdd = myModel.createResource(uri);
		 * 
		 * myModel.add( resourceToAdd, RDF.type,
		 * myModel.createResource(defaultNamespaceUrl +
		 * objectToAdd.getClass().getSimpleName()));
		 * 
		 * for (SparqlClassProperty sparqlClassProperty : listProperties) {
		 * 
		 * // System.out.println("Property name "+sparqlClassProperty.getName()+
		 * " Property type "+sparqlClassProperty.getType());
		 * 
		 * // If it's not the URI or an array or the isPeerUid if
		 * (!sparqlClassProperty.isUri() && !sparqlClassProperty.isArray()) { if
		 * (sparqlClassProperty.getType().equals("int")) {
		 * myModel.add(myModel.createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()),
		 * sparqlClassProperty.getValue())); } else if
		 * (sparqlClassProperty.getType().equals( "long")) {
		 * myModel.add(myModel.createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()),
		 * sparqlClassProperty.getValue())); } else if
		 * (sparqlClassProperty.getType().equals( "float")) {
		 * myModel.add(myModel.createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()),
		 * sparqlClassProperty.getValue())); } else if
		 * (sparqlClassProperty.getType().equals( "java.lang.String")) { String
		 * value; if (sparqlClassProperty.getValue() == null) { value = ""; }
		 * else { value = (String) sparqlClassProperty .getValue(); }
		 * myModel.add(myModel.createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()), value)); } } // Array add
		 * the node if (sparqlClassProperty.isArray()) { // Primitives types +
		 * Strings if (SparqlClassInspector.getClassInspector()
		 * .isSparqlType(sparqlClassProperty)) { String property =
		 * sparqlClassProperty.getType();
		 * 
		 * Field arrayField = objectToAdd .getClass()
		 * .getField(sparqlClassProperty.getName());
		 * 
		 * if (arrayField.get(objectToAdd) != null) { // Integer array if
		 * (property.equals("[I")) { int[] array = (int[]) arrayField
		 * .get(objectToAdd);
		 * 
		 * for (int i = 0; i < array.length; i++) { myModel.add(myModel
		 * .createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()), array[i])); }
		 * 
		 * } // Long array else if (property.equals("[J")) { long[] array =
		 * (long[]) arrayField .get(objectToAdd);
		 * 
		 * for (int i = 0; i < array.length; i++) { myModel.add(myModel
		 * .createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()), array[i])); } } // Float
		 * array else if (property.equals("[F")) { float[] array = (float[])
		 * arrayField .get(objectToAdd);
		 * 
		 * for (int i = 0; i < array.length; i++) { myModel.add(myModel
		 * .createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()), array[i])); } } // String
		 * array else if (property .equals("[Ljava.lang.String;")) { String[]
		 * array = (String[]) arrayField .get(objectToAdd);
		 * 
		 * for (int i = 0; i < array.length; i++) { myModel.add(myModel
		 * .createLiteralStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()), array[i])); } } }
		 * 
		 * } // IEntity types else { Field arrayField = objectToAdd .getClass()
		 * .getField(sparqlClassProperty.getName());
		 * 
		 * IEntity[] array = (IEntity[]) arrayField .get(objectToAdd); if (array
		 * != null) { int size = Array.getLength(array);
		 * 
		 * for (int i = 0; i < size; i++) { IEntity object = (IEntity)
		 * Array.get( array, i);
		 * 
		 * String uri_field = ""; List<SparqlClassProperty> properties =
		 * SparqlClassInspector .getClassInspector()
		 * .explorePropertiesForSPARQL( object); for (SparqlClassProperty
		 * property : properties) { if (property.isUri()) { uri_field =
		 * property.getName(); break; } }
		 * 
		 * Field fieldLinked = object.getClass() .getField(uri_field); String
		 * uriToLink = (String) fieldLinked .get(object);
		 * 
		 * // System.out.println("URI to link : "+uriToLink);
		 * 
		 * myModel.add(myModel.createStatement( resourceToAdd,
		 * myModel.createProperty(sparqlClassProperty .getNamespaceURL() +
		 * sparqlClassProperty .getPropertyName()),
		 * myModel.createResource(uriToLink))); } } } } }
		 * 
		 * // Commit the transaction myDataset.commit();
		 * 
		 * return true; } else { return false; } } catch (Exception ex) {
		 * 
		 * myDataset.abort();
		 * 
		 * throw new DDBSToolkitException("An exception has occured", ex);
		 * 
		 * } finally { myDataset.end(); } } else {
		 * 
		 * if (!isOpen()) { throw new DDBSToolkitException(
		 * "The database connection is not opened"); } else { throw new
		 * DDBSToolkitException( "The object passed in parameter is null"); } }
		 */
	}

	@Override
	public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {

		delete(objectToUpdate);
		add(objectToUpdate);
		return true;
	}

	@Override
	public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {
		return isOpen;

		/*
		 * if (objectToDelete != null) { // Start a writing transaction
		 * myDataset.begin(ReadWrite.WRITE); try {
		 * 
		 * // Get the model Model myModel = myDataset.getDefaultModel();
		 * 
		 * // Explore the properties of the object to add
		 * List<SparqlClassProperty> listProperties = SparqlClassInspector
		 * .getClassInspector().explorePropertiesForSPARQL( objectToDelete);
		 * 
		 * // Get the URI String uriValue = null; for (SparqlClassProperty
		 * sparqlClassProperty : listProperties) { if
		 * (sparqlClassProperty.isUri()) { uriValue = (String)
		 * sparqlClassProperty.getValue(); break; } }
		 * 
		 * if (uriValue != null) { // Remove all the triples associated with the
		 * URI myModel.createResource(uriValue).removeAll(null);
		 * 
		 * // Commit the transaction myDataset.commit(); } else { return false;
		 * }
		 * 
		 * return true; } catch (Exception ex) { return false; } finally {
		 * myDataset.end(); }
		 * 
		 * } else { if (!isOpen()) { throw new DDBSToolkitException(
		 * "The database connection is not opened"); } else { throw new
		 * DDBSToolkitException( "The object passed in parameter is null"); } }
		 */
	}

	@Override
	public boolean createEntity(IEntity objectToCreate)
			throws DDBSToolkitException {
		// No Create table statement with Sparql Endpoint
		return true;
	}

	@Override
	public <T extends IEntity> T loadArray(T objectToLoad, String field,
			String orderBy) throws DDBSToolkitException {
		return objectToLoad;

		/*
		 * try { List<SparqlClassProperty> listOfProperties =
		 * SparqlClassInspector .getClassInspector().explorePropertiesForSPARQL(
		 * objectToLoad);
		 * 
		 * SparqlClassProperty linkProperty = null; SparqlClassProperty
		 * uriPropertyObjectToLoad = null;
		 * 
		 * for (SparqlClassProperty property : listOfProperties) { if
		 * (property.getName().equals(field)) { linkProperty = property; } if
		 * (property.isUri()) { uriPropertyObjectToLoad = property; } }
		 * 
		 * String objectName = linkProperty.getType().substring(2,
		 * linkProperty.getType().length() - 1);
		 * 
		 * IEntity objectLinked = (IEntity) Class.forName(objectName)
		 * .newInstance();
		 * 
		 * listOfProperties = SparqlClassInspector.getClassInspector()
		 * .explorePropertiesForSPARQL(objectLinked);
		 * 
		 * Field fieldUri = objectToLoad.getClass().getField(
		 * uriPropertyObjectToLoad.getName());
		 * 
		 * List<String> listCondition = new ArrayList<String>();
		 * listCondition.add("<" + fieldUri.get(objectToLoad) + "> " +
		 * linkProperty.getNamespaceName() + ":" +
		 * linkProperty.getPropertyName() + " " +
		 * getObjectVariable(objectLinked));
		 * 
		 * List<IEntity> listObject = listAll(objectLinked, listCondition,
		 * orderBy);
		 * 
		 * Field f = objectToLoad.getClass().getField(field);
		 * 
		 * Object array = Array.newInstance(Class.forName(objectName),
		 * listObject.size());
		 * 
		 * int i = 0; for (IEntity entity : listObject) { Array.set(array, i,
		 * entity); i++; }
		 * 
		 * f.set(objectToLoad, array);
		 * 
		 * return objectToLoad; } catch (Exception e) { throw new
		 * DDBSToolkitException( "Error during use of the reflection mechanism",
		 * e); }
		 */
	}

	/**
	 * Sparql Result
	 * 
	 * @author Cyril Grandjean
	 * @version 1.0 Class creation
	 */
	private class SparqlResults {

		private Map<String, Map<String, Set<Integer>>> integerArray;

		private Map<String, Map<String, Set<Long>>> longArray;

		private Map<String, Map<String, Set<Float>>> floatArray;

		private Map<String, Map<String, Set<Double>>> doubleArray;

		private Map<String, Map<String, Set<String>>> stringArray;

		public void addInt(String propertyName, String uri,
				Integer integerElement) {
			if (integerArray == null) {
				integerArray = new HashMap<>();
			}
			if (integerArray.get(propertyName) == null) {
				integerArray.put(propertyName,
						new HashMap<String, Set<Integer>>());
			}
			if (integerArray.get(propertyName).get(uri) == null) {
				integerArray.get(propertyName).put(uri,
						new LinkedHashSet<Integer>());
			}
			integerArray.get(propertyName).get(uri).add(integerElement);
		}

		public void addLong(String propertyName, String uri, Long longElement) {
			if (longArray == null) {
				longArray = new HashMap<>();
			}
			if (longArray.get(propertyName) == null) {
				longArray.put(propertyName, new HashMap<String, Set<Long>>());
			}
			if (longArray.get(propertyName).get(uri) == null) {
				longArray.get(propertyName).put(uri, new LinkedHashSet<Long>());
			}
			longArray.get(propertyName).get(uri).add(longElement);
		}

		public void addFloat(String propertyName, String uri, Float floatElement) {
			if (floatArray == null) {
				floatArray = new HashMap<>();
			}
			if (floatArray.get(propertyName) == null) {
				floatArray.put(propertyName, new HashMap<String, Set<Float>>());
			}
			if (floatArray.get(propertyName).get(uri) == null) {
				floatArray.get(propertyName).put(uri,
						new LinkedHashSet<Float>());
			}
			floatArray.get(propertyName).get(uri).add(floatElement);
		}

		public void addDouble(String propertyName, String uri,
				Double doubleElement) {
			if (doubleArray == null) {
				doubleArray = new HashMap<>();
			}
			if (doubleArray.get(propertyName) == null) {
				doubleArray.put(propertyName,
						new HashMap<String, Set<Double>>());
			}
			if (doubleArray.get(propertyName).get(uri) == null) {
				doubleArray.get(propertyName).put(uri,
						new LinkedHashSet<Double>());
			}
			doubleArray.get(propertyName).get(uri).add(doubleElement);
		}

		public void addString(String propertyName, String uri,
				String stringElement) {
			if (stringArray == null) {
				stringArray = new HashMap<>();
			}
			if (stringArray.get(propertyName) == null) {
				stringArray.put(propertyName,
						new HashMap<String, Set<String>>());
			}
			if (stringArray.get(propertyName).get(uri) == null) {
				stringArray.get(propertyName).put(uri,
						new LinkedHashSet<String>());
			}
			stringArray.get(propertyName).get(uri).add(stringElement);
		}

		public Set<Integer> getIntegerArray(String propertyName, String uri) {
			return integerArray.get(propertyName).get(uri);
		}

		public Set<Long> getLongArray(String propertyName, String uri) {
			return longArray.get(propertyName).get(uri);
		}

		public Set<Float> getFloatArray(String propertyName, String uri) {
			return floatArray.get(propertyName).get(uri);
		}

		public Set<Double> getDoubleArray(String propertyName, String uri) {
			return doubleArray.get(propertyName).get(uri);
		}

		public Set<String> getStringArray(String propertyName, String uri) {
			return stringArray.get(propertyName).get(uri);
		}
	}

	protected <T extends IEntity> List<T> conversionResultSet(
			ResultSet results, T myObject) throws DDBSToolkitException {

		try {
			List<T> resultList = new ArrayList<T>();

			SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
					.getDDBSEntity(myObject);

			SparqlResults sparqlResults = new SparqlResults();

			while (results.hasNext()) {

				QuerySolution myResult = results.next();

				String uri = myResult.getResource(
						sparqlEntity.getUri().getName()).toString();

				@SuppressWarnings("unchecked")
				T myData = (T) Class.forName(
						ClassInspector.getClassInspector().getFullClassName(
								myObject)).newInstance();

				for (SparqlClassProperty sparqlClassProperty : sparqlEntity
						.getEntityProperties()) {
					Field f = myData.getClass().getField(
							sparqlClassProperty.getName());

					if (myResult.get(sparqlClassProperty.getName()) != null) {

						if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.INTEGER)) {
							f.set(myData,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getInt());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity().equals(
										SparqlDDBSToolkitSupportedEntity.LONG)) {
							f.set(myData,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getLong());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity().equals(
										SparqlDDBSToolkitSupportedEntity.FLOAT)) {
							f.set(myData,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getFloat());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.DOUBLE)) {
							f.set(myData,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getDouble());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.STRING)) {
							String stringResult = myResult.get(
									sparqlClassProperty.getName()).toString();
							stringResult = stringResult
									.replaceAll(
											"\\^\\^http://www.w3.org/2001/XMLSchema#string$",
											"");
							f.set(myData, stringResult);
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY)) {
							sparqlResults.addInt(
									sparqlClassProperty.getName(),
									uri,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getInt());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY)) {
							sparqlResults.addLong(
									sparqlClassProperty.getName(),
									uri,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getLong());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY)) {
							sparqlResults.addFloat(
									sparqlClassProperty.getName(),
									uri,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getFloat());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY)) {
							sparqlResults.addDouble(
									sparqlClassProperty.getName(),
									uri,
									myResult.getLiteral(
											sparqlClassProperty.getName())
											.getDouble());
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.STRING_ARRAY)) {
							String stringResult = myResult.get(
									sparqlClassProperty.getName()).toString();
							stringResult = stringResult
									.replaceAll(
											"\\^\\^http://www.w3.org/2001/XMLSchema#string$",
											"");
							sparqlResults.addString(
									sparqlClassProperty.getName(), uri,
									stringResult);
						}
					}

					if (myData instanceof DistributedEntity
							&& getPeer() != null) {
						((DistributedEntity) myData).setPeerUid(getPeer()
								.getUid());
					}
				}

				resultList.add(myData);
			}

			for (T myData : resultList) {
				String uri = (String) myData.getClass()
						.getField(sparqlEntity.getUri().getName())
						.get(myObject);

				for (SparqlClassProperty sparqlClassProperty : sparqlEntity
						.getEntityProperties()) {
					Field f = myData.getClass().getField(
							sparqlClassProperty.getName());

					if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY)) {
						f.set(myData,
								sparqlResults.getIntegerArray(
										sparqlClassProperty.getName(), uri)
										.toArray());
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY)) {
						f.set(myData,
								sparqlResults.getLongArray(
										sparqlClassProperty.getName(), uri)
										.toArray());
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY)) {
						f.set(myData,
								sparqlResults.getFloatArray(
										sparqlClassProperty.getName(), uri)
										.toArray());
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY)) {
						f.set(myData,
								sparqlResults.getDoubleArray(
										sparqlClassProperty.getName(), uri)
										.toArray());
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.STRING_ARRAY)) {
						f.set(myData,
								sparqlResults.getStringArray(
										sparqlClassProperty.getName(), uri)
										.toArray());
					}
				}

			}

			return resultList;
		} catch (InstantiationException ie) {
			throw new DDBSToolkitException(
					"Problem during instantiation of the object using reflection",
					ie);
		} catch (IllegalAccessException iae) {
			throw new DDBSToolkitException(
					"Illegal access exception using reflection", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found using reflection",
					cnfe);
		} catch (NoSuchFieldException nsfe) {
			throw new DDBSToolkitException(
					"No such field exception using reflection", nsfe);
		}
	}
}
