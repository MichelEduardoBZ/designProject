package br.com.testDesign.transform;

import br.com.testDesign.dto.BasicDTO;
import br.com.testDesign.entities.BasicEntity;

public interface ConvertContract <T extends BasicEntity, E extends BasicDTO>{

    E convertToDTO(T entity);

    E convertToDTOReference(T entity);

    T convertToEntity(E dto);

    T convertToDTOReference(E dto);

}
