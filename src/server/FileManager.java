package server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager implements Runnable {
    private String path;
    public long timestart = 0;
    long lifetime = 60000;
    Thread t;

    /**
     * реализация хэша, так как сервер работает в многопоточном режиме, каждого
     * клиента обрабатывает в отдельном потоке статик - так как кэш для все
     * общий будет, а для каждого клиента будет создаваться файл-менеджер
     **/
    /**
     * String - path to  our file on disk, byte - content of file
     * in bytes
     */
    private static ConcurrentHashMap<String, byte[]> map = new ConcurrentHashMap<String, byte[]>();

    /**
     * отвечает за загрузку данных с файловой системы. Удобно - один раз
     * загрузить данные с жесткого диска, поместить в кэш, и некоторое время эти
     * данные будут доступны пользователям. Каждый раз загружать их с диска -
     * ресурсоемкая задача
     */

    public FileManager(String path) {
        // "c:\folder\" --> "c:\folder"
        /**
         * path - путь к катологу, где лежат наши файли которые будут отдаваться
         * сервером клиенту
         **/
        if (path.endsWith("/") || path.endsWith("\\"))
            path = path.substring(0, path.length() - 1);

        this.path = path;
        t = new Thread(this);

    }

    /**
     * по адресу String возвращает содержимое файла *
     */
    public byte[] get(String url) {

        try {

            byte[] buf = map.get(url);
            if (buf != null)  // in cache
                return buf;

            // "c:\folder" + "/index.html" -> "c:/folder/index.html"
            String fullPath = path.replace('\\', '/') + url;

            RandomAccessFile f = new RandomAccessFile(fullPath, "r");
            try {
                buf = new byte[(int) f.length()];
                f.read(buf, 0, buf.length);
            } finally {
                f.close();
            }

            map.put(url, buf); // put to cache
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss ");
            timestart = new Date().getTime();
            System.out.println("Time : " + dateFormat.format(timestart) + " file in cache");
            t.start();

            return buf;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (System.currentTimeMillis() >= timestart + lifetime) {
                map.clear();
                System.out.println("Cache is clear");
                break;

            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
