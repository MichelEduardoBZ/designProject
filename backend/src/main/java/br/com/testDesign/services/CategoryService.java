package br.com.testDesign.services;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.entities.CategoryEntity;
import br.com.testDesign.repositories.CategoryRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.EntityNotFoundException;
import br.com.testDesign.transform.category.CategoryTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTransform categoryTransform;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(PageRequest pageRequest) {
        return categoryRepository.findAll(pageRequest).map(e -> categoryTransform.transformToDTO(e));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findByIdl(Long id) {
        return categoryTransform.transformToDTO(categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Transactional
    public CategoryDTO insertCategory(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity = categoryRepository.save(categoryTransform.transformToEntity(categoryDTO));
        return categoryTransform.transformToDTO(categoryEntity);
    }

    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        try {
            CategoryEntity categoryEntity = categoryRepository.getReferenceById(categoryId);
            categoryEntity.setName(categoryDTO.getName());
            return categoryTransform.transformToDTO(categoryRepository.save(categoryEntity));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id not found " + categoryId);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteCategory(Long categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException();
        }
        try {
            categoryRepository.deleteById(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }
}
