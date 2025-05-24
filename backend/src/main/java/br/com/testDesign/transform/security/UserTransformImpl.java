package br.com.testDesign.transform.security;

import br.com.testDesign.dto.user.RoleDTO;
import br.com.testDesign.dto.user.UserDTO;
import br.com.testDesign.entities.RoleEntity;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.repositories.RoleRepository;
import br.com.testDesign.transform.TransformContractImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class UserTransformImpl extends TransformContractImpl<UserEntity, UserDTO>  implements UserTransform {

    @Autowired
    private RoleRepository roleRepository;

    public UserDTO transformToDTO(UserEntity entity) {
        UserDTO dto = transformToDTOReference(entity);
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());

        if (entity.getRoles() != null) {
            Set<RoleDTO> roleDTOs = new HashSet<>();
            for (RoleEntity roleEntity : entity.getRoles()) {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(roleEntity.getId());
                roleDTO.setAuthority(roleEntity.getAuthority());
                dto.getRoles().add(roleDTO);
            }
        }
        return dto;
    }

    @Override
    public UserDTO transformToDTOReference(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        return dto;
    }

    public UserEntity transformToEntity(UserDTO dto) {

        UserEntity entity = transformToDTOReference(dto);
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        Optional.ofNullable(dto.getRoles())
                .orElse(Collections.emptySet())
                .forEach(e -> entity.getRoles().add(roleRepository.getReferenceById(e.getId())));

        return entity;
    }

    @Override
    public UserEntity transformToDTOReference(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        return entity;
    }
}
