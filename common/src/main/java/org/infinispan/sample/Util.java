package org.infinispan.sample;

public class Util {

   public static int getPortOffset() {
      int offset = 0;
      String offsetEnv = System.getProperty("offset");
      if (offsetEnv != null) {
         offset = offset + Integer.parseInt(offsetEnv);
      }
      return offset;
   }
}
