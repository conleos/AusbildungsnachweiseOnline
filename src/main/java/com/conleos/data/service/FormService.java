package com.conleos.data.service;

import com.conleos.common.PasswordHasher;
import com.conleos.common.Role;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.repository.FormRepository;
import com.conleos.data.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FormService {
    private static FormService instance;

    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        instance = this;
        this.formRepository = formRepository;
    }

    public static FormService getInstance() {
        return instance;
    }

    public List<Form> getAllForms() {
        return formRepository.getAllForms();
    }

    public List<Integer> getAllFormIDs() {
        return formRepository.getAllFormChannels();
    }

    public void saveForm(Form form) {
        Form.FormEntry monday = new Form.FormEntry(form.getMonday()); form.setMonday(null);
        Form.FormEntry tuesday = new Form.FormEntry(form.getTuesday()); form.setTuesday(null);
        Form.FormEntry wednesday = new Form.FormEntry(form.getWednesday()); form.setWednesday(null);
        Form.FormEntry thursday = new Form.FormEntry(form.getThursday()); form.setThursday(null);
        Form.FormEntry friday = new Form.FormEntry(form.getFriday()); form.setFriday(null);
        Form.FormEntry saturday = new Form.FormEntry(form.getSaturday()); form.setSaturday(null);
        Form.FormEntry sunday = new Form.FormEntry(form.getSunday()); form.setSunday(null);
        formRepository.saveAndFlush(form);

        form.setMonday(monday);
        form.setTuesday(tuesday);
        form.setWednesday(wednesday);
        form.setThursday(thursday);
        form.setFriday(friday);
        form.setSaturday(saturday);
        form.setSunday(sunday);
        formRepository.saveAndFlush(form);
    }

    public Form getFormByID(Long id) {
        List<Form> temp = formRepository.getFormByChannel(id);
        return temp.isEmpty() ? null : temp.getFirst();
    }

    public List<Form> getFormsByOwner(User owner) {
        return formRepository.getFormsByOwner(owner);
    }

    public Form getFormByDateAndUser(LocalDate date, User user) {
        List<Form> temp = formRepository.getFormsByDateAndUser(date, user);
        return temp.isEmpty() ? null : temp.getFirst();
    }

    public void deleteFormsOfUser(User user) {
        List<Form> forms = getFormsByOwner(user);
        for (Form form : forms) {
            CommentService.getInstance().deleteCommentsByForm(form.getId());
            formRepository.delete(form);
        }
    }
}
