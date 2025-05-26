package br.com.testDesign.tests;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.entities.ProductEntity;

import java.time.Instant;

public class ProductFactory {

    public  static ProductEntity createProductEntityWithData() {
        ProductEntity product = new ProductEntity(1L, "Livros", "Harry Poter V7", 129.99, "https://img.com/img.png", Instant.parse("2004-12-12T03:00:00Z"));
        product.getCategories().add(new CategoryEntity(1L, "Livros"));
        return product;
    }

    public  static ProductDTO createProductDTO() {
        ProductDTO productDTO = new ProductDTO(1L, "Livros", "Harry Poter V7", 129.99, "https://img.com/img.png", Instant.parse("2004-12-12T03:00:00Z"));
        productDTO.getCategories().add(new CategoryDTO(1L, "Livros"));
        return productDTO;
    }
}
