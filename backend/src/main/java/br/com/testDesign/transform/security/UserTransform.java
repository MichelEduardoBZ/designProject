package br.com.testDesign.transform.security;

import br.com.testDesign.dto.user.UserDTO;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.transform.ConvertContract;

public interface UserTransform extends ConvertContract<UserEntity, UserDTO> {
}
