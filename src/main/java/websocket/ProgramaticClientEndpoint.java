package websocket;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class ProgramaticClientEndpoint extends Endpoint {

    private final BlockingDeque<String> queue = new LinkedBlockingDeque<>();
    volatile Session session;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                queue.add(message);
            }
        });

        session.addMessageHandler(new MessageHandler.Whole<ByteBuffer>() {
            @Override
            public void onMessage(ByteBuffer buffer) {
                byte[] bytes;
                if(buffer.hasArray()) {
                    bytes = buffer.array();
                } else {
                    bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                }
                String message = new String(bytes, Charset.forName("UTF-8"));
                queue.add(message);
            }
        });




    }

    public String getMessage() throws InterruptedException {
        String message = queue.poll(10, TimeUnit.SECONDS);
        if (message == null) {
            throw new IllegalStateException("No message in the queue in specified timeout");
        }
        return message;
    }
}
