package br.com.testDesign.repositories;

import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.services.exceptions.EntityNotFoundException;
import br.com.testDesign.tests.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
public class ProductRepositoryTests {

    private ProductEntity product;
    private long existingId;
    private long countTotalProducts;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    public void setUp() throws Exception {
        product = ProductFactory.createProductEntityWithData();
        existingId = 1L;
        countTotalProducts = 25L;
    }

    @Test
    public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        product.setId(null);
        repository.save(product);
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Assertions.assertFalse(repository.findById(existingId).isPresent());
    }

    @Test
    public void findByIdShouldThrowIllegalArgumentExceptionWhenIdIsNull() {
        product.setId(null);

        Assertions.assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            repository.findById(product.getId());
        }) ;
    }

    @Test
    public void findByIdShouldReturnProductEntityWhenIdIsNotNull() {
        ProductEntity productEntity = repository.getReferenceById(existingId);
       Assertions.assertEquals(existingId, productEntity.getId());
    }
}
