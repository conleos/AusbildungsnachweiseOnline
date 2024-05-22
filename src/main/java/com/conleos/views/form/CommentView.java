package com.conleos.views.form;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.conleos.data.entity.Form;
import com.conleos.core.Session;
import com.conleos.data.entity.Comment;
import com.conleos.data.entity.User;
import com.conleos.data.service.CommentService;
import com.conleos.data.service.UserService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class CommentView extends Div {

    private VerticalLayout chatLayout;

    public CommentView(Form form) {
        MessageList list = new MessageList();
        list.setWidth("100%");
        MessageInput input = new MessageInput();
        input.setWidth("100%");
        input.addSubmitListener(submitEvent -> {
            MessageListItem newMessage = new MessageListItem(submitEvent.getValue(), Instant.now(), Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser().getFullName());
            newMessage.setUserColorIndex(6);
            newMessage.addClassNames("current-user-message");
            List<MessageListItem> items = new ArrayList<>(list.getItems());
            items.add(newMessage);
            Comment comment = new Comment();
            comment.setForm(form);
            comment.setTime(newMessage.getTime().toString());
            comment.setUserId(Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser().getId());
            comment.setComment(newMessage.getText());
            CommentService.getInstance().saveComment(comment);
            list.setItems(items);
        });

        List<Comment> comments = CommentService.getInstance().getAllCommentsByForm(form.getId());

        if (!comments.isEmpty()) {
            List<MessageListItem> oldItems = new ArrayList<>();
            for (Comment comment : comments) {
                User author = UserService.getInstance().getUserByID(comment.getUserId());
                MessageListItem message = new MessageListItem(comment.getComment(), Instant.parse(comment.getTime()), author != null ? author.getFullName() : "Unkown");

                if (message.getUserName().equals(Session.getSessionFromVaadinSession(VaadinSession.getCurrent()).getUser().getFullName())) {
                    message.addClassNames("current-user-message");
                    message.setUserColorIndex(6);
                } else {
                    message.setUserColorIndex(50);
                }
                oldItems.add(message);
            }
            list.setItems(oldItems);
        }

        chatLayout = new VerticalLayout(list, input);
        chatLayout.setHeight("500px");
        chatLayout.setWidth("100%");
        chatLayout.expand(list);
        chatLayout.addClassName(LumoUtility.Border.TOP);


    }

    public VerticalLayout getChatLayout() {
        return chatLayout;
    }
}
