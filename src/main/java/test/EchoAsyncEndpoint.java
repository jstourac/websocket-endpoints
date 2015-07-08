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

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@ServerEndpoint("/echoAsyncEndpoint")
public class EchoAsyncEndpoint {

    StringBuilder sb = null;
    ByteArrayOutputStream bytes = null;


    @OnMessage
    public void handleMessage(final String message, Session session, boolean last) throws IOException {
        if (sb == null) {
            sb = new StringBuilder();
        }
        sb.append(message);
        if (last) {
            session.getAsyncRemote().sendText(sb.toString());
            sb = null;
        }
    }

    @OnMessage
    public void handleMessage(final byte[] message, Session session, boolean last) throws IOException {
        if (bytes == null) {
            bytes = new ByteArrayOutputStream();
        }
        bytes.write(message);
        if (last) {
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(bytes.toByteArray()));
            bytes = null;
        }
    }
}
