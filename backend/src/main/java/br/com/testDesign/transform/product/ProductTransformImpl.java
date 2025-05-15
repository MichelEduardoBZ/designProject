package br.com.testDesign.transform.product;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.repositories.CategoryRepository;
import br.com.testDesign.transform.TransformContractImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ProductTransformImpl extends TransformContractImpl<ProductEntity, ProductDTO> implements ProductTransform {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDTO transformToDTO(ProductEntity entity) {
        ProductDTO dto  = transformToDTOReference(entity);

        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setImgUrl(entity.getImgUrl());
        dto.setDate(entity.getDate());

        if (entity.getCategories() != null) {
            List<CategoryDTO> categoryDTOs = new ArrayList<>();
            for (CategoryEntity categoryEntity : entity.getCategories()) {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setId(categoryEntity.getId());
                categoryDTO.setName(categoryEntity.getName());
                dto.getCategories().add(categoryDTO);
            }
        }
        return dto;
    }

    @Override
    public ProductDTO transformToDTOReference(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());

        return dto;
    }

    @Override
    public ProductEntity transformToEntity(ProductDTO dto) {
        ProductEntity entity = transformToDTOReference(dto);
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        if (dto.getCategories() != null) {
            Set<CategoryEntity> categoryEntities = new HashSet<>();
            for (CategoryDTO categoryDTO : dto.getCategories()) {
                entity.getCategories().add(categoryRepository.getReferenceById(categoryDTO.getId()));
            }
        }
        return entity;
    }

    @Override
    public ProductEntity transformToDTOReference(ProductDTO dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        return entity;
    }
}
