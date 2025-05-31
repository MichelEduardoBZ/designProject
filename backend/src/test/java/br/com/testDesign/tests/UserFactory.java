package br.com.testDesign.tests;

import br.com.testDesign.dto.user.UserDTO;
import br.com.testDesign.entities.RoleEntity;
import br.com.testDesign.entities.UserEntity;

public class UserFactory {

    public static UserEntity createClientUser() {
        UserEntity user = new UserEntity(1L, "Michel", "Eduardo", "michel.zeschau@gmail.com", "'$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(1L, "ROLE_OPERATOR"));
        return user;
    }

    public static UserEntity createAdminUser() {
        UserEntity user = new UserEntity(2L, "Alex", "Bob", "alex@gmail.com", "'$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(2L, "ROLE_ADMIN"));
        return user;
    }
    public static UserEntity createCustomClientUser(Long id, String username) {
        UserEntity user = new UserEntity(id, "Michel", "Eduardo", username, "'$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(1L, "ROLE_OPERATOR"));
        return user;
    }

    public static UserEntity createCustomAdminUser(Long id, String username) {
        UserEntity user = new UserEntity(id, "Alex", "Bob", username, "'$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG");
        user.addRole(new RoleEntity(2L, "ROLE_ADMIN"));
        return user;
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(1L, "Michel", "Eduardo", "michel@gmail.com");
    }
}
