package br.com.testDesign.transform.category;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryTransformImpl implements  CategoryTransform {

        public CategoryDTO transformToDTO(CategoryEntity entity) {
            if (entity == null) {
                return null;
            }

            CategoryDTO dto = new CategoryDTO();
            dto.setId(entity.getId());
            dto.setName(entity.getName());

            return dto;
        }

        public CategoryEntity transformToEntity(CategoryDTO dto) {
            if (dto == null) {
                return null;
            }

            CategoryEntity entity = new CategoryEntity();
            entity.setName(dto.getName());
            return entity;
        }
}
