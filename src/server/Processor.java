package server;

import java.util.List;

/**
 * interface for modules that compress data and
 * process data with chunking *
 */

public interface Processor {
    byte[] process(byte[] data, List<String> headers);
}