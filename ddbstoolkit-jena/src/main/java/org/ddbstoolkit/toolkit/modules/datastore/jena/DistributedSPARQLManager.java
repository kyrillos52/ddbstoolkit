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

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;

    /**
     * Class representing a distributed RDF triple store
     * User: Cyril GRANDJEAN
     * Date: 19/06/2012
     * Time: 09:46
     *
     * @version 1.0 : Creation of the class
     * @version 1.1 : Manage the PropertyName annotation used for properties such as "fb:book.book.characters"
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

            this.myPeer = new Peer();
            this.myPeer.setName("Default Peer");
            this.myPeer.setUid("Default UID");
        }

        /**
         * Constructor when the peer is used to manage data
         * @param datasetPath Path of the Jena data sources folder
         */
        public DistributedSPARQLManager(String datasetPath) {
            this.pathDataset = datasetPath;
            this.myPeer = new Peer();
            this.myPeer.setName("Default Peer");
            this.myPeer.setUid("Default UID");
        }

        /**
         * Constructor when the peer is used to manage data
         * @param myPeer Peer to connect
         * @param datasetPath Path of the Jena data source folder
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
            myDataset = TDBFactory.createDataset(pathDataset) ;
            isOpen = true;
        }


        @Override
        public void close() {
            myDataset.close();
            isOpen = false;
        }

        /**
         * Get the SPARQL variable corresponding to an object
         * @param object Object to inspect
         * @return variable name in SPARQL
         */
        public static String getObjectVariable(IEntity object)
        {
            if(object != null)
            {
                ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(object);

                for (SparqlClassProperty sparqlClassProperties : listProperties) {
                    if(sparqlClassProperties.isUri())
                    {
                        return "?"+sparqlClassProperties.getName();
                    }
                }

                return "?"+SparqlClassInspector.getClassName(object);
            }
            else
            {
                return null;
            }
        }

        @Override
        public <T extends IEntity> ArrayList<T> listAll(T object, ArrayList<String> conditionList, String orderBy) throws DDBSToolkitException {

            //Get the class name of the object to inspect
            String className = SparqlClassInspector.getClassName(object);

            //Explore the properties of objects
            ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(object);

            //Create the request
            String subject = "?"+className;

            //Properties to remove from the request
            ArrayList<SparqlClassProperty> propertyToRemove = new ArrayList<SparqlClassProperty>();

            StringBuilder sparqlHeader = new StringBuilder();
            ArrayList<String> listHeaders = new ArrayList<String>();

            for (SparqlClassProperty sparqlClassProperty : listProperties) {

                //URI is not inside the request
                if(sparqlClassProperty.isUri())
                {
                    subject = "?"+sparqlClassProperty.getName();
                    propertyToRemove.add(sparqlClassProperty);
                }
                //Non SPARQL Types are ignored
                if(sparqlClassProperty.isArray() &&!SparqlClassInspector.isSparqlType(sparqlClassProperty) || sparqlClassProperty.getName().equals("node_id"))
                {
                    propertyToRemove.add(sparqlClassProperty);
                }
                String header = "prefix "+sparqlClassProperty.getNamespaceName()+": <"+sparqlClassProperty.getNamespaceURL()+">\n";

                //Avoid duplication of headers
                if(!listHeaders.contains(header))
                {
                    sparqlHeader.append(header);
                    listHeaders.add(header);
                }
            }

            //Properties are removed
            for (SparqlClassProperty sparqlClassProperties : propertyToRemove) {
                listProperties.remove(sparqlClassProperties);
            }

            StringBuilder sparqlSelect = new StringBuilder();
            StringBuilder sparqlWhere = new StringBuilder();

            sparqlSelect.append("SELECT "+subject);


            //Create the prefixes
            for (int i = 0; i < listProperties.size(); i++) {

                SparqlClassProperty sparqlClassProperty = listProperties.get(i);

                sparqlSelect.append(" ?");
                sparqlSelect.append(sparqlClassProperty.getName());

                //If the field is optional
                if(sparqlClassProperty.isOptional() || sparqlClassProperty.isArray())
                {
                    sparqlWhere.append("OPTIONAL { ");
                }
                sparqlWhere.append(subject);
                sparqlWhere.append(" ");
                sparqlWhere.append(sparqlClassProperty.getNamespaceName());
                sparqlWhere.append(":");
                sparqlWhere.append(sparqlClassProperty.getPropertyName());

                sparqlWhere.append(" ?");
                sparqlWhere.append(sparqlClassProperty.getName());
                //If the field is optional
                if(sparqlClassProperty.isOptional() || sparqlClassProperty.isArray())
                {
                    sparqlWhere.append("} ");
                }

                if(i < listProperties.size() - 1)
                {
                    sparqlWhere.append(".\n");
                }
            }

            StringBuilder sparqlRequest = new StringBuilder();
            if(conditionList == null || conditionList.isEmpty())
            {
                sparqlRequest.append(sparqlHeader);
                sparqlRequest.append(sparqlSelect);
                sparqlRequest.append(" WHERE { ");
                sparqlRequest.append(sparqlWhere);
                sparqlRequest.append(" }");
            }
            else
            {
                sparqlRequest.append(sparqlHeader);
                sparqlRequest.append(sparqlSelect);
                sparqlRequest.append(" WHERE { ");
                sparqlRequest.append(sparqlWhere);
                sparqlRequest.append(".\n");

                for(int i = 0; i < conditionList.size(); i++)
                {
                    sparqlRequest.append(conditionList.get(i));

                    if(i < conditionList.size() - 1)
                    {
                        sparqlRequest.append(".\n");
                    }
                }

                sparqlRequest.append("} ");
            }
            if(orderBy != null)
            {
                String[] list = orderBy.split(" ");
                if(list.length == 2)
                {
                    String comparatorField = list[0];
                    String orderByOrder = list[1];

                    sparqlRequest.append("ORDER BY "+orderByOrder+"(?"+comparatorField+")");
                }

            }

            //System.out.println(sparqlRequest.toString());

            Query query = QueryFactory.create(sparqlRequest.toString());

            String serviceUrl = "";
            Annotation[] annotations = object.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof Service)
                {
                    Service myService = (Service)annotation;
                    serviceUrl = myService.url();
                }
            }

            ResultSet results = null;
            //Use of remote endpoints
            if(!serviceUrl.isEmpty())
            {
                QueryExecution qe = QueryExecutionFactory.sparqlService(serviceUrl, query);
                results = qe.execSelect();
                ArrayList<T> resultList = conversionResultSet(results, object);
                qe.close();

                return  resultList;
            }
            //Use of local datastore
            else
            {
                myDataset.begin(ReadWrite.READ) ;

                try {
                    QueryExecution qe = QueryExecutionFactory.create(query, myDataset.getDefaultModel());
                    results = qe.execSelect();

                    ArrayList<T> resultList = conversionResultSet(results, object);
                    qe.close();

                    return  resultList;
                }
                finally
                {
                    myDataset.end() ;
                }

            }
        }

        @Override
        public IEntity read(IEntity object) throws DDBSToolkitException {

            //Explore the properties of objects
            ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(object);

            ArrayList<SparqlClassProperty> propertyToRemove = new ArrayList<SparqlClassProperty>();
            String uri = null;

            StringBuilder sparqlHeader = new StringBuilder();
            ArrayList<String> listHeaders = new ArrayList<String>();

            for (SparqlClassProperty sparqlClassProperty : listProperties) {

                //URI is not inside the request
                if(sparqlClassProperty.isUri())
                {
                    propertyToRemove.add(sparqlClassProperty);
                    uri = (String)sparqlClassProperty.getValue();
                }
                //Non SPARQL Types are ignored
                if(sparqlClassProperty.isArray() &&!SparqlClassInspector.isSparqlType(sparqlClassProperty) || sparqlClassProperty.getName().equals("node_id"))
                {
                    propertyToRemove.add(sparqlClassProperty);
                }
                String header = "prefix "+sparqlClassProperty.getNamespaceName()+": <"+sparqlClassProperty.getNamespaceURL()+">\n";

                //Avoid duplication of headers
                if(!listHeaders.contains(header))
                {
                    sparqlHeader.append(header);
                    listHeaders.add(header);
                }
            }

            for (SparqlClassProperty sparqlClassProperties : propertyToRemove) {
                listProperties.remove(sparqlClassProperties);
            }

            StringBuilder sparqlSelect = new StringBuilder();
            StringBuilder sparqlWhere = new StringBuilder();

            sparqlSelect.append("SELECT ");

            //Create the prefixes
            for (int i = 0; i < listProperties.size(); i++) {

                SparqlClassProperty sparqlClassProperty = listProperties.get(i);

                sparqlSelect.append(" ?");
                sparqlSelect.append(sparqlClassProperty.getName());

                //If the field is optional
                if(sparqlClassProperty.isOptional())
                {
                    sparqlWhere.append("OPTIONAL { ");
                }

                sparqlWhere.append("<");
                sparqlWhere.append(uri);
                sparqlWhere.append("> ");
                sparqlWhere.append(sparqlClassProperty.getNamespaceName());
                sparqlWhere.append(":");
                sparqlWhere.append(sparqlClassProperty.getPropertyName());
                sparqlWhere.append(" ?");
                sparqlWhere.append(sparqlClassProperty.getName());
                //If the field is optional
                if(sparqlClassProperty.isOptional())
                {
                    sparqlWhere.append("} ");
                }

                if(i < listProperties.size() - 1)
                {
                    sparqlWhere.append(".\n");
                }
            }

            StringBuilder sparqlRequest = new StringBuilder();
            sparqlRequest.append(sparqlHeader);
            sparqlRequest.append(sparqlSelect);
            sparqlRequest.append(" WHERE { ");
            sparqlRequest.append(sparqlWhere);
            sparqlRequest.append("}");

            //System.out.println(sparqlRequest.toString());

            Query query = QueryFactory.create(sparqlRequest.toString());

            String serviceUrl = "";
            Annotation[] annotations = object.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                if(annotation instanceof Service)
                {
                    Service myService = (Service)annotation;
                    serviceUrl = myService.url();
                }
            }

            ResultSet results = null;
            if(!serviceUrl.isEmpty())
            {
                QueryExecution qe = QueryExecutionFactory.sparqlService(serviceUrl, query);
                results = qe.execSelect();
                qe.close();

                ArrayList<IEntity> resultList = conversionResultSet(results, object);
                if(resultList.size() == 1)
                {
                    return resultList.get(0);
                }
                else
                {
                    return null;
                }
            }
            else
            {
                myDataset.begin(ReadWrite.READ) ;

                try {
                    QueryExecution qe = QueryExecutionFactory.create(query, myDataset.getDefaultModel());
                    results = qe.execSelect();

                    ArrayList<IEntity> resultList = conversionResultSet(results, object);
                    if(resultList.size() == 1)
                    {
                        qe.close();
                        return resultList.get(0);
                    }
                    else
                    {
                        qe.close();
                        return null;
                    }


                }
                finally
                {
                    myDataset.end() ;
                }


            }


        }

        /**
         * Last element (Compatible SPARQL 1.0) : Need the Id annotation
         * @param object object to read
         * @return last element added
         * @throws Exception
         */
        @Override
        public IEntity readLastElement(IEntity object) throws DDBSToolkitException {

        	try
        	{
        		String className = SparqlClassInspector.getClassName(object);

                //Create the request
                String subject = "?"+className;

                //Explore the properties of objects
                ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(object);

                StringBuilder sparqlHeader = new StringBuilder();
                ArrayList<String> listHeaders = new ArrayList<String>();

                listHeaders.add("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
                sparqlHeader.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");

                SparqlClassProperty idProperty = null;
                SparqlClassProperty uriProperty = null;

                for (SparqlClassProperty sparqlClassProperty : listProperties) {

                    if(sparqlClassProperty.isId())
                    {
                        idProperty = sparqlClassProperty;
                    }
                    if(sparqlClassProperty.isUri())
                    {
                        uriProperty = sparqlClassProperty;
                    }
                    String header = "prefix "+sparqlClassProperty.getNamespaceName()+": <"+sparqlClassProperty.getNamespaceURL()+">\n";

                    //Avoid duplication of headers
                    if(!listHeaders.contains(header))
                    {
                        sparqlHeader.append(header);
                        listHeaders.add(header);
                    }
                }

                StringBuilder sparqlRequest = new StringBuilder();
                sparqlRequest.append(sparqlHeader);
                sparqlRequest.append("SELECT ?element ?entity_id WHERE { ?element ");
                sparqlRequest.append(idProperty.getNamespaceName());
                sparqlRequest.append(":");
                sparqlRequest.append(idProperty.getPropertyName());
                sparqlRequest.append(" ?entity_id } ORDER BY DESC(?entity_id) LIMIT 1");

                //System.out.println(sparqlRequest.toString());

                Query query = QueryFactory.create("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> prefix business: <http://cyril-grandjean.co.uk/business/> SELECT ?element ?entity_id WHERE { ?element business:company_ID ?entity_id } ORDER BY DESC(?entity_id) LIMIT 1");

                String serviceUrl = "";
                Annotation[] annotations = object.getClass().getAnnotations();
                for (Annotation annotation : annotations) {
                    if(annotation instanceof Service)
                    {
                        Service myService = (Service)annotation;
                        serviceUrl = myService.url();
                    }
                }

                String lastUri = null;

                ResultSet results = null;
                if(!serviceUrl.isEmpty())
                {
                    QueryExecution qe = QueryExecutionFactory.sparqlService(serviceUrl, query);
                    results = qe.execSelect();

                    //For each object
                    while(results.hasNext()){

                        QuerySolution myResult = results.next();

                        lastUri = myResult.get("element").toString();
                    }

                    qe.close();
                }
                else
                {
                    myDataset.begin(ReadWrite.READ) ;

                    try {
                        QueryExecution qe = QueryExecutionFactory.create(query, myDataset.getDefaultModel());
                        results = qe.execSelect();


                        //For each object
                        while(results.hasNext()){

                            QuerySolution myResult = results.next();

                            lastUri = myResult.get("element").toString();
                        }

                        qe.close();
                    }
                    finally
                    {
                        myDataset.end() ;
                    }

                }


                if(lastUri != null)
                {

                    Field f = object.getClass().getField(uriProperty.getName());

                    f.set(object, lastUri);

                    return read(object);
                }
                else
                {
                    return null;
                }
        	}
        	catch (IllegalAccessException iae) {
    			throw new DDBSToolkitException("Illegal access exception using reflection", iae);
    		} catch (NoSuchFieldException nsfe) {
    			throw new DDBSToolkitException("No such field exception using reflection", nsfe);
    		}
        }

        @Override
        public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

            //Connection must be opened and object non null
            if(isOpen && objectToAdd != null)
            {
                //Start a writing transaction
                myDataset.begin(ReadWrite.WRITE) ;
                try {

                    //Get the model
                    Model myModel = myDataset.getDefaultModel();

                    //Explore the properties of the object to add
                    ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(objectToAdd);

                    //Get the URI
                    String uri = "";
                    for (SparqlClassProperty sparqlClassProperty : listProperties) {
                        if(sparqlClassProperty.isUri())
                        {
                            uri = (String) sparqlClassProperty.getValue();
                            break;
                        }
                    }

                    //If there is an URI
                    if(!uri.equals(""))
                    {
                        Field[] f = null;
                        Class c = null;

                        c = objectToAdd.getClass();
                        f = c.getFields();

                        Annotation[] classAnnotations = c.getAnnotations();

                        //Get the default namespace
                        String defaultNamespaceUrl = "";
                        for(Annotation annotation : classAnnotations)
                        {
                            if (annotation instanceof DefaultNamespace)
                            {
                                DefaultNamespace ns = (DefaultNamespace)annotation;

                                defaultNamespaceUrl = ns.url();
                            }
                        }

                        Resource resourceToAdd = myModel.createResource(uri);

                        myModel.add(resourceToAdd, RDF.type, myModel.createResource(defaultNamespaceUrl + objectToAdd.getClass().getSimpleName()));

                        for (SparqlClassProperty sparqlClassProperty : listProperties) {

                            if(!sparqlClassProperty.getName().equals("node_id"))
                            {
                                //System.out.println("Property name "+sparqlClassProperty.getName()+" Property type "+sparqlClassProperty.getType());

                                //If it's not the URI or an array or the node_id
                                if(!sparqlClassProperty.isUri() && !sparqlClassProperty.isArray() && !sparqlClassProperty.getName().equals("node_id"))
                                {
                                    if(sparqlClassProperty.getType().equals("int"))
                                    {
                                        myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()),  (Integer)sparqlClassProperty.getValue()));
                                    }
                                    else if(sparqlClassProperty.getType().equals("long"))
                                    {
                                        myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()),  (Long)sparqlClassProperty.getValue()));
                                    }
                                    else if(sparqlClassProperty.getType().equals("float"))
                                    {
                                        myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()),  (Float)sparqlClassProperty.getValue()));
                                    }
                                    else if(sparqlClassProperty.getType().equals("java.lang.String"))
                                    {
                                        String value;
                                        if(sparqlClassProperty.getValue() == null)
                                        {
                                            value = "";
                                        }
                                        else
                                        {
                                            value = (String)sparqlClassProperty.getValue();
                                        }
                                        myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()),  value));
                                    }
                                }
                                //Array add the node
                                if(sparqlClassProperty.isArray())
                                {
                                    //Primitives types + Strings
                                    if(SparqlClassInspector.isSparqlType(sparqlClassProperty))
                                    {
                                        String property = sparqlClassProperty.getType();

                                        Field arrayField = objectToAdd.getClass().getField(sparqlClassProperty.getName());

                                        if(arrayField.get(objectToAdd) != null)
                                        {
                                            //Integer array
                                            if(property.equals("[I"))
                                            {
                                                int [] array = (int[]) arrayField.get(objectToAdd);

                                                for(int i = 0; i < array.length; i++)
                                                {
                                                    myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()), array[i]));
                                                }

                                            }
                                            //Long array
                                            else if(property.equals("[J"))
                                            {
                                                long [] array = (long[]) arrayField.get(objectToAdd);

                                                for(int i = 0; i < array.length; i++)
                                                {
                                                    myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()), array[i]));
                                                }
                                            }
                                            //Float array
                                            else if(property.equals("[F"))
                                            {
                                                float [] array = (float[]) arrayField.get(objectToAdd);

                                                for(int i = 0; i < array.length; i++)
                                                {
                                                    myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()), array[i]));
                                                }
                                            }
                                            //String array
                                            else if(property.equals("[Ljava.lang.String;"))
                                            {
                                                String [] array = (String[]) arrayField.get(objectToAdd);

                                                for(int i = 0; i < array.length; i++)
                                                {
                                                    myModel.add(myModel.createLiteralStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()), array[i]));
                                                }
                                            }
                                        }

                                    }
                                    //IEntity types
                                    else
                                    {
                                        Field arrayField = objectToAdd.getClass().getField(sparqlClassProperty.getName());

                                        IEntity[] array = (IEntity[]) arrayField.get(objectToAdd);
                                        if(array != null)
                                        {
                                            int size = Array.getLength(array);

                                            for(int i = 0; i < size; i++)
                                            {
                                                IEntity object =(IEntity) Array.get(array, i);

                                                String uri_field = "";
                                                ArrayList<SparqlClassProperty> properties = SparqlClassInspector.explorePropertiesForSPARQL(object);
                                                for(SparqlClassProperty property : properties)
                                                {
                                                    if(property.isUri())
                                                    {
                                                        uri_field = property.getName();
                                                        break;
                                                    }
                                                }

                                                Field fieldLinked = object.getClass().getField(uri_field);
                                                String uriToLink = (String)fieldLinked.get(object);

                                                //System.out.println("URI to link : "+uriToLink);

                                                myModel.add(myModel.createStatement(resourceToAdd, myModel.createProperty(sparqlClassProperty.getNamespaceURL() + sparqlClassProperty.getPropertyName()),  myModel.createResource(uriToLink)));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //Commit the transaction
                        myDataset.commit() ;

                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    return false;
                }
                finally {
                    myDataset.end() ;
                }
            }
            else
            {
                return false;
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

            if(objectToDelete != null)
            {
                //Start a writing transaction
                myDataset.begin(ReadWrite.WRITE) ;
                try {

                    //Get the model
                    Model myModel = myDataset.getDefaultModel() ;

                    //Explore the properties of the object to add
                    ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(objectToDelete);

                    //Get the URI
                    String uriValue = null;
                    for (SparqlClassProperty sparqlClassProperty : listProperties) {
                        if(sparqlClassProperty.isUri())
                        {
                            uriValue = (String) sparqlClassProperty.getValue();
                            break;
                        }
                    }

                    if(uriValue != null)
                    {
                        //Remove all the triples associated with the URI
                        myModel.createResource(uriValue).removeAll(null);

                        //Commit the transaction
                        myDataset.commit() ;
                    }
                    else
                    {
                        return false;
                    }

                    return true;
                }
                catch (Exception ex)
                {
                    return false;
                }
                finally {
                    myDataset.end() ;
                }

            }
            else
            {
                return false;
            }
        }

        @Override
        public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException {
            //No Create table statement with Sparql Endpoint
            return true;
        }

        @Override
        public IEntity loadArray(IEntity objectToLoad, String field, String orderBy) throws DDBSToolkitException {

        	try
        	{
        		ArrayList<SparqlClassProperty> listOfProperties = SparqlClassInspector.explorePropertiesForSPARQL(objectToLoad);

                SparqlClassProperty linkProperty = null;
                SparqlClassProperty uriPropertyObjectToLoad = null;

                for(SparqlClassProperty property : listOfProperties)
                {
                    if(property.getName().equals(field))
                    {
                        linkProperty = property;
                    }
                    if(property.isUri())
                    {
                        uriPropertyObjectToLoad = property;
                    }
                }

                String objectName = linkProperty.getType().substring(2, linkProperty.getType().length()-1);

                IEntity objectLinked = (IEntity) Class.forName(objectName).newInstance();

                SparqlClassProperty uriProperty = null;
                listOfProperties = SparqlClassInspector.explorePropertiesForSPARQL(objectLinked);

                for(SparqlClassProperty property : listOfProperties)
                {
                    if(property.isUri())
                    {
                        uriProperty = property;
                    }
                }

                Field fieldUri = objectToLoad.getClass().getField(uriPropertyObjectToLoad.getName());

                ArrayList<String> listCondition = new ArrayList<String>();
                listCondition.add("<"+fieldUri.get(objectToLoad)+"> "+linkProperty.getNamespaceName()+":"+linkProperty.getPropertyName()+" "+getObjectVariable(objectLinked));


                ArrayList<IEntity> listObject = listAll(objectLinked, listCondition, orderBy);

                Field f = objectToLoad.getClass().getField(field);

                Object array = Array.newInstance(Class.forName(objectName), listObject.size());

                int i = 0;
                for(IEntity entity : listObject)
                {
                    Array.set(array, i, entity);
                    i++;
                }

                f.set(objectToLoad, array);

                return objectToLoad;
        	}
        	catch (Exception e) {
    			throw new DDBSToolkitException("Error during use of the reflection mechanism", e);
    		}
        }

        protected <T extends IEntity> ArrayList<T> conversionResultSet(ResultSet results, T myObject) throws DDBSToolkitException {

        	try
        	{
        		ArrayList<T> resultList = new ArrayList<T>();

                boolean hasArray = false;
                String uri = "";

                ArrayList<SparqlClassProperty> listProperties = SparqlClassInspector.explorePropertiesForSPARQL(myObject);
                for(SparqlClassProperty myProperty : listProperties)
                {
                    if(myProperty.isArray() && SparqlClassInspector.isSparqlType(myProperty))
                    {
                        hasArray = true;
                    }
                    if(myProperty.isUri())
                    {
                        uri = myProperty.getName();
                    }
                }

                //There is no arrays
                if(!hasArray)
                {
                    //For each object
                    while(results.hasNext()){

                        QuerySolution myResult = results.next();

                        //Get class name
                        String nameClass = ClassInspector.getFullClassName(myObject);

                        //Instantiate the object
                        T myData = (T) Class.forName(nameClass).newInstance();

                        //Set object properties
                        for(SparqlClassProperty myProperty : listProperties)
                        {
                            Field f = myData.getClass().getField(myProperty.getName());

                            //If it's not an array
                            if(!myProperty.isArray())
                            {
                                if(!myProperty.isOptional() || (myProperty.isOptional() && myResult.getLiteral(myProperty.getName()) != null))
                                {
                                    if(myProperty.getType().equals("int"))
                                    {
                                        f.set(myData, myResult.getLiteral(myProperty.getName()).getInt());
                                    }
                                    else if(myProperty.getType().equals("long"))
                                    {
                                        f.set(myData, myResult.getLiteral(myProperty.getName()).getLong());
                                    }
                                    else if(myProperty.getType().equals("float"))
                                    {
                                        f.set(myData, myResult.getLiteral(myProperty.getName()).getFloat());
                                    }
                                    else if(myProperty.getType().equals("java.lang.String"))
                                    {
                                        //If it's the node_id property
                                        if(myProperty.getName().equals("node_id"))
                                        {
                                            f.set(myData, myPeer.getUid());
                                        }
                                        else
                                        {
                                            if(myResult.get(myProperty.getName()) == null)
                                            {
                                                f.set(myData, f.get(myObject));
                                            }
                                            else
                                            {
                                                String stringResult = myResult.get(myProperty.getName()).toString();
                                                stringResult = stringResult.replaceAll("\\^\\^http://www.w3.org/2001/XMLSchema#string$","");
                                                f.set(myData, stringResult);
                                            }

                                        }
                                    }
                                }
                            }
                        }

                        resultList.add(myData);
                    }

                    return resultList;
                }
                else
                {

                    //For each object
                    while(results.hasNext()){

                        QuerySolution myResult = results.next();

                        //Get class name
                        String nameClass = ClassInspector.getFullClassName(myObject);

                        //Instantiate the object
                        T myData = (T) Class.forName(nameClass).newInstance();

                        Field fieldUri =  myData.getClass().getField(uri);

                        boolean hasEntity = false;
                        IEntity existingEntity = null;
                        for(IEntity entity : resultList)
                        {
                             String entityUri = (String)fieldUri.get(entity);
                             if(myResult.getResource(uri) == null || entityUri.equals(myResult.getResource(uri).toString()))
                             {
                                 hasEntity = true;
                                 existingEntity = entity;
                                 break;
                             }
                        }

                        //If the entity didn't exist
                        if(!hasEntity)
                        {
                            //Set object properties
                            for(SparqlClassProperty myProperty : listProperties)
                            {
                                Field f = myData.getClass().getField(myProperty.getName());

                                //If it's not an array
                                if(!myProperty.isArray())
                                {
                                    if(myProperty.getType().equals("int"))
                                    {
                                        f.set(myData, myResult.getLiteral(myProperty.getName()).getInt());
                                    }
                                    else if(myProperty.getType().equals("long"))
                                    {
                                        f.set(myData, myResult.getLiteral(myProperty.getName()).getLong());
                                    }
                                    else if(myProperty.getType().equals("float"))
                                    {
                                        f.set(myData, myResult.getLiteral(myProperty.getName()).getFloat());
                                    }
                                    else if(myProperty.getType().equals("java.lang.String"))
                                    {
                                        //If it's the node_id property
                                        if(myProperty.getName().equals("node_id"))
                                        {
                                            f.set(myData, myPeer.getUid());
                                        }
                                        else
                                        {
                                            if(myResult.get(myProperty.getName()) == null)
                                            {
                                                f.set(myData, f.get(myObject));
                                            }
                                            else
                                            {
                                                String stringResult = myResult.get(myProperty.getName()).toString();
                                                stringResult = stringResult.replaceAll("\\^\\^http://www.w3.org/2001/XMLSchema#string$","");
                                                f.set(myData, stringResult);
                                            }

                                        }
                                    }
                                }
                                //If it's an array and sparql type
                                else if(myProperty.isArray() && SparqlClassInspector.isSparqlType(myProperty))
                                {
                                    Field arrayField = myData.getClass().getField(myProperty.getName());
                                    Object listObject = arrayField.get(myObject);

                                    String property = myProperty.getType();

                                    //Detect if the object already exist
                                    boolean objectExist = false;

                                    int sizeArray = 0;

                                    if(listObject != null)
                                    {
                                        sizeArray = Array.getLength(listObject);
                                        for(int i = 0; i < sizeArray; i++)
                                        {
                                            //Integer array
                                            if(property.equals("[I"))
                                            {
                                                if(myResult.getLiteral(myProperty.getName()).getInt() == (Integer)Array.getInt(listObject, i))
                                                {
                                                    objectExist = true;
                                                }
                                            }
                                            //Long array
                                            else if(property.equals("[J"))
                                            {
                                                if(myResult.getLiteral(myProperty.getName()).getLong() == (Long)Array.getLong(listObject, i))
                                                {
                                                    objectExist = true;
                                                }
                                            }
                                            //Float array
                                            else if(property.equals("[F"))
                                            {
                                                if(myResult.getLiteral(myProperty.getName()).getFloat() == (Float)Array.getFloat(listObject, i))
                                                {
                                                    objectExist = true;
                                                }
                                            }
                                            else if(property.equals("[Ljava.lang.String;"))
                                            {
                                                if(myResult.get(myProperty.getName()).toString().equals((String)Array.get(listObject, i)))
                                                {
                                                    objectExist = true;
                                                }
                                            }
                                        }
                                    }


                                    //If the object is not included
                                    if(!objectExist)
                                    {
                                        if(myResult.get(myProperty.getName()) != null)
                                        {
                                            if(property.equals("[I"))
                                            {
                                                int[] array = new int[sizeArray+1];

                                                for(int i = 0; i < sizeArray; i++)
                                                {
                                                    Array.set(array, i, Array.getInt(listObject, i));
                                                }

                                                Array.set(array, sizeArray, myResult.getLiteral(myProperty.getName()).getInt());

                                                arrayField.set(myData, array);
                                            }
                                            else if(property.equals("[J"))
                                            {
                                                long[] array = new long[sizeArray+1];

                                                for(int i = 0; i < sizeArray; i++)
                                                {
                                                    Array.set(array, i, Array.getLong(listObject, i));
                                                }

                                                Array.set(array, sizeArray, myResult.getLiteral(myProperty.getName()).getLong());

                                                arrayField.set(myData, array);
                                            }
                                            else if(property.equals("[F"))
                                            {
                                                float[] array = new float[sizeArray+1];

                                                for(int i = 0; i < sizeArray; i++)
                                                {
                                                    Array.set(array, i, Array.getFloat(listObject, i));
                                                };

                                                Array.set(array, sizeArray, myResult.getLiteral(myProperty.getName()).getFloat());

                                                arrayField.set(myData, array);
                                            }
                                            else if(property.equals("[Ljava.lang.String;"))
                                            {
                                                String[] array = new String[sizeArray+1];

                                                for(int i = 0; i < sizeArray; i++)
                                                {
                                                    Array.set(array, i, (String)Array.get(listObject, i));
                                                };

                                                String stringResult = myResult.get(myProperty.getName()).toString();
                                                stringResult = stringResult.replaceAll("\\^\\^http://www.w3.org/2001/XMLSchema#string$","");
                                                Array.set(array, sizeArray, stringResult);

                                                arrayField.set(myData, array);
                                            }
                                        }
                                    }
                            }
                        }
                        resultList.add(myData);
                    }
                    else
                    {
                        //Set object properties
                        for(SparqlClassProperty myProperty : listProperties)
                        {
                            Field f = myData.getClass().getField(myProperty.getName());

                            //If it's an array and sparql type
                            if(myProperty.isArray() && SparqlClassInspector.isSparqlType(myProperty))
                            {
                                Field arrayField = myData.getClass().getField(myProperty.getName());
                                Object listObject =  arrayField.get(existingEntity);

                                String property = myProperty.getType();

                                //Detect if the object already exist
                                boolean objectExist = false;

                                int sizeArray = 0;

                                if(listObject != null)
                                {
                                    sizeArray = Array.getLength(listObject);
                                    for(int i = 0; i < sizeArray; i++)
                                    {
                                        if(property.equals("[I"))
                                        {
                                            if(myResult.getLiteral(myProperty.getName()).getInt() == (Integer)Array.getInt(listObject, i))
                                            {
                                                objectExist = true;
                                            }
                                        }
                                        else if(property.equals("[J"))
                                        {
                                            if(myResult.getLiteral(myProperty.getName()).getLong() == (Long)Array.getLong(listObject, i))
                                            {
                                                objectExist = true;
                                            }
                                        }
                                        else if(property.equals("[F"))
                                        {
                                            if(myResult.getLiteral(myProperty.getName()).getFloat() == (Float)Array.getFloat(listObject, i))
                                            {
                                                objectExist = true;
                                            }
                                        }
                                        else if(property.equals("[Ljava.lang.String;"))
                                        {
                                            String stringResult = myResult.get(myProperty.getName()).toString();
                                            stringResult = stringResult.replaceAll("\\^\\^http://www.w3.org/2001/XMLSchema#string$","");

                                            if(stringResult.equals((String)Array.get(listObject, i)))
                                            {
                                                objectExist = true;
                                            }
                                        }
                                    }
                                }


                                //If the object is not included
                                if(!objectExist)
                                {
                                    if(property.equals("[I"))
                                    {
                                        int[] array = new int[sizeArray+1];

                                        for(int i = 0; i < sizeArray; i++)
                                        {
                                            Array.set(array, i, Array.getInt(listObject, i));
                                        }

                                        Array.set(array, sizeArray, myResult.getLiteral(myProperty.getName()).getInt());

                                        arrayField.set(existingEntity, array);
                                    }
                                    else if(property.equals("[J"))
                                    {
                                        long[] array = new long[sizeArray+1];

                                        for(int i = 0; i < sizeArray; i++)
                                        {
                                            Array.set(array, i, Array.getLong(listObject, i));
                                        }

                                        Array.set(array, sizeArray, myResult.getLiteral(myProperty.getName()).getLong());

                                        arrayField.set(existingEntity, array);
                                    }
                                    else if(property.equals("[F"))
                                    {
                                        float[] array = new float[sizeArray+1];

                                        for(int i = 0; i < sizeArray; i++)
                                        {
                                            Array.set(array, i, Array.getFloat(listObject, i));
                                        };

                                        Array.set(array, sizeArray, myResult.getLiteral(myProperty.getName()).getFloat());

                                        arrayField.set(existingEntity, array);
                                    }
                                    else if(property.equals("[Ljava.lang.String;"))
                                    {
                                        String[] array = new String[sizeArray+1];

                                        for(int i = 0; i < sizeArray; i++)
                                        {
                                            Array.set(array, i, (String)Array.get(listObject, i));
                                        };

                                        String stringResult = myResult.get(myProperty.getName()).toString();
                                        stringResult = stringResult.replaceAll("\\^\\^http://www.w3.org/2001/XMLSchema#string$","");
                                        Array.set(array, sizeArray, stringResult);

                                        arrayField.set(existingEntity, array);
                                    }
                                }
                            }
                        }
                    }
                }

                    //System.out.println("Return list size = "+resultList.size());
            }

            return resultList;
        }
    	catch (InstantiationException ie) {
			throw new DDBSToolkitException("Problem during instantiation of the object using reflection", ie);
		} catch (IllegalAccessException iae) {
			throw new DDBSToolkitException("Illegal access exception using reflection", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found using reflection", cnfe);
		} catch (NoSuchFieldException nsfe) {
			throw new DDBSToolkitException("No such field exception using reflection", nsfe);
		}
            
    }
}
