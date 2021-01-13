package org.infinispan.sample.destination;

import static org.infinispan.client.hotrod.ProtocolVersion.PROTOCOL_VERSION_29;

import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.remote.configuration.RemoteStoreConfigurationBuilder;
import org.infinispan.rest.RestServer;
import org.infinispan.rest.configuration.RestServerConfiguration;
import org.infinispan.rest.configuration.RestServerConfigurationBuilder;
import org.infinispan.sample.SerializationCtx;
import org.infinispan.sample.Util;

public class MainApp {

   public static final String CACHE_NAME = "cache";

   public static void main(String[] args) {
      System.setProperty("infinispan.deserialization.whitelist.regexps", ".*");
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.clustering().cacheMode(CacheMode.DIST_SYNC);
      builder.encoding().key().mediaType(MediaType.APPLICATION_OBJECT_TYPE);
      builder.encoding().value().mediaType(MediaType.APPLICATION_OBJECT_TYPE);
      RemoteStoreConfigurationBuilder store = builder.persistence().addStore(RemoteStoreConfigurationBuilder.class);
      store.hotRodWrapping(false).rawValues(false)
            .marshaller(JavaSerializationMarshaller.class)
            .protocolVersion(PROTOCOL_VERSION_29)
            .remoteCacheName(CACHE_NAME).shared(true)
            .addServer().host("localhost").port(11222);

      GlobalConfigurationBuilder gcb = GlobalConfigurationBuilder.defaultClusteredBuilder();
      gcb.serialization().addContextInitializer(SerializationCtx.INSTANCE);
      GlobalConfiguration globalConfiguration = gcb.defaultCacheName("default").build();
      DefaultCacheManager cacheManager = new DefaultCacheManager(globalConfiguration, new ConfigurationBuilder().build(), true);

      cacheManager.defineConfiguration(CACHE_NAME, builder.build());

      RestServerConfiguration restServerConfiguration = new RestServerConfigurationBuilder()
            .host("0.0.0.0")
            .port(Util.getPortOffset() + 8888)
            .build();

      RestServer restServer = new RestServer();
      restServer.start(restServerConfiguration, cacheManager);
      System.out.println("Server REST started on port " + restServer.getPort());

   }
}
