package br.com.testDesign.transform.category;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.transform.ConvertContract;

public interface CategoryTransform extends ConvertContract<CategoryEntity, CategoryDTO> {
}
