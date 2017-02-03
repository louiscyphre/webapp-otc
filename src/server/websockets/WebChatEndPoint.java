package server.websockets;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import server.AppConstants;

/**
 * Example of a simple Server-side WebSocket end-point that managed a chat between 
 * several client end-points
 * @author haggai
 */
@ServerEndpoint("/chat/{username}")
public class WebChatEndPoint {
	
	//tracks all active chat users
    private static Map<Session,String> chatUsers = Collections.synchronizedMap(new HashMap<Session,String>()); 
    
    /**
     * Joins a new client to the chat
     * @param session 
     * 			client end point session
     * @throws IOException
     */
    @OnOpen
    public void joinChat(Session session, @PathParam("username") String username) throws IOException{
    	try {
    		if (session.isOpen()) {
    			//add new client to managed chat sessions
    	    	chatUsers.put(session, username);
        		//notify everyone that a new client has join the chat
        		doNotify(null,"User <span class='username'>"+username+"</span> has joined the chat...", null);
        		//welcome the new client
    			session.getBasicRemote().sendText("Welcome <span class='username'>"+username +
        			                          "</span>. There are currently "+chatUsers.size()+" participants in this chat.");
    		}
    	} catch (IOException e) {
    		session.close();
    	}
    }

    /**
     * Message delivery between chat participants
     * @param session
     * 			client end point session
     * @param msg
     * 			message to deliver		
     * @throws IOException
     */
    @OnMessage
    public void deliverChatMessege(Session session, String msg) throws IOException{
        try {
            if (session.isOpen()) {
               //deliver message
               String user = chatUsers.get(session);
               doNotify(user, msg, null);
            }
        } catch (IOException e) {
                session.close();
        }
    }
    
    /**
     * Removes a client from the chat
     * @param session
     * 			client end point session
     * @throws IOException
     */
    @OnClose
    public void leaveChat(Session session) throws IOException{
    	try {
    		String user = chatUsers.remove(session);//fake user just for removal
    		//let other participants know that client has left the chat
    		doNotify(null,"User <span class='username'>"+user+"</span> has left the chat...",session);
    	} catch (IOException e) {
    		session.close();
    	} 
    }

    /*
     * Helper method for message delivery to chat participants. skip parameter is used to avoid delivering a message 
     * to a certain client (e.g., one that has just left) 
     */
    private void doNotify(String author, String message, Session skip) throws IOException{
    	for (Entry<Session,String> user : chatUsers.entrySet()){
    		Session session = user.getKey();
    		if (!session.equals(skip) && session.isOpen()){
    			session.getBasicRemote().sendText((author != null ? "&gt&gt <span class='username'>"+author+"</span>: " : "")+ message+ " ("+new Date()+")");
    		}
    	}
    }

}
