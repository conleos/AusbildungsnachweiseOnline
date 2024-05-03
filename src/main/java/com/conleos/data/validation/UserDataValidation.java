package com.conleos.data.validation;

import com.conleos.data.entity.User;
import com.conleos.data.service.UserService;

public class UserDataValidation {

    public static class Result {
        public boolean isValid;
        public String erroMsg;

        public Result() {
            isValid = true;
            erroMsg = null;
        }

        public Result(String error) {
            isValid = false;
            erroMsg = error;
        }
    }

    public static Result validateNewUserData(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return new Result("Username should not be empty!");
        }
        if (UserService.getInstance().getUserByUsername(user.getUsername()) != null) {
            return new Result("Username '" + user.getUsername() + "' already exists!");
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            return new Result("FirstName should not be empty!");
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            return new Result("LastName should not be empty!");
        }

        if (user.getRole() == null) {
            return new Result("A Role should be selected!");
        }

        return new Result();
    }

}