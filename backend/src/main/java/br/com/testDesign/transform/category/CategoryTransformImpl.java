package br.com.testDesign.transform.category;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.transform.TransformContractImpl;
import org.springframework.stereotype.Component;

@Component
public class CategoryTransformImpl extends TransformContractImpl<CategoryEntity, CategoryDTO>  implements  CategoryTransform {

        public CategoryDTO transformToDTO(CategoryEntity entity) {
            CategoryDTO dto = transformToDTOReference(entity);
            dto.setName(entity.getName());
            return dto;
        }

    @Override
    public CategoryDTO transformToDTOReference(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        return dto;
    }

    public CategoryEntity transformToEntity(CategoryDTO dto) {
            CategoryEntity entity = transformToDTOReference(dto);
            entity.setName(dto.getName());
            return entity;
        }

    @Override
    public CategoryEntity transformToDTOReference(CategoryDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(dto.getId());
        return entity;
    }
}
