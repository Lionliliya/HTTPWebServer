package server;

import java.io.*;
import java.util.List;
import java.util.zip.*;

/** класс занимается сжатием данных перед отправкой **/
public class Compressor implements Processor {

	public static final int ALG_DEFLATE = 0;
	public static final int ALG_GZIP = 1;
	/**
	 * чем больше данные зжимаются - тем больше для этого требуется
	 * процессорного времени
	 **/
	private int compLevel;
	// прочитать про GZIP
	private int compAlg = ALG_GZIP;

	public Compressor(int compLevel) {
		this.compLevel = compLevel;
	}

	/** добавляються заголовки в список List - смотри стр.30,36 **/
	public byte[] process(byte[] data, List<String> headers) {
		try {
			/** сюда складывать будем результат сзжатия **/
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			if (compAlg == ALG_DEFLATE) {
				DeflaterOutputStream ds = new DeflaterOutputStream(os,
						new Deflater(compLevel));
				ds.write(data);
				ds.finish();

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