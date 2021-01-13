# Infinispan Embedded Rolling Upgrade

## Info 
Showcase for migration of embedded clusters using Hot Rod. 

An embedded cluster with version ```9.4``` stores caches
as POJOs, and has an embedded Hot Rod server that is used by a second cluster running ```11.0.x``` to migrate data without downtime.

## Running the source cluster

Start one of more nodes with: 

```
cd source/
mvn exec:java
```

To avoid port conflict in successive nodes, use the ```offset``` JVM property, e.g.: 

```mvn clean install -Doffset=1000 exec:java```

## Running the target cluster

Start one or more nodes with:

```
cd destination/
mvn exec:java
```

To avoid port conflict, the JVM property ```offset``` can also be used, similarly to the source cluster. 

## Migrating data

Since the destination cluster exposes a REST server, the rolling upgrade can be triggered with:

```
curl "http://localhost:8888/rest/v2/caches/cache?action=sync-data"
```

Afterwards the remote store can be disconnected:

```
curl "http://localhost:8888/rest/v2/caches/cache?action=disconnect-source"
```

The source cluster can be stopped and the data should be available in the destination cluster. Check with:

```
curl "http://localhost:8888/rest/v2/caches/cache?action=keys"
```


