package br.com.testDesign.services;

import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.projection.ProductProjection;
import br.com.testDesign.repositories.ProductRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.transform.product.ProductTransform;
import br.com.testDesign.util.PageUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

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
    public Page<ProductDTO> findAllPaged(String name, String categoriesId, Pageable pageable) {
        List<Long> listCategoriesId = !"0".equals(categoriesId) ? Arrays.stream(categoriesId.split(",")).map(Long::parseLong).toList() : List.of();

        Page<ProductProjection> productsReference = productRepository.searchProductsByCategoriesAndName(listCategoriesId, name, pageable);

        List<Long> productsId = productsReference.map(ProductProjection::getId).toList();
        List<ProductEntity> productEntities = PageUtils.replaceSortByContent(productsReference.getContent(), productRepository.searchProductsWithCategories(productsId));
        List<ProductDTO> productsDTO = productEntities.stream().map(p -> productTransform.convertToDTO(p)).toList();

        return new PageImpl<>(productsDTO, productsReference.getPageable(), productsReference.getTotalElements());
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
