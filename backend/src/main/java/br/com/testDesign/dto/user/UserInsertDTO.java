package br.com.testDesign.dto.user;

import br.com.testDesign.services.validation.user.UserInsertValid;

@UserInsertValid
public class UserInsertDTO extends UserDTO {

    private String password;

    public UserInsertDTO() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}