package com.conleos.views.dashboard;

import com.conleos.data.entity.FormStatus;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.Locale;

public class DashboardFormFilter extends VerticalLayout {

    private final Button button;
    private Dialog dialog;
    private final Button applyFiltersButton;
    private boolean hideInProgressForms = false;
    private boolean hideInReviewForms = false;
    private boolean hideSignedForms = false;
    private boolean hideRejectedForms = false;
    private Locale locale = UI.getCurrent().getLocale();

    public DashboardFormFilter() {
        button = new Button(getTranslation("view.dashboardFilter.button.filter", locale), VaadinIcon.FILTER.create());
        button.addClickListener(event -> dialog.open());
        dialog = new Dialog(getTranslation("view.dashboardFilter.button.filter.label", locale));
        dialog.setWidth("45%");

        Checkbox hideInProgressFormsCheckBox = new Checkbox(getTranslation("view.dashboardFilter.hideInProgress", locale));
        hideInProgressFormsCheckBox.setValue(hideInProgressForms);
        dialog.add(hideInProgressFormsCheckBox);

        Checkbox hideInReviewFormsCheckBox = new Checkbox(getTranslation("view.dashboardFilter.hideInReview", locale));
        hideInReviewFormsCheckBox.setValue(hideInReviewForms);
        dialog.add(hideInReviewFormsCheckBox);

        Checkbox hideSignedFormsCheckBox = new Checkbox(getTranslation("view.dashboardFilter.hideSigned", locale));
        hideSignedFormsCheckBox.setValue(hideSignedForms);
        dialog.add(hideSignedFormsCheckBox);

        Checkbox hideRejectedFormsCheckBox = new Checkbox(getTranslation("view.dashboardFilter.hideRejected", locale));
        hideRejectedFormsCheckBox.setValue(hideRejectedForms);
        dialog.add(hideRejectedFormsCheckBox);

        Button cancelButton = new Button(getTranslation("view.dashboardFilter.button.cancel", locale));
        cancelButton.addClickListener(event -> {
            dialog.close();
            hideInProgressFormsCheckBox.setValue(hideInProgressForms);
            hideInReviewFormsCheckBox.setValue(hideInReviewForms);
            hideSignedFormsCheckBox.setValue(hideSignedForms);
            hideRejectedFormsCheckBox.setValue(hideRejectedForms);
        });
        applyFiltersButton = new Button(getTranslation("view.dashboardFilter.button.apply", locale));
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
