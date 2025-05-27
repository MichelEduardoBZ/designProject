package br.com.testDesign.services;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.repositories.CategoryRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.transform.category.CategoryTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTransform categoryTransform;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream().map(e -> categoryTransform.convertToDTO(e)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        return categoryTransform.convertToDTO(categoryRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Transactional
    public CategoryDTO insertCategory(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = categoryRepository.save(categoryTransform.convertToEntity(categoryDTO));
        return categoryTransform.convertToDTO(categoryEntity);
    }

    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        try {
            CategoryEntity categoryEntity = categoryRepository.getReferenceById(categoryId);
            categoryEntity.setName(categoryDTO.getName());
            return categoryTransform.convertToDTO(categoryRepository.save(categoryEntity));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + categoryId);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException();
        }
        try {
            categoryRepository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }
}
