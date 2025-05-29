package br.com.testDesign.tests;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;

public class CategoryFactory {

    public  static CategoryEntity createCategoryEntityWithData() {
        return new CategoryEntity(1L, "Livros");
    }

    public  static CategoryDTO createCategoryDTO() {
        return new CategoryDTO(1L, "Livros");
    }
}
