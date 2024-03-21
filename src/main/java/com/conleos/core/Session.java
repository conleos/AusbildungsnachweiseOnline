package com.conleos.core;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.vaadin.flow.server.VaadinSession;

import java.util.HashMap;
import java.util.Map;

/*
* A Session is used to ensure a Client is authenticated.
*/
public class Session {

    private final static Map<VaadinSession, Session> sessions = new HashMap<>();

    private User user;

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

        User user = UserService.getInstance().getUserByUsername(username);

        if (user == null) {
            return null;
        }

        if (user.getPasswordHash().equals(passwordHash)) {
            return createSession(VaadinSession.getCurrent(), user);
        }

        return null;
    }

    /* Returns a new Session, that is mapped to the current VaadinSession. */
    private static Session createSession(VaadinSession vaadinSession, User user) {
        Session session = new Session();

        session.setUser(user);

        sessions.put(vaadinSession, session);
        return session;
    }

    public Role getSessionRole() {
        return user.getRole();
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
    }
}
