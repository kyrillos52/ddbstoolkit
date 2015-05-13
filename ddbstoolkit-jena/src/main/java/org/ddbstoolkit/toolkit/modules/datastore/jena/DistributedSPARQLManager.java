package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassIdProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlDDBSEntity;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlDDBSToolkitSupportedEntity;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.RDF;

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

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(object);

		return sparqlEntity.getObjectVariable(object);
	}

	/**
	 * Get a SPARQL String request
	 * 
	 * @param object
	 *            Object
	 * @param conditionList
	 *            List of conditions
	 * @param orderBy
	 *            Order by element
	 * @param sparqlEntity
	 *            SPARQL Entity
	 * @return
	 */
	private <T extends IEntity> String getSparqlRequest(T object,
			List<String> conditionList, String orderBy,
			@SuppressWarnings("rawtypes") SparqlDDBSEntity sparqlEntity, Set<String> additionalHeaders) {

		StringBuilder sparqlHeader = new StringBuilder();
		Set<String> listHeaders = new HashSet<String>();

		@SuppressWarnings("unchecked")
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
		if(additionalHeaders != null)
		{
			for(String additionalHeader : additionalHeaders)
			{
				if (!listHeaders.contains(additionalHeader)) {
					sparqlHeader.append(additionalHeader);
				}
				listHeaders.add(additionalHeader);
			}
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
		return sparqlRequest.toString();
	}

	/**
	 * Add element in datastore
	 * 
	 * @param myModel
	 *            Datastore model
	 * @param resourceToAdd
	 *            Datastore resource
	 * @param sparqlClassProperty
	 *            Sparql property
	 * @param elementToAdd
	 *            Element to add
	 */
	private void addElement(Model myModel, Resource resourceToAdd,
			SparqlClassProperty sparqlClassProperty, Object elementToAdd) {

		if (elementToAdd != null) {
			myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel
					.createProperty(sparqlClassProperty.getNamespaceURL()
							+ sparqlClassProperty.getPropertyName()),
					elementToAdd));
		}
	}
	
	private <T extends IEntity> List<T> listAll(T object,
			List<String> conditionList, String orderBy, Set<String> additionalHeaders)
			throws DDBSToolkitException {
		
		testConnection(object);

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(object);

		Query query = QueryFactory.create(getSparqlRequest(object,
				conditionList, orderBy, sparqlEntity, additionalHeaders));

		String serviceUrl = sparqlEntity.getServiceUrl();

		ResultSet results = null;

		// Use of remote endpoints
		if (serviceUrl != null && !serviceUrl.isEmpty() && myDataset == null) {

			try (QueryExecution qe = QueryExecutionFactory.sparqlService(
					serviceUrl, query)) {
				results = qe.execSelect();
				return conversionResultSet(results, object);
			}
		}
		// Use of local datastore
		else {
			myDataset.begin(ReadWrite.READ);

			try (QueryExecution qe = QueryExecutionFactory.create(query,
					myDataset.getDefaultModel())) {
				results = qe.execSelect();

				return conversionResultSet(results, object);

			} finally {
				myDataset.end();
			}

		}
		
	}

	@Override
	public <T extends IEntity> List<T> listAll(T object,
			List<String> conditionList, String orderBy)
			throws DDBSToolkitException {

		return listAll(object, conditionList, orderBy, null);
	}

	@Override
	public <T extends IEntity> T read(T object) throws DDBSToolkitException {

		testConnection(object);

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(object);
		
		List<SparqlClassProperty> otherEntities = sparqlEntity.getSupportedPrimaryTypeEntityPropertiesWithoutURI();

		if(otherEntities.size() > 0)
		{
			List<String> conditionList = new ArrayList<>();
			SparqlClassProperty uriProperty = sparqlEntity.getUri();
			conditionList.add("<" + uriProperty.getValue()+"> "
					+ otherEntities.get(0).getNamespaceName() + ":"
					+ otherEntities.get(0).getPropertyName() + " ?" + otherEntities.get(0).getName());

			List<T> results = listAll(object, conditionList, null);
			if (results.size() == 1) {
				return results.get(0);
			} else {
				throw new DDBSToolkitException("Read function has returned "
						+ results.size() + " results");
			}
		}
		else
		{
			//TODO
			return null;
		}
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

		testConnection(object);

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(object);

		List<SparqlClassIdProperty> sparqlIdProperties = sparqlEntity
				.getSparqlEntityIDProperties();

		if (sparqlIdProperties.size() == 1) {
			SparqlClassIdProperty sparqlIdProperty = sparqlIdProperties.get(0);

			List<String> conditionList = new ArrayList<>();
			conditionList.add("?element " + sparqlIdProperty.getNamespaceName()
					+ ":" + sparqlIdProperty.getPropertyName() + " ?entity_id");

			List<T> results = listAll(object, conditionList,
					"ORDER BY DESC(?entity_id)");
			if (results.size() == 1) {
				return results.get(0);
			} else {
				throw new DDBSToolkitException("Read function has returned "
						+ results.size() + " results");
			}
		} else {
			throw new DDBSToolkitException("Read last function has returned "
					+ sparqlIdProperties.size() + " Id elements");
		}
	}

	@Override
	public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

		testConnection(objectToAdd);

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(objectToAdd);

		// Start a writing transaction
		myDataset.begin(ReadWrite.WRITE);

		// Get the model
		Model myModel = myDataset.getDefaultModel();

		SparqlClassProperty uriProperty = sparqlEntity.getUri();
		String defaultNamespaceUri = sparqlEntity.getDefaultNamespace();

		try {
			if (uriProperty != null && defaultNamespaceUri != null
					&& !((String) uriProperty.getValue()).isEmpty()) {
				Resource resourceToAdd = myModel
						.createResource((String) uriProperty.getValue());

				myModel.add(
						resourceToAdd,
						RDF.type,
						myModel.createResource(defaultNamespaceUri
								+ objectToAdd.getClass().getSimpleName()));

				for (SparqlClassProperty sparqlClassProperty : sparqlEntity
						.getSupportedPrimaryTypeEntityPropertiesWithoutURI()) {
					if (sparqlClassProperty.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.INTEGER)
							|| sparqlClassProperty
									.getDdbsToolkitSupportedEntity()
									.equals(SparqlDDBSToolkitSupportedEntity.LONG)
							|| sparqlClassProperty
									.getDdbsToolkitSupportedEntity()
									.equals(SparqlDDBSToolkitSupportedEntity.FLOAT)
							|| sparqlClassProperty
									.getDdbsToolkitSupportedEntity()
									.equals(SparqlDDBSToolkitSupportedEntity.LONG)
							|| sparqlClassProperty
									.getDdbsToolkitSupportedEntity()
									.equals(SparqlDDBSToolkitSupportedEntity.STRING)) {
						addElement(myModel, resourceToAdd, sparqlClassProperty,
								sparqlClassProperty.getValue());
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY)) {

						int[] array = (int[]) sparqlClassProperty.getValue();
						if (array != null) {
							for (int elementToAdd : array) {
								addElement(myModel, resourceToAdd,
										sparqlClassProperty, elementToAdd);
							}
						}

					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY)) {

						long[] array = (long[]) sparqlClassProperty.getValue();
						if (array != null) {
							for (long elementToAdd : array) {
								addElement(myModel, resourceToAdd,
										sparqlClassProperty, elementToAdd);
							}
						}
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY)) {

						float[] array = (float[]) sparqlClassProperty
								.getValue();

						if (array != null) {
							for (float elementToAdd : array) {
								addElement(myModel, resourceToAdd,
										sparqlClassProperty, elementToAdd);
							}
						}
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY)) {

						double[] array = (double[]) sparqlClassProperty
								.getValue();
						if (array != null) {
							for (double elementToAdd : array) {
								addElement(myModel, resourceToAdd,
										sparqlClassProperty, elementToAdd);
							}
						}
					} else if (sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.STRING_ARRAY)) {

						String[] array = (String[]) sparqlClassProperty
								.getValue();
						if (array != null) {
							for (String elementToAdd : array) {
								addElement(myModel, resourceToAdd,
										sparqlClassProperty, elementToAdd);
							}
						}
					}
				}

				// Commit the transaction
				myDataset.commit();

				return true;
			} else {
				throw new DDBSToolkitException("URI has not been defined");
			}
		} catch (Exception ex) {
			
			myDataset.abort();
			
			throw new DDBSToolkitException("An exception has occured", ex);
		
		} finally {
			myDataset.end();
		}
	}

	@Override
	public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {

		delete(objectToUpdate);
		add(objectToUpdate);
		return true;
	}

	@Override
	public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

		testConnection(objectToDelete);

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(objectToDelete);

		// Start a writing transaction
		myDataset.begin(ReadWrite.WRITE);

		try {

			// Get the model
			Model myModel = myDataset.getDefaultModel();

			SparqlClassProperty uriProperty = sparqlEntity.getUri();

			if (uriProperty != null && uriProperty.getValue() != null) {
				// Remove all the triples associated with the URI
				myModel.createResource((String) uriProperty.getValue())
						.removeAll(null);

				// Commit the transaction
				myDataset.commit();

				return true;
			} else {
				throw new DDBSToolkitException("URI has not been defined");
			}

		} finally {
			myDataset.end();
		}
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

		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> sparqlEntity = SparqlDDBSEntity
				.getDDBSEntity(objectToLoad);

		testConnection(objectToLoad);

		SparqlClassProperty linkProperty = sparqlEntity
				.getDDBSEntityProperty(field);

		SparqlClassProperty uri = sparqlEntity.getUri();

		IEntity objectLinked;
		try {
			objectLinked = (IEntity) Class.forName(
					linkProperty.getObjectTypeName()).newInstance();

			if (linkProperty != null && uri != null) {
				List<String> listCondition = new ArrayList<String>();
				listCondition.add("<" + uri.getValue() + "> "
						+ linkProperty.getNamespaceName() + ":"
						+ linkProperty.getPropertyName() + " "
						+ getObjectVariable(objectLinked));
				
				Set<String> additionalHeader = new HashSet<>();
				additionalHeader.add("prefix " + linkProperty.getNamespaceName()
					+ ": <" + linkProperty.getNamespaceURL() + ">\n");

				List<IEntity> listObject = listAll(objectLinked, listCondition,
						orderBy, additionalHeader);

				Field f = objectToLoad.getClass().getField(
						linkProperty.getName());
				
				Object arrayObject = Array.newInstance(Class.forName(linkProperty.getObjectTypeName()), listObject.size());
				
				int counterArray = 0;
				for(IEntity iEntity : listObject)
				{
					Array.set(arrayObject, counterArray, iEntity);
					counterArray++;
				}
				
				f.set(objectToLoad, arrayObject);
			} else {
				throw new DDBSToolkitException(
						"Linked field or URI has not been defined");
			}

			return objectToLoad;

		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | NoSuchFieldException
				| SecurityException e) {
			throw new DDBSToolkitException("Error while creating object", e);
		}
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

			@SuppressWarnings("unchecked")
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
						.getField(sparqlEntity.getUri().getName()).get(myData);

				for (SparqlClassProperty sparqlClassProperty : sparqlEntity
						.getEntityProperties()) {
					
					if(sparqlClassProperty.isArray() && !sparqlClassProperty
							.getDdbsToolkitSupportedEntity()
							.equals(SparqlDDBSToolkitSupportedEntity.IENTITY_ARRAY))
					{
						Field f = myData.getClass().getField(
								sparqlClassProperty.getName());

						if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.INTEGER_ARRAY)) {
							if(!sparqlClassProperty.isPrimitiveArray())
							{
								f.set(myData,sparqlResults.getIntegerArray(
										sparqlClassProperty.getName(), uri)
										.toArray());
							}
							else
							{
								Set<Integer> integerSet = sparqlResults.getIntegerArray(
										sparqlClassProperty.getName(), uri);
								int[]resultInt = new int[integerSet.size()];
								int counterResult = 0;
								for(Integer integer : integerSet)
								{
									resultInt[counterResult] = integer;
									counterResult++;
								}
								f.set(myData,resultInt);
							}
							
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.LONG_ARRAY)) {
							
							if(!sparqlClassProperty.isPrimitiveArray())
							{
								f.set(myData,sparqlResults.getLongArray(
										sparqlClassProperty.getName(), uri)
										.toArray(new Long[] {}));
							}
							else
							{
								Set<Long> longSet = sparqlResults.getLongArray(
										sparqlClassProperty.getName(), uri);
								long[]resultLong = new long[longSet.size()];
								int counterResult = 0;
								for(Long longObject : longSet)
								{
									resultLong[counterResult] = longObject;
									counterResult++;
								}
								f.set(myData,resultLong);
							}
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.FLOAT_ARRAY)) {
							
							if(!sparqlClassProperty.isPrimitiveArray())
							{
								f.set(myData,sparqlResults.getFloatArray(
										sparqlClassProperty.getName(), uri)
										.toArray(new Float[] {}));
							}
							else
							{
								Set<Float> floatSet = sparqlResults.getFloatArray(
										sparqlClassProperty.getName(), uri);
								float[]resultFloat = new float[floatSet.size()];
								int counterResult = 0;
								for(Float floatObject : floatSet)
								{
									resultFloat[counterResult] = floatObject;
									counterResult++;
								}
								f.set(myData,resultFloat);
							}
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.DOUBLE_ARRAY)) {
							
							if(!sparqlClassProperty.isPrimitiveArray())
							{
								f.set(myData,sparqlResults.getDoubleArray(
										sparqlClassProperty.getName(), uri)
										.toArray(new Double[] {}));
							}
							else
							{
								Set<Double> doubleSet = sparqlResults.getDoubleArray(
										sparqlClassProperty.getName(), uri);
								double[]resultDouble = new double[doubleSet.size()];
								int counterResult = 0;
								for(Double doubleObject : resultDouble)
								{
									resultDouble[counterResult] = doubleObject;
									counterResult++;
								}
								f.set(myData,resultDouble);
							}
						} else if (sparqlClassProperty
								.getDdbsToolkitSupportedEntity()
								.equals(SparqlDDBSToolkitSupportedEntity.STRING_ARRAY)) {
							
							f.set(myData,sparqlResults.getStringArray(
									sparqlClassProperty.getName(), uri)
									.toArray(new String[0]));
						}
						
						
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
