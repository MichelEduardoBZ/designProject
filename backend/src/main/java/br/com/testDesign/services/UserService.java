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
import br.com.testDesign.transform.security.UserTransform;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTransform userTransform;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(e -> userTransform.convertToDTO(e));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        return userTransform.convertToDTO(userRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public UserDTO findUserAuthenticated() {
        return userTransform.convertToDTO(authService.authenticated());
    }

    @Transactional
    public UserDTO insertUser(UserInsertDTO userInsertDTO) {
        UserEntity userEntity = userRepository.save(userTransform.convertToEntity(userInsertDTO));

        userEntity.getRoles().clear();
        userEntity.getRoles().add(roleRepository.findByAuthority("ROLE_OPERATOR"));

        userEntity.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));
        return userTransform.convertToDTO(userEntity);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserUpdateDTO userDTO) {
        try {
            UserEntity userEntity = userRepository.getReferenceById(userId);
            userEntity.setFirstName(userDTO.getFirstName());
            userEntity.setLastName(userDTO.getLastName());
            userEntity.setEmail(userDTO.getEmail());

            Optional.ofNullable(userDTO.getRoles())
                    .orElse(Collections.emptySet())
                    .forEach(e -> userEntity.getRoles().add(roleRepository.getReferenceById(e.getId())));
            return userTransform.convertToDTO(userRepository.save(userEntity));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException();
        }
        try {
            userRepository.deleteById(userId);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserEntity user = new UserEntity();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        result.forEach(e -> user.addRole(new RoleEntity(e.getRoleId(), e.getAuthority())));

        return user;
    }
}
