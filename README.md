# Infinispan Embedded Rolling Upgrade

## Info 
Showcase for migration of embedded clusters using Hot Rod. 

An embedded cluster with version ```9.4``` stores caches
as POJOs, and has an embedded Hot Rod server that is used by a second cluster running ```11.0.9-SNAPSHOT``` to migrate data without downtime.

## Running the source cluster

Build the project:
mvn clean install

Start one of more nodes with: 

```
cd source/
mvn exec:java
```

Optionnally to avoid port conflict in successive nodes, use the ```offset``` JVM property, e.g.: 

```mvn clean install -Doffset=1000 exec:java```

## Running the target cluster

Start one or more nodes with:

```
cd destination/
mvn exec:java
```

To avoid port conflict, the JVM property ```offset``` can also be used, similarly to the source cluster. 

## Migrating data

The source cluster perform the data migration when it starts and disconnect from the source cluster.

Since the destination cluster exposes a REST server:




The source cluster can be stopped and the data should be available in the destination cluster. Check with:

```
curl "http://localhost:8888/rest/v2/caches/cache?action=keys"
```

It's also possible to view any object from the cache by passing using the following REST url. 
We use the key=1 
curl  -H "Key-Content-Type: application/x-java-object; type=java.lang.Integer" "http://localhost:8888/rest/v2/caches/cache/1"
