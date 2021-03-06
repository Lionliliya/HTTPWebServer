package server;

import java.io.*;
import java.util.List;

/**
 * to give the user data when we do not know exactly
 * data length*
 */

public class Chunker implements Processor {

    /**
     * a fixed block size for transmission *
     */
    private int chunkSize;

    public Chunker(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public byte[] process(byte[] data, List<String> headers) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            int n = data.length / chunkSize; // the number of entire blocks
            int tail = data.length % chunkSize; // the size of the tail

            int offset = 0;

            /** block length translate to a string of 16 hexadecimal
             * number**/

            String head = Integer.toHexString(chunkSize) + "\r\n";

            /**in the loop encode all whole blocs**/

            for (int i = 0; i < n; i++) {
                os.write(head.getBytes());
                os.write(data, offset, chunkSize);
                os.write("\r\n".getBytes());
                offset += chunkSize;
            }
            if (tail > 0) {
                head = Integer.toHexString(tail) + "\r\n";
                os.write(head.getBytes());
                os.write(data, offset, tail);
                os.write("\r\n".getBytes());
            }
            /** конци строк - согласто стандарту http **/

            os.write("0\r\n\r\n".getBytes());
            headers.add("Transfer-Encoding: chunked\r\n");
            return os.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int value) {
        chunkSize = value;
    }
}
