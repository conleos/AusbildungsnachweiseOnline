package com.conleos.views.dashboard;

import com.conleos.common.Role;
import com.conleos.data.entity.FormStatus;
import com.conleos.i18n.Lang;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;

public class DashboardFormFilter extends VerticalLayout {

    private final Button button;
    private Dialog dialog;
    private final Button applyFiltersButton;
    private boolean hideInProgressForms = false;
    private boolean hideInReviewForms = false;
    private boolean hideSignedForms = false;
    private boolean hideRejectedForms = false;

    public DashboardFormFilter(Role roleOfViewer) {
        if (roleOfViewer.equals(Role.Trainee)) {
            hideSignedForms = true;
            hideInReviewForms = true;
        } else {
            hideInProgressForms = true;
            hideSignedForms = true;
            hideRejectedForms = true;
        }

        button = new Button(Lang.translate("view.dashboardFilter.button.filter"), VaadinIcon.FILTER.create());
        button.addClickListener(event -> dialog.open());
        dialog = new Dialog(Lang.translate("view.dashboardFilter.button.filter.label"));
        dialog.setWidth("45%");

        Checkbox hideInProgressFormsCheckBox = new Checkbox(Lang.translate("view.dashboardFilter.hideInProgress"));
        hideInProgressFormsCheckBox.setValue(hideInProgressForms);
        dialog.add(hideInProgressFormsCheckBox);

        Checkbox hideInReviewFormsCheckBox = new Checkbox(Lang.translate("view.dashboardFilter.hideInReview"));
        hideInReviewFormsCheckBox.setValue(hideInReviewForms);
        dialog.add(hideInReviewFormsCheckBox);

        Checkbox hideSignedFormsCheckBox = new Checkbox(Lang.translate("view.dashboardFilter.hideSigned"));
        hideSignedFormsCheckBox.setValue(hideSignedForms);
        dialog.add(hideSignedFormsCheckBox);

        Checkbox hideRejectedFormsCheckBox = new Checkbox(Lang.translate("view.dashboardFilter.hideRejected"));
        hideRejectedFormsCheckBox.setValue(hideRejectedForms);
        dialog.add(hideRejectedFormsCheckBox);

        Button cancelButton = new Button(Lang.translate("view.dashboardFilter.button.cancel"));
        cancelButton.addClickListener(event -> {
            dialog.close();
            hideInProgressFormsCheckBox.setValue(hideInProgressForms);
            hideInReviewFormsCheckBox.setValue(hideInReviewForms);
            hideSignedFormsCheckBox.setValue(hideSignedForms);
            hideRejectedFormsCheckBox.setValue(hideRejectedForms);
        });
        applyFiltersButton = new Button(Lang.translate("view.dashboardFilter.button.apply"));
        applyFiltersButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        applyFiltersButton.addClickListener(event -> {
            dialog.close();
            hideInProgressForms = hideInProgressFormsCheckBox.getValue();
            hideInReviewForms = hideInReviewFormsCheckBox.getValue();
            hideSignedForms = hideSignedFormsCheckBox.getValue();
            hideRejectedForms = hideRejectedFormsCheckBox.getValue();
        });
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(applyFiltersButton);
    }

    public void filterAndSortFormCards(List<FormCard> cards) {
        if (hideInProgressForms) {
            cards.removeIf(formCard -> formCard.getForm() != null && formCard.getForm().getStatus().equals(FormStatus.InProgress));
        }
        if (hideInReviewForms) {
            cards.removeIf(formCard -> formCard.getForm() != null && formCard.getForm().getStatus().equals(FormStatus.InReview));
        }
        if (hideSignedForms) {
            cards.removeIf(formCard -> formCard.getForm() != null && formCard.getForm().getStatus().equals(FormStatus.Signed));
        }
        if (hideRejectedForms) {
            cards.removeIf(formCard -> formCard.getForm() != null && formCard.getForm().getStatus().equals(FormStatus.Rejected));
        }
    }

    public Button getButton() {
        return button;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public Button getApplyFiltersButton() {
        return applyFiltersButton;
    }


}
