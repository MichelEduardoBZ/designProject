package br.com.testDesign.transform;

import br.com.testDesign.dto.BasicDTO;
import br.com.testDesign.entities.BasicEntity;

public interface TransformContract<T extends BasicEntity, E extends BasicDTO> {

    E transformToDTO(T entity);

    T transformToEntity(E dto);
}

