package fr.chklang.glink.application.rest;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocketListener;

public class GLinkWebSocket extends DefaultWebSocket {

	public GLinkWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener[] listeners) {
		super(protocolHandler, request, listeners);
	}

}
