package com.conleos.core;

import com.conleos.views.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class AuthenticationController implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(new BeforeEnterListener() {
                @Override
                public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

                    // Validate User Session
                    if (Session.getSessionFromVaadinSession(VaadinSession.getCurrent()) == null) {
                        beforeEnterEvent.rerouteTo(LoginView.class);
                    }

                }
            });
        });
    }
}
