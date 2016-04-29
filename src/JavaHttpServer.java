
package javahttpserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    A very simple Java HTTP Server. Run the program, open a browser and type
    http://localhost:5555/video (or /audio) to test/use.   
 */
public class JavaHttpServer {

    static final int RESPONSECODE_OK = 200;

    public static void main(String[] args) {
        try {
            // create new HttpServer object and binds it to port 5555 and localhost address; 0 is the backlog (?)
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(5555), 0);

            // create a new context, /image. contexts are used to access different objects hosted by the server.
            // new ImageHandler() is to show httpServer what class is to be used when /image is accessed. 
            httpServer.createContext("/video", new VideoHandler());
            httpServer.createContext("/audio", new AudioHandler());

            httpServer.setExecutor(null); // must be called before .start()...sets a default Executor
            httpServer.start(); // start the server

            System.out.println("Server started");
        } catch (IOException ex) {
            Logger.getLogger(JavaHttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
       ImageHandler class handles the /image context by implementing HttpHandler interface.
       It must implement the handle method.
     */
    static class VideoHandler implements HttpHandler {

        /*
            Handles access to the /video1 context. HttpExchange encapslates an http request
            and response in one exchange. It has methods for examining the request and 
            generating a response. 
         */
        @Override
        public void handle(HttpExchange he) throws IOException {

            Headers resonseHeaders = he.getResponseHeaders();
            resonseHeaders.set("Content-Type", "video/flv");

            // used to hold the video
            File video = new File("demo.flv");

            // bytes array used for input/output stream objects
            byte[] bytes = new byte[(int) video.length()];

            /*
               This is used to break the video file up and store its bytes in the bytes array.
               The .read method stores what it reads in bytes, starts at offset 0, continutes until 
               the full length of bytes array
             */
            FileInputStream inputStream = new FileInputStream(video);
            BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
            bufferedStream.read(bytes, 0, bytes.length);

            // Sends response headers with code 200 (OK) and a response body length
            // of the length of the video file; this must be called before
            // getResponseBody() is called
            he.sendResponseHeaders(HttpURLConnection.HTTP_OK, video.length());

            /*
               getResponseBody() returns a stream to which the response body must be written, which
               is the stream the outputStream writes the contents of the bytes array to.
               The write method reads from bytes starting at offset 0, and continues
               until the full length of bytes. After written to, the stream must be closed.   
             */
            OutputStream outputStream = he.getResponseBody();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.close();
        }
    }
    
    static class AudioHandler implements HttpHandler {

        
        @Override
        public void handle(HttpExchange he) throws IOException {

            Headers h = he.getResponseHeaders();
            h.set("Content-Type", "audio/mp3");
 
            File video = new File("blink.mp3");

            byte[] bytes = new byte[(int) video.length()];

            
            FileInputStream inputStream = new FileInputStream(video);
            BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
            bufferedStream.read(bytes, 0, bytes.length);

            
            he.sendResponseHeaders(RESPONSECODE_OK, video.length());

            
            OutputStream outputStream = he.getResponseBody();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.close();
        }
    }

}
