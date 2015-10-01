package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EncoderOutputStream extends OutputStream {
	private int chunkSize;
	private ByteArrayOutputStream out;

	public EncoderOutputStream(int chunkSize) {
		super();
		this.chunkSize = chunkSize;
		this.out = new ByteArrayOutputStream();
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);

	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void write(byte[] data) throws IOException {

		int n = data.length / chunkSize;
		int tail = data.length % chunkSize;
		int offset = 0;

		String head = Integer.toHexString(chunkSize) + "\r\n";

		for (int i = 0; i < n; i++) {
			out.write(head.getBytes());
			out.write(data, offset, chunkSize);
			out.write("\r\n".getBytes());
			offset += chunkSize;
		}

		if (tail > 0) {
			head = Integer.toHexString(tail) + "\r\n";
			out.write(head.getBytes());
			out.write(data, offset, tail);
			out.write("\r\n".getBytes());
		}

		out.write("0\r\n\r\n".getBytes());

	}

	public byte[] toByteArray() {
		return out.toByteArray();
	}

}
