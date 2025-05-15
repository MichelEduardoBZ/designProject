package br.com.testDesign.resources;

import br.com.testDesign.dto.CategoryDTO;
import br.com.testDesign.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @GetMapping(value = "/{categoryId}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable  Long categoryId) {
        return ResponseEntity.ok(categoryService.findByIdl(categoryId));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> insertCategory(@RequestBody  CategoryDTO  categoryDTO) {
        categoryDTO = categoryService.insertCategory(categoryDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(categoryDTO.getId())
                            .toUri();

        return ResponseEntity.created(uri).body(categoryDTO);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,  @RequestBody  CategoryDTO  categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryDTO));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
