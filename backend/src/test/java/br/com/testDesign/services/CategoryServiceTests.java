package br.com.testDesign.services;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.repositories.CategoryRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.tests.CategoryFactory;
import br.com.testDesign.transform.category.CategoryTransform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    private CategoryEntity category;
    private CategoryDTO categoryDTO;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryTransform categoryTransform;

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
        category = CategoryFactory.createCategoryEntityWithData();
        categoryDTO = CategoryFactory.createCategoryDTO();
    }

    private void mockRepositoryBehavior() {
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);

        Mockito.when(repository.findAll()).thenReturn(Collections.singletonList(category));
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(category);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getReferenceById(nonExistingId);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    private void mockTransformBehavior() {
        Mockito.when(categoryTransform.convertToDTO(ArgumentMatchers.any())).thenReturn(categoryDTO);
        Mockito.when(categoryTransform.convertToEntity(ArgumentMatchers.any())).thenReturn(category);
    }

    @Test
    public void findAllShouldReturnListCategoryDTO() {
        List<CategoryDTO> result = service.findAll();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(result.get(0).getId(), category.getId());
        Assertions.assertEquals(result.get(0).getName(), category.getName());

        Mockito.verify(repository, Mockito.times(1)).findAll();
    }

    @Test
    public void findByIdShouldReturnCategoryDTOWhenIdExists() {
        CategoryDTO newCategoryDTO = service.findById(existingId);

        Assertions.assertNotNull(newCategoryDTO);
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void insertCategoryShouldReturnCategoryDTO() {
        CategoryDTO newCategoryDTO = service.insertCategory(categoryDTO);

        Assertions.assertNotNull(newCategoryDTO);
        Assertions.assertEquals(newCategoryDTO.getId(), category.getId());
        Assertions.assertEquals(newCategoryDTO.getName(), category.getName());

        Mockito.verify(repository, Mockito.times(1)).save(category);
    }

    @Test
    public void updateCategoryShouldReturnCategoryDTOWhenIdExists() {
        CategoryDTO newCategoryDTO = service.updateCategory(existingId, categoryDTO);

        Assertions.assertNotNull(newCategoryDTO);
        Mockito.verify(repository, Mockito.times(1)).getReferenceById(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(category);
    }

    @Test
    public void updateCategoryShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updateCategory(nonExistingId, categoryDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getReferenceById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.deleteCategory(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteCategory(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowsDataIntegrityViolationExceptionWhenDependentIdExists() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.deleteCategory(dependentId);
        });
    }

}
