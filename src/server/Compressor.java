package server;

import java.io.*;
import java.util.List;
/**package for data archiving**/
import java.util.zip.*;

/**
 * класс занимается сжатием данных перед отправкой *
 */
public class Compressor implements Processor {

    /**
     * two algorithm should be supported *
     */

    public static final int ALG_DEFLATE = 0;
    public static final int ALG_GZIP = 1;
    /**
     * чем больше данные зжимаются - тем больше для этого требуется
     * процессорного времени
     **/

    /**
     * In Gzip lib level of compressing from 0 to 9*
     */
    private int compLevel;
    private int compAlg = ALG_GZIP;

    public Compressor(int compLevel) {
        this.compLevel = compLevel;
    }

    /**
     * добавляються заголовки в список List - смотри стр.30,36 *
     */
    public byte[] process(byte[] data, List<String> headers) {
        try {
            /** сюда складывать будем результат сзжатия
             * the result of compressing will be located here **/
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            if (compAlg == ALG_DEFLATE) {
                DeflaterOutputStream ds = new DeflaterOutputStream(os,
                        new Deflater(compLevel));
                ds.write(data);
                ds.finish();

                /**we can find all headers and proper values in
                 * http standard**/
                headers.add("Content-Encoding: deflated\r\n");
            } else if (compAlg == ALG_GZIP) {
                GZIPOutputStream ds = new GZIPOutputStream(os);
                ds.write(data);
                ds.finish();

                headers.add("Content-Encoding: gzip\r\n");
            }

            return os.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public int getCompressionLevel() {
        return compLevel;
    }

    public void setCompressionLevel(int value) {
        compLevel = value;
    }

    public int getCompressionAlg() {
        return compAlg;
    }

    public void setCompressionAlg(int value) {
        compAlg = value;
    }
}