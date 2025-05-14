package br.com.testDesign.transform;

import br.com.testDesign.dto.BasicDTO;
import br.com.testDesign.entities.BasicEntity;

public abstract class TransformContractImpl<T extends BasicEntity, E extends BasicDTO> implements  TransformContract<T, E>, ConvertContract<T, E> {

    @Override
    public E convertToDTO(T entity) {
        if (entity == null) {
            return null;
        }

        return transformToDTO(entity);
    }

    @Override
    public E convertToDTOReference(T entity) {
        if (entity == null) {
            return null;
        }

        return transformToDTOReference(entity);
    }

    @Override
    public T convertToEntity(E dto) {
        if (dto == null) {
            return null;
        }

        return transformToEntity(dto);
    }

    @Override
    public T convertToDTOReference(E dto) {
        if (dto == null) {
            return null;
        }

        return transformToDTOReference(dto);
    }
}
