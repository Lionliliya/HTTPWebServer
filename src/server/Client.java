package server;

import java.lang.Exception;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

public class Client implements Runnable {
    private Socket socket;
    private FileManager fm;

    public Client(Socket socket, String path) {
        this.socket = socket;
        fm = new FileManager(path);
    }

    private void returnStatusCode(int code, OutputStream os) throws IOException {
        String msg = null;

        switch (code) {
            case 400:
                msg = "HTTP/1.1 400 Bad Request";
                break;
            case 404:
                msg = "HTTP/1.1 404 Not Found";
                break;
            case 500:
                msg = "HTTP/1.1 500 Internal Server Error";
                break;
        }

        byte[] resp = msg.concat("\r\n\r\n").getBytes();
        os.write(resp);
    }

    /**
     * переобразует заголовки в байтовый масив для передачи на сокет *
     */
    private byte[] getBinaryHeaders(List<String> headers) {
        StringBuilder res = new StringBuilder();

        for (String s : headers)
            res.append(s);

        return res.toString().getBytes();
    }

    /**
     * получаем запрос в виде строки и ссылку на OutputStream os
     * куда писать
     * ответ
     */
    private void process(String request, OutputStream os) throws IOException {
        System.out.println(request);
        System.out.println("---------------------------------------------");
        /**take only the first line of request - till first end of
         *  line**/
        int idx = request.indexOf("\r\n");
        request = request.substring(0, idx);

        String[] parts = request.split(" ");
        if (parts.length != 3) {
            returnStatusCode(400, os); /*according to the protocol it is
                                        a error 404*/
            return;
        }

        String method = parts[0], url = parts[1], version = parts[2];

        if ((!version.equalsIgnoreCase("HTTP/1.0"))
                && (!version.equalsIgnoreCase("HTTP/1.1"))) {
            returnStatusCode(400, os);
            return;
        }
        if (!method.equalsIgnoreCase("GET")) { /*this server works only with get method*/
            returnStatusCode(400, os);
            return;
        }
        if ("/".equals(url))
            url = "/index.html";
/**first line for response**/
        List<String> headers = new ArrayList<String>();
        headers.add("HTTP/1.1 200 OK\r\n");

        byte[] content = fm.get(url);

        if (content == null) {
            returnStatusCode(404, os);
            return;
        }

        ProcessorsList pl = new ProcessorsList();
        pl.add(new Compressor(6));
        pl.add(new Chunk_encoder(30)); // comment
        content = pl.process(content, headers);
/**If the processor in the case of exception return null**/
        if (content == null) {
            returnStatusCode(500, os);
            return;
        }

        // uncomment next line
        // headers.add("Content-Length: " + content.length + "\r\n");
        headers.add("Connection: close\r\n\r\n");

        os.write(getBinaryHeaders(headers));
        os.write(content);
    }

    public void run() {
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            byte[] buf;
            byte[] temp;
            int r, len;

            try {
                do {
                    len = is.available();
                    buf = new byte[len];
                    if ((r = is.read(buf)) <= 0)
                        break;

                    bs.write(buf, 0, r);
                    temp = bs.toByteArray();

                    /**in a lop find two ends of line. It means that the body of a
                     *  request is end **/

                    for (int i = 0; i < temp.length - 3; i++) {
                        if ((temp[i] == (byte) 13)
                                && (temp[i + 1] == (byte) 10)
                                && (temp[i + 2] == (byte) 13)
                                && (temp[i + 3] == (byte) 10)) {
                            String request = new String(temp, 0, i);
                            process(request, os);
                        }
                    }
                } while (!Thread.currentThread().isInterrupted());
            } finally {
                socket.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}