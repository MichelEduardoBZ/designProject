package br.com.testDesign.services;

import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.repositories.ProductRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.transform.product.ProductTransform;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTransform productTransform;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(e -> productTransform.convertToDTO(e));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productTransform.convertToDTO(productRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Transactional
    public ProductDTO insertProduct(ProductDTO productDTO) {
        ProductEntity productEntity = productRepository.save(productTransform.convertToEntity(productDTO));
        return productTransform.convertToDTO(productEntity);
    }

    @Transactional
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        try {
            ProductEntity productEntity = productRepository.getReferenceById(productId);
            productEntity.setName(productDTO.getName());
            productEntity.setDescription(productDTO.getDescription());
            return productTransform.convertToDTO(productRepository.save(productEntity));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found " + productId);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteProduct(Long productId) {
        if(!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException();
        }
        try {
            productRepository.deleteById(productId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }
}
