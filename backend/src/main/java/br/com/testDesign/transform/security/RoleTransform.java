package br.com.testDesign.transform.security;

import br.com.testDesign.dto.user.RoleDTO;
import br.com.testDesign.entities.RoleEntity;
import br.com.testDesign.transform.ConvertContract;

public interface RoleTransform extends ConvertContract<RoleEntity, RoleDTO> {
}
