package com.conleos.views.profile;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.core.Session;
import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;
import com.conleos.i18n.Lang;
import com.conleos.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@PageTitle("Profile")
@Route(value = "profile", layout = MainLayout.class)
public class ProfileView extends VerticalLayout{

    private Div imageContainer;

    public ProfileView(UserService service) {
        Session session = Session.getSessionFromVaadinSession(VaadinSession.getCurrent());

        add(createContent(session.getUser()));
    }

    private Component createContent(User user) {
        TextField firstName = new TextField(Lang.translate("view.profile.label.firstname"));
        firstName.setValue(user.getFirstName());
        firstName.setEnabled(false);
        TextField lastName = new TextField(Lang.translate("view.profile.label.lastname"));
        lastName.setValue(user.getLastName());
        lastName.setEnabled(false);
        Dialog dialog = new Dialog();
        H2 changeHeader = new H2(Lang.translate("view.profile.button.changePassword"));
        Text sure = new Text(Lang.translate("view.profile.textSure"));
        PasswordField oldPasswordField = new PasswordField(Lang.translate("view.profile.label.oldPassword"));
        PasswordField newPasswordField = new PasswordField(Lang.translate("view.profile.label.newPassword"));
        Text wrong = new Text(Lang.translate("view.profile.textWrong"));
        VerticalLayout input = new VerticalLayout();
        input.add(changeHeader,sure,oldPasswordField,newPasswordField);
        dialog.add(input);
        Button changeButton = new Button(Lang.translate("view.profile.button.change"));
        changeButton.addClickListener(clickEvent -> {
            if (PasswordHasher.hash(oldPasswordField.getValue()).equals(user.getPasswordHash())) {
                System.out.println(PasswordHasher.hash(newPasswordField.getValue()));
                UserService.getInstance().setNewPassword(PasswordHasher.hash(newPasswordField.getValue()), user.getId());
                dialog.close();
            } else {
                input.add(wrong);
            }
        });
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        changeButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(changeButton);
        Button cancelButton = new Button(Lang.translate("view.profile.button.cancel"), (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        Button change = new Button(Lang.translate("view.profile.button.changePassword"), e -> dialog.open());

        imageContainer = new Div();

        VerticalLayout layout = new VerticalLayout(firstName, lastName, dialog, change);

        if (!user.getRole().equals(Role.Trainee)) {
            layout.add(new H3("Signature"));
            layout.add(imageContainer);

            createSignImage(user);

            MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
            Upload upload = new Upload(buffer);
            upload.setAcceptedFileTypes("image/jpeg","image/jpg", "image/png", "image/gif");
            upload.addSucceededListener(event -> {
                String attachmentName = event.getFileName();
                try {
                    // The image can be jpg png or gif, but we store it always as png file in this example
                    BufferedImage inputImage = ImageIO.read(buffer.getInputStream(attachmentName));
                    ByteArrayOutputStream pngContent = new ByteArrayOutputStream();
                    ImageIO.write(inputImage, "png", pngContent);
                    user.setSignatureImage(pngContent.toByteArray());
                    UserService.getInstance().saveUser(user);
                    createSignImage(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            layout.add(upload);
        }

        return layout;
    }

    private void createSignImage(User user) {
        Image image = user.generateSignImage();
        if (image != null) {
            image.setHeight("100%");
            imageContainer.removeAll();
            imageContainer.add(image);
        }
    }

}