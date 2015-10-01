package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DecoderInputStream extends InputStream {
	ByteArrayInputStream in;
	byte[] dataRead;
	byte[] result;

	public DecoderInputStream(byte[] dataRead) {
		super();
		this.in = new ByteArrayInputStream(dataRead);
		this.dataRead = dataRead;
	}

	@Override
	public int read() throws IOException {
		int c= in.read();
		return c ;
	}

	public int read(byte[] data) {
		int b = 0;
		
		
		return b;
	}

}
