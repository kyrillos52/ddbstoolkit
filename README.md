DDBS Toolkit
======================
[![Build Status](https://travis-ci.org/kyrillos52/ddbstoolkit.svg?branch=master)](https://travis-ci.org/kyrillos52/ddbstoolkit)
[![Coverage Status](https://codecov.io/gh/kyrillos52/ddbstoolkit/branch/master/graph/badge.svg)](https://codecov.io/gh/kyrillos52/ddbstoolkit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.ddbstoolkit.toolkit/ddbstoolkit-root/badge.svg?style=flat-square)](https://repo1.maven.org/maven2/org/ddbstoolkit/)
======================
DDBS Toolkit is a lightweight Java toolkit for distributed data store applications using programming abstraction.
* Official website:  [ddbstoolkit.org](https://ddbstoolkit.org)
* Supported data modules:  MySQL, PosgreSQL, Jena, SQLite
* Supported middleware modules:  JGroups, SQLSpaces

## Import modules into your project
Add the needed dependencies into your pom.xml. Example for MySQL module:
```
<dependency>
    <groupid>org.ddbstoolkit.toolkit.modules.datastore.mysql</groupid>
    <artifactid>ddbstoolkit-mysql</artifactid>
    <version>1.0.0-beta2</version>
    <scope>compile</scope>
</dependency>
```
## Create your data model
You need to create your data classes with the following requirements :
* For non distributed data: your classes need to implement IEntity
* For distributed data: your classes need to extend DistributedEntity
Example for distributed Actor data
```
public class Actor extends DistributedEntity {
 
    @Id
    @EntityName(name="actor_id")
    private Integer actorId;
 
    @EntityName(name="actor_name")
    private String actorName;
 
    @EntityName(name="film_id")
    private Integer filmId;
     
    public Integer getActorId() {
        return actorId;
    }
 
    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }
 
    public String getActorName() {
        return actorName;
    }
 
    public void setActorName(String actorName) {
        this.actorName = actorName;
    }
 
    public Integer getFilmId() {
        return filmId;
    }
 
    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }
}
```
## Instantiate your data modules
Example for MySQL Module
```
MySQLConnector mysqlConnector = new MySQLConnector("jdbc:mysql://localhost:3306/myDB", "user", "password");
DistributableEntityManager manager = new DistributedMySQLTableManager(mysqlConnector)
```
## You can already start to use your data module
Example to add an actor data
```
manager.open();
 
Actor anActor = new Actor();
anActor.setActorName("Steven");
     
manager.add(anActor);
 
manager.close();
```
## For middleware modules: Create your receiver interface and start to listen
The receiver interface will listen to data transactions and will transmit the command into the data module. Example for JGroups module:
```
DistributableReceiverInterface receiver = null;
try {
    receiver = new JGroupReceiver(manager, "defaultCluster", "receiver");
    receiver.start();
} finally {
    if(receiver != null) {
        receiver.stop();
    }
}
```
## For middleware modules: Create your sender interface
The sender module will send commands to your receiver interfaces. Example for JGroups module:
```
DistributableSenderInterface senderInterface = new JGroupSender("defaultCluster", "sender");
```
## For middleware modules: You can already start to use your middleware module
Example to add an actor data
```
senderInterface.open();
 
Actor anActor = new Actor();
anActor.setPeerUid("receiver");
anActor.setActorName("Steven"); 
     
senderInterface.add(anActor);
 
senderInterface.close();
```

## Create your own module
You first need to add the DDBS Toolkit core dependency into your pom.xml
```
<dependency>
    <groupid>org.ddbstoolkit.toolkit.core</groupid>
    <artifactid>ddbstoolkit-core</artifactid>
    <version>1.0.0-beta2</version>
    <scope>compile</scope>
</dependency>
```
In order to create your own data module, you will have to :
* Implement the interface DistributableEntityManager

In order to create your own middleware module, you will have to :
* Implement the interface DistributableSenderInterface
* Implement the interface DistributableReceiverInterface

## More information, link to the documentation
[ddbstoolkit.org](https://ddbstoolkit.org)
