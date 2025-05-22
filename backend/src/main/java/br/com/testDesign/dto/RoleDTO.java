package br.com.testDesign.dto;

public class RoleDTO extends BasicDTO {

    private String authority;

    public RoleDTO(){}

    public RoleDTO(String authority) {
        this.authority = authority;
    }

    public RoleDTO(Long id, String authority) {
        super(id);
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
