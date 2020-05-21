package cryptographyAlgorithm;

import java.io.*;
import java.security.MessageDigest;

public class MD5Checksum {
	/**
	 * convert byte[] to md5 hash code
	 * @param filename : path to file
	 * @return 
	 * @throws Exception
	 */
   public static byte[] createChecksum(String filename) throws Exception {
       InputStream fis =  new FileInputStream(filename);

       byte[] buffer = new byte[1024];
       MessageDigest complete = MessageDigest.getInstance("MD5");
       int numRead;

       do {
           numRead = fis.read(buffer);
           if (numRead > 0) {
               complete.update(buffer, 0, numRead);
           }
       } while (numRead != -1);

       fis.close();
       return complete.digest();
   }

   /**
    * Return a string in hex-format represent for verify integrity file
    * @param filename
    * @return
    * @throws Exception
    */
   public static String getMD5Checksum(String filename) throws Exception {
       byte[] b = createChecksum(filename);
       String result = "";

       for (int i=0; i < b.length; i++) {
           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
       }
       return result;
   }

}