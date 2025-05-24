package br.com.testDesign.transform.security;

import br.com.testDesign.dto.user.RoleDTO;
import br.com.testDesign.entities.RoleEntity;
import br.com.testDesign.transform.TransformContractImpl;
import org.springframework.stereotype.Component;

@Component
public class RoleTransformImpl extends TransformContractImpl<RoleEntity, RoleDTO>  implements RoleTransform {

        public RoleDTO transformToDTO(RoleEntity entity) {
            RoleDTO dto = transformToDTOReference(entity);
            dto.setAuthority(entity.getAuthority());
            return dto;
        }

    @Override
    public RoleDTO transformToDTOReference(RoleEntity entity) {
        RoleDTO dto = new RoleDTO();
        dto.setId(entity.getId());
        return dto;
    }

    public RoleEntity transformToEntity(RoleDTO dto) {
            RoleEntity entity = transformToDTOReference(dto);
            entity.setAuthority(dto.getAuthority());
            return entity;
        }

    @Override
    public RoleEntity transformToDTOReference(RoleDTO dto) {
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        return entity;
    }
}
