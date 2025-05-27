package br.com.testDesign.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Table(name = "tb_role")
public class RoleEntity extends BasicEntity implements GrantedAuthority {

    private String authority;

    public RoleEntity(){}

    public RoleEntity(String authority) {
        this.authority = authority;
    }

    public RoleEntity(long id, String authority) {
        super(id);
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity role = (RoleEntity) o;
        return getId() == role.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
