package com.conleos.views.form;

import com.conleos.data.entity.Form;
import com.conleos.data.entity.FormStatus;
import com.conleos.data.service.FormService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.util.List;

public class InstructorFormNextButton extends Button {

    public InstructorFormNextButton(Form currentForm) {
        super("Next Form to be reviewed", VaadinIcon.ARROW_RIGHT.create());
        addClickListener(event -> {
            List<Form> forms = FormService.getInstance().getFormsByOwner(currentForm.getOwner());
            for (Form form : forms) {
                if (form.getId().equals(currentForm.getId())) {
                    continue;
                }
                if (form.getStatus().equals(FormStatus.InReview)) {
                    // We need to trigger a Page Reload
                    UI.getCurrent().access(() -> UI.getCurrent().navigate("/"));
                    UI.getCurrent().access(() -> UI.getCurrent().navigate("/form/" + form.getId()));
                    return;
                }
            }
            Notification.show(currentForm.getOwner().getFullName() + " has no further Forms in review!", 4000, Notification.Position.BOTTOM_CENTER).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
    }

}
