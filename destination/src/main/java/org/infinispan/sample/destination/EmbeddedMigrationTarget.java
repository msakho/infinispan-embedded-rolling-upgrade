/**
 * @author Meissa
 */
package org.infinispan.sample.destination;


import static org.infinispan.client.hotrod.ProtocolVersion.PROTOCOL_VERSION_26;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.infinispan.Cache;

import org.infinispan.commons.dataconversion.MediaType;

import org.infinispan.configuration.cache.CacheMode;

import org.infinispan.configuration.cache.ConfigurationBuilder;

import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;

import org.infinispan.jboss.marshalling.commons.GenericJBossMarshaller;
import org.infinispan.manager.DefaultCacheManager;

import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;
import org.infinispan.rest.RestServer;
import org.infinispan.rest.configuration.RestServerConfiguration;
import org.infinispan.rest.configuration.RestServerConfigurationBuilder;
import org.infinispan.sample.CustomObject;

import org.infinispan.sample.Util;
import org.infinispan.upgrade.RollingUpgradeManager;

/**
 * @author Meissa
 */
public class EmbeddedMigrationTarget {
	public static final Logger log = LogManager.getLogger(EmbeddedMigrationTarget.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Options options = new Options();
	      
	      options.addRequiredOption("cc", "cache", true, "The cache entries to sychronize");
	      options.addOption("p", "port", true, "The HotRod port on which this instance will listen on. Defaults to 11222");
	      options.addOption("b", "bind", true, "The HotRod bind address on which this instance will listen on. Defaults to 0.0.0.0");
	      CommandLineParser parser = new DefaultParser();
	      CommandLine cmd = parser.parse(options, args);
	     
	      String targetCacheName=cmd.getOptionValue("cc", "cache");
	      System.setProperty("infinispan.deserialization.whitelist.regexps", ".*");
	      ConfigurationBuilder builder = new ConfigurationBuilder();
	      builder.clustering().cacheMode(CacheMode.DIST_SYNC);
	      builder.encoding().key().mediaType(MediaType.APPLICATION_OBJECT_TYPE);
	      builder.encoding().value().mediaType(MediaType.APPLICATION_OBJECT_TYPE);
	      RemoteStoreConfigurationBuilder store = builder.persistence().addStore(RemoteStoreConfigurationBuilder.class);
	      store.hotRodWrapping(false).rawValues(false)
	            .marshaller(GenericJBossMarshaller.class)
	            .protocolVersion(PROTOCOL_VERSION_26)
	            .remoteCacheName(targetCacheName).shared(true)
	            .addServer().host(cmd.getOptionValue("b", "0.0.0.0")).port(Integer.parseInt(cmd.getOptionValue("p", "11222")));
	      

	      GlobalConfigurationBuilder gcb = GlobalConfigurationBuilder.defaultClusteredBuilder();
	      gcb.serialization().marshaller(new GenericJBossMarshaller());     
	      GlobalConfiguration globalConfiguration = gcb.build();
	      DefaultCacheManager cacheManager = new DefaultCacheManager(globalConfiguration, true);
	      cacheManager.defineConfiguration(targetCacheName, builder.build());
	      
	      RestServerConfiguration restServerConfiguration = new RestServerConfigurationBuilder()
	              .host("0.0.0.0")
	              .port(Util.getPortOffset() + 8888)
	              .build();

	        RestServer restServer = new RestServer();
	        restServer.start(restServerConfiguration, cacheManager);
	        log.info("Server REST started on port " + restServer.getPort());

	      /*Cache<Integer, CustomObject> cache = cacheManager.getCache(targetCacheName);
	      
	      RollingUpgradeManager rum = cacheManager.getGlobalComponentRegistry().getNamedComponentRegistry(targetCacheName).getLocalComponent(RollingUpgradeManager.class);
	      long migrated = rum.synchronizeData("hotrod");
	      log.info("Migrated " + migrated + " entries");
	      rum.disconnectSource("hotrod");
	      cache.forEach((key, value) -> log.info(key + " -> " + value));*/
	   }
	      

	

}
