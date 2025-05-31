package br.com.testDesign.services;

import br.com.testDesign.dto.user.UserDTO;
import br.com.testDesign.dto.user.UserInsertDTO;
import br.com.testDesign.dto.user.UserUpdateDTO;
import br.com.testDesign.entities.RoleEntity;
import br.com.testDesign.entities.UserEntity;
import br.com.testDesign.projection.UserDetailsProjection;
import br.com.testDesign.repositories.RoleRepository;
import br.com.testDesign.repositories.UserRepository;
import br.com.testDesign.services.exceptions.DatabaseException;
import br.com.testDesign.services.exceptions.ResourceNotFoundException;
import br.com.testDesign.tests.UserDetailsFactory;
import br.com.testDesign.tests.UserFactory;
import br.com.testDesign.transform.security.UserTransform;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserTransform userTransform;

    @Mock
    private AuthService authService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UserEntity user;
    private UserDTO userDTO;
    private UserInsertDTO userInsertDTO;
    private UserUpdateDTO userUpdateDTO;
    private List<UserDetailsProjection> userDetails;
    private PageImpl<UserEntity> page;

    private Long existingId;
    private Long nonExistingId;

    private String existingUsername;
    private String nonExistingUsername;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 999L;
        existingUsername = "michel@gmail.com";
        nonExistingUsername = "notfound@gmail.com";

        user = UserFactory.createCustomClientUser(existingId, existingUsername);
        userDTO = UserFactory.createUserDTO();
        userInsertDTO = new UserInsertDTO();
        userInsertDTO.setFirstName("Michel");
        userInsertDTO.setLastName("Eduardo");
        userInsertDTO.setEmail(existingUsername);
        userInsertDTO.setPassword("12345678");

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("NovoNome");
        userUpdateDTO.setLastName("NovoSobrenome");
        userUpdateDTO.setEmail("novoemail@gmail.com");

        userDetails = UserDetailsFactory.createCustomAdminUser(existingUsername);
        page = new PageImpl<>(List.of(user));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(repository.findById(existingId)).thenReturn(Optional.of(user));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getReferenceById(existingId)).thenReturn(user);
        doThrow(new EntityNotFoundException()).when(repository).getReferenceById(nonExistingId);

        when(userTransform.convertToDTO(any())).thenReturn(userDTO);
        when(userTransform.convertToEntity(any(UserInsertDTO.class))).thenReturn(user);
        when(repository.save(any())).thenReturn(user);
        when(repository.existsById(existingId)).thenReturn(true);
        when(repository.existsById(nonExistingId)).thenReturn(false);

        when(roleRepository.findByAuthority(any())).thenReturn(new RoleEntity(1L, "ROLE_OPERATOR"));
        when(roleRepository.getReferenceById(any())).thenReturn(new RoleEntity(1L, "ROLE_OPERATOR"));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        when(repository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetails);
        when(repository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());
    }

    @Test
    public void findAllShouldReturnPageOfUserDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDTO> result = service.findAll(pageable);

        assertNotNull(result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() {
        UserDTO result = service.findById(existingId);

        assertNotNull(result);
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    public void findUserAuthenticatedShouldReturnUserDTO() {
        when(authService.authenticated()).thenReturn(user);
        UserDTO result = service.findUserAuthenticated();

        assertNotNull(result);
        assertEquals(userDTO.getEmail(), result.getEmail());
    }

    @Test
    public void insertUserShouldReturnUserDTO() {
        UserDTO result = service.insertUser(userInsertDTO);

        assertNotNull(result);
        verify(repository, times(1)).save(any(UserEntity.class));
        verify(roleRepository, times(1)).findByAuthority("ROLE_OPERATOR");
    }

    @Test
    public void updateUserShouldReturnUserDTOWhenIdExists() {
        UserDTO result = service.updateUser(existingId, userUpdateDTO);

        assertNotNull(result);
        verify(repository, times(1)).getReferenceById(existingId);
        verify(repository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void updateUserShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.updateUser(nonExistingId, userUpdateDTO));
    }

    @Test
    public void deleteUserShouldDoNothingWhenIdExists() {
        assertDoesNotThrow(() -> service.deleteUser(existingId));
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    public void deleteUserShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> service.deleteUser(nonExistingId));
    }

    @Test
    public void deleteUserShouldThrowDatabaseExceptionWhenIntegrityViolationOccurs() {
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(existingId);

        assertThrows(DatabaseException.class, () -> service.deleteUser(existingId));
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        UserDetails result = service.loadUserByUsername(existingUsername);

        assertNotNull(result);
        assertEquals(existingUsername, result.getUsername());
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(nonExistingUsername));
    }
}
