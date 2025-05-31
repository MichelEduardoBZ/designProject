package br.com.testDesign.services;

import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.projection.ProductProjection;
import br.com.testDesign.repositories.ProductRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.tests.ProductFactory;
import br.com.testDesign.transform.product.ProductTransform;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    private ProductEntity product;
    private ProductDTO productDTO;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<ProductEntity> page;

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductTransform productTransform;

    @BeforeEach
    public void setUp() {
        initializeIds();
        initializeEntities();
        mockRepositoryBehavior();
        mockTransformBehavior();
    }

    private void initializeIds() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
    }

    private void initializeEntities() {
        product = ProductFactory.createProductEntityWithData();
        productDTO = ProductFactory.createProductDTO();
        page = new PageImpl<>(List.of(product));
    }

    private void mockRepositoryBehavior() {
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.doThrow(EntityNotFoundException.class).when(repository).getReferenceById(nonExistingId);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    private void mockTransformBehavior() {
        Mockito.when(productTransform.convertToDTO(ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(productTransform.convertToEntity(ArgumentMatchers.any())).thenReturn(product);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO newProductDTO = service.findById(existingId);

        Assertions.assertNotNull(newProductDTO);
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void findAllShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "pc";
        String categories = "1,2";

        List<Long> listCategoriesId = List.of(1L, 2L);

        // Criar um ProductProjection mock (interface)
        ProductProjection productProjection = Mockito.mock(ProductProjection.class);
        Mockito.when(productProjection.getId()).thenReturn(product.getId());

        // Criar a Page de ProductProjection
        Page<ProductProjection> productProjectionPage = new PageImpl<>(List.of(productProjection), pageable, 1);

        // Mock do repository para searchProductsByCategoriesAndName
        Mockito.when(repository.searchProductsByCategoriesAndName(
                ArgumentMatchers.eq(listCategoriesId),
                ArgumentMatchers.eq(name),
                ArgumentMatchers.eq(pageable)
        )).thenReturn(productProjectionPage);

        // Mock do repository para searchProductsWithCategories (retorna lista ProductEntity)
        Mockito.when(repository.searchProductsWithCategories(ArgumentMatchers.anyList())).thenReturn(List.of(product));

        // Mock do transform para converter ProductEntity para ProductDTO
        Mockito.when(productTransform.convertToDTO(ArgumentMatchers.any(ProductEntity.class))).thenReturn(productDTO);

        Page<ProductDTO> result = service.findAllPaged(name, categories, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(productDTO.getId(), result.getContent().get(0).getId());

        Mockito.verify(repository, Mockito.times(1)).searchProductsByCategoriesAndName(listCategoriesId, name, pageable);
        Mockito.verify(repository, Mockito.times(1)).searchProductsWithCategories(ArgumentMatchers.anyList());
        Mockito.verify(productTransform, Mockito.times(1)).convertToDTO(product);
    }

    @Test
    public void insertProductShouldReturnProductDTO() {
        ProductDTO newProductDTO = service.insertProduct(productDTO);

        Assertions.assertNotNull(newProductDTO);
        Assertions.assertEquals(newProductDTO.getId(), product.getId());
        Assertions.assertEquals(newProductDTO.getName(), product.getName());

        Mockito.verify(repository, Mockito.times(1)).save(product);
    }

    @Test
    public void updateProductShouldReturnProductDTOWhenIdExists() {
        ProductDTO newProductDTO = service.updateProduct(existingId, productDTO);

        Assertions.assertNotNull(newProductDTO);
        Mockito.verify(repository, Mockito.times(1)).getReferenceById(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(product);
    }

    @Test
    public void updateProductShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updateProduct(nonExistingId, productDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.deleteProduct(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteProduct(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowsDataIntegrityViolationExceptionWhenDependentIdExists() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.deleteProduct(dependentId);
        });
    }
}
