package fr.chklang.glink.application.rest;

import java.awt.Frame;
import java.io.IOException;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;

public class GLinkWebSocketApplication extends WebSocketApplication {

    /**
     * Creates a customized {@link WebSocket} implementation.
     * 
     * @return customized {@link WebSocket} implementation - {@link ChatWebSocket}
     */
    @Override
    public WebSocket createSocket(ProtocolHandler handler,
                                  HttpRequestPacket request,
                                  WebSocketListener... listeners) {
        return new GLinkWebSocket(handler, request, listeners);
    }

    /**
     * Method is called, when {@link ChatWebSocket} receives a {@link Frame}.
     * @param websocket {@link ChatWebSocket}
     * @param data {@link Frame}
     *
     * @throws IOException
     */
    @Override
    public void onMessage(WebSocket websocket, String data) {
        // check if it's login notification
    	System.out.println("Data : " + data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConnect(WebSocket socket) {
        // do nothing
        // override this method to take control over members list
    	socket.send("Bonjour");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose(WebSocket websocket, DataFrame frame) {
    }
}
