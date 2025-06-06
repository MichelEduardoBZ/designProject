package br.com.testDesign.resources;

import br.com.testDesign.dto.user.UserDTO;
import br.com.testDesign.dto.user.UserInsertDTO;
import br.com.testDesign.dto.user.UserUpdateDTO;
import br.com.testDesign.services.UserService;
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
@RequestMapping(value = "/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDTO> findById(@PathVariable  Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    @GetMapping(value = "/profile")
    public ResponseEntity<UserDTO> findUserAuthenticated() {
        return ResponseEntity.ok(userService.findUserAuthenticated());
    }

    @PostMapping
    public ResponseEntity<UserDTO> insertUser(@Valid @RequestBody UserInsertDTO  userInsertDTO) {
        UserDTO userDTO = userService.insertUser(userInsertDTO);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(userDTO.getId())
                            .toUri();

        return ResponseEntity.created(uri).body(userDTO);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @Valid  @RequestBody UserUpdateDTO  userDTO) {
        return ResponseEntity.ok(userService.updateUser(userId, userDTO));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
