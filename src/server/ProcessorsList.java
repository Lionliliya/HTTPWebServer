package server;

import java.util.ArrayList;
import java.util.List;

public class ProcessorsList implements Processor {
    /**
     * прочитали данные из жесткого диска (res), потом - один Processor сначала
     * архивирует данные, следующий - обрабатывает чанкером
     */
    private List<Processor> list = new ArrayList<Processor>();

    /**
     * add our Compressor and Chunker classes*
     */

    public void add(Processor p) {
        list.add(p);
    }

    public byte[] process(byte[] data, List<String> headers) {

        byte[] res = data;
        /**for data that had been read from the disk are processed by compressor
         * first, then by chunker **/

        for (Processor p : list)
            res = p.process(res, headers);

        return res;
    }
}
