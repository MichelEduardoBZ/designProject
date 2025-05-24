package br.com.testDesign.dto.user;

import br.com.testDesign.dto.BasicDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class UserDTO extends BasicDTO {

    @NotBlank(message = "Campo obrigatório.")
    private String firstName;
    private String lastName;

    @Email(message = "Favor entrar um email válido.")
    private String email;

    private Set<RoleDTO> roles  = new HashSet<>();

    public UserDTO(){}

    public UserDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserDTO(Long id, String firstName, String lastName, String email) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }
}
