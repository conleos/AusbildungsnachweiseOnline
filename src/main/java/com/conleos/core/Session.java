package com.conleos.core;

import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;

/*
* A Session is used to ensure a Client is authenticated.
*/
public class Session {

    private static Map<VaadinSession, Session> sessions = new HashMap<>();

    /*
    * Used to validate a User login.
    * returns the Server-side Session object if a valid Session exist for this connection.
    * returns null otherwise. */
    public static Session getSessionFromVaadinSession(VaadinSession vaadinSession) {
        return sessions.getOrDefault(vaadinSession, null);
    }

    /*
    * Authenticate a Client with a Username and a password Hash.
    * Hash Function: Java build-in hashCode()
    * Returns a new Session, that is mapped to the current VaadinSession. */
    public static Session authenticateUserAndCreateSession(String username, String passwordHash) {

        // TODO: Placeholder and testing only !
        if (username.equals("admin") && passwordHash.equals(String.valueOf(new String("1234").hashCode()))) {
            return createSession(VaadinSession.getCurrent());
        }

        return null;
    }

    /* Returns a new Session, that is mapped to the current VaadinSession. */
    private static Session createSession(VaadinSession vaadinSession) {
        Session session = new Session();

        sessions.put(vaadinSession, session);
        return session;
    }

}
