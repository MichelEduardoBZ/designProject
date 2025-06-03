package br.com.testDesign.resources;

import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.projection.ProductProjection;
import br.com.testDesign.services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping(value = "/filter")
    public ResponseEntity<Page<ProductDTO>> findAllPaged(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "categoryId", defaultValue = "0")  String categoryId,
            Pageable pageable)
    {
        return ResponseEntity.ok(productService.findAllPaged(name, categoryId, pageable));
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<ProductDTO> findById(@PathVariable  Long productId) {
        return ResponseEntity.ok(productService.findById(productId));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDTO> insertProduct(@Valid @RequestBody  ProductDTO  productDTO) {
        productDTO = productService.insertProduct(productDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(productDTO.getId())
                            .toUri();

        return ResponseEntity.created(uri).body(productDTO);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId,  @Valid @RequestBody  ProductDTO  productDTO) {
        return ResponseEntity.ok(productService.updateProduct(productId, productDTO));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
