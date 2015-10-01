package server;

import java.io.IOException;
import java.util.List;

public class Chunk_encoder implements Processor {
	private int sizeOfchunk;

	public Chunk_encoder(int sizeOfchunk) {
		this.sizeOfchunk = sizeOfchunk;
	}

	public int getSizeOfchunk() 
	{
		return sizeOfchunk;
	}

	public void setSizeOfchunk(int sizeOfchunk) {
		this.sizeOfchunk = sizeOfchunk;
	}

	@Override
	public byte[] process(byte[] data, List<String> headers) {
		try (EncoderOutputStream eos = new EncoderOutputStream(sizeOfchunk)) {
			eos.write(data);
			
			
			headers.add("Transfer-Encoding: chunked\r\n");
			return eos.toByteArray();
		} catch (IOException e) {
			System.out.println(e);
			System.out.println("ERROR");
			return null;
		}

	}

}
