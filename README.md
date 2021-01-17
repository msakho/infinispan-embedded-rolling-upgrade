# Infinispan Embedded Rolling Upgrade

## Info 
Showcase for migration of embedded clusters using Hot Rod. 

An embedded cluster with version ```DG 7.0``` stores caches
as POJOs, and has an embedded Hot Rod server that is used by a second cluster running ```11.0.x``` to migrate data without downtime.
:warning: Java 8 should be used to run the processes!
## Build the project

```
mvn clean package
```

## Running the source cluster

Start one of more nodes with: 

```
cd source/
mvn exec:java
```

To avoid port conflict in successive nodes, use the ```offset``` JVM property, e.g.: 

```mvn clean install -Doffset=1000 exec:java```

## Running the target cluster and Migrate data

Start one or more nodes with:

```
cd destination/
mvn exec:java
```

To avoid port conflict, the JVM property ```offset``` can also be used, similarly to the source cluster. 
Once the destination is started, the data from the source cache are beeing migrated and will be outputed from the console.

## Viewing migrated data

Since the destination cluster exposes a REST server, the data should be available in the destination cluster. Check with:

```
curl "http://localhost:8888/rest/v2/caches/cache?action=keys"
```

It's also possible to view a specific cache data with the following REST call:

```
curl  -H "Key-Content-Type: application/x-java-object; type=java.lang.Integer" "http://localhost:8888/rest/v2/caches/cache/1"
```




