package websocket;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@ServerEndpoint("/receive/fileserver")
public class FileServer {
    static File uploadedFile = null;
    static String fileName = null;
    static FileOutputStream fos = null;
    final static String filePath = System.getProperty("java.io.tmp");
    int fCount = 0;

    @OnOpen
    public void open(Session session, EndpointConfig conf) {
        System.out.println("Websocket server open");
        session.setMaxBinaryMessageBufferSize(1024 * 1024);
    }

    @OnMessage
    public void processUpload(ByteBuffer msg, boolean last, Session session) {
        System.out.println("Binary Data: " + fCount + ", Capacity: " + msg.capacity());
        fCount++;
        while (msg.hasRemaining()) {
            try {
                fos.write(msg.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnMessage
    public void message(Session session, String msg) {
        System.out.println("got msg: " + msg);
        if (!msg.equals("end")) {
            fileName = msg.substring(msg.indexOf(':') + 1);
            uploadedFile = new File(filePath + fileName);
            try {
                fos = new FileOutputStream(uploadedFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void close(Session session, CloseReason reason) {
        System.out.println("socket closed: " + reason.getReasonPhrase());
    }

    @OnError
    public void error(Session session, Throwable t) {
        t.printStackTrace();

    }
}
