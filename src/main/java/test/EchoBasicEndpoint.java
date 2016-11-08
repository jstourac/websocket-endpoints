/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(EchoBasicEndpoint.URL_PATTERN)
public class EchoBasicEndpoint {
    public static final String URL_PATTERN = "/echoBasicEndpoint";

    private static final Logger log = LoggerFactory.getLogger(EchoBasicEndpoint.class);

    Writer writer;
    OutputStream stream;

    @OnMessage
    public void handleStringMessage(final String message, Session session, boolean last) throws IOException {
        if (writer == null) {
            writer = session.getBasicRemote().getSendWriter();
        }
        writer.write(message);
        if (last) {
            writer.close();
            writer = null;
        }
    }

    @OnMessage
    public void handleByteMessage(final byte[] message, Session session, boolean last) throws IOException {
        if (stream == null) {
            stream = session.getBasicRemote().getSendStream();
        }
        stream.write(message);
        stream.flush();
        if (last) {
            stream.close();
            stream = null;
        }
    }

    @OnError
    public void onError(Throwable t) {
        log.warn("WebSockets error message detected", t);
    }

}
