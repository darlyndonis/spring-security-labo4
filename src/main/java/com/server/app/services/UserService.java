package com.server.app.services;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.auth.*;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.exceptions.ConfictException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final JsonWebToken jwtUtil;

  @Transactional
  public User create(UserCreateDto dto) {
    uniqueUsername(dto.getUsername(), null);
    uniqueEmail(dto.getEmail(), null);
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setName(dto.getName());
    user.setSurname(dto.getSurname());
    user.setEmail(dto.getEmail());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));

    if (dto.getRole() != null) {
      Role role = roleRepository.findById(dto.getRole())
          .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
      user.setRole(role);
    }

    return userRepository.save(user);
  }

  public Page<User> findAll(int page, int size, String search) {
    return userRepository.findAll(PageRequest.of(page, size), search);
  }

  public User findById(int id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
  }

  @Transactional
  public User updateUser(int userId, UserUpdateDto dto) {
    User user = findById(userId);

    if (user.isBlocked()) {
      throw new ConfictException("The user: " + user.getUsername() + " is locked");
    }

    if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
      uniqueUsername(dto.getUsername(), userId);
      user.setUsername(dto.getUsername());
    }

    if (dto.getName() != null && !dto.getName().isBlank()) {
      user.setName(dto.getName());
    }

    if (dto.getSurname() != null && !dto.getSurname().isBlank()) {
      user.setSurname(dto.getSurname());
    }

    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
      uniqueEmail(dto.getEmail(), userId);
      user.setEmail(dto.getEmail());
    }

    if (dto.getBlocked() != null) {
      user.setBlocked(dto.getBlocked());
    }

    if (dto.getRole() != null) {
      Role role = roleRepository.findById(dto.getRole())
          .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
      user.setRole(role);
    }

    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
      user.setPassword(dto.getPassword());
    }

    return userRepository.save(user);
  }

  @Transactional
  public AuthResponse login(LoginRequest request) {
    User user = userRepository.findUserByUsername(request.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UnauthorizedException("Credenciales inválidas");
    }
    if (user.isBlocked()) {
      throw new UnauthorizedException("Tu cuenta ha sido bloqueada");
    }
    if (user.getRole() == null || !user.getRole().getActive()) {
      throw new UnauthorizedException("El rol de tu cuenta no está activo");
    }

    return new AuthResponse(jwtUtil.createToken(user), user);
  }

  @Transactional
  public AuthResponse signUp(SignUpRequest request) {
    uniqueUsername(request.getUsername(), null);
    uniqueEmail(request.getEmail(), null);

    Role role = roleRepository.findByName("ADMIN")
            .orElseThrow(() -> new NotFoundException("Rol ADMIN no encontrado"));

    if (!role.getActive()) {
      throw new UnauthorizedException("El rol ADMIN no está activo");
    }

    User user = User.builder()
            .username(request.getUsername())
            .name(request.getName())
            .surname(request.getSurname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(role)
            .build();

    userRepository.save(user);
    return new AuthResponse(jwtUtil.createToken(user), user);
  }

  @Transactional
  public AuthResponse updateProfile(int userId, UpdateProfileRequest request) {
    User user = findById(userId);

    uniqueUsername(request.getUsername(), userId);
    uniqueEmail(request.getEmail(), userId);

    user.setUsername(request.getUsername());
    user.setName(request.getName());
    user.setSurname(request.getSurname());
    user.setEmail(request.getEmail());

    userRepository.save(user);
    return new AuthResponse(jwtUtil.createToken(user), user);
  }

  @Transactional
  public User updatePassword(int userId, UpdatePasswordRequest request) {
    User user = findById(userId);

    if (!passwordEncoder.matches(request.getOldpassword(), user.getPassword())) {
      throw new BadRequestException("La contraseña actual es incorrecta");
    }
    if (!request.getNewpassword().equals(request.getConfirmpassword())) {
      throw new BadRequestException("Las contraseñas nuevas no coinciden");
    }

    user.setPassword(passwordEncoder.encode(request.getNewpassword()));
    return userRepository.save(user);
  }

  private void uniqueUsername(String username, Integer id) {
    userRepository.findUserByUsername(username).ifPresent(existing -> {
      if (id == null || existing.getId() != id) {
        throw new ConfictException("El nombre de usuario ya está en uso");
      }
    });
  }

  private void uniqueEmail(String email, Integer id) {
    userRepository.findUserByEmail(email).ifPresent(existing -> {
      if (id == null || existing.getId() != id) {
        throw new ConfictException("El correo electrónico ya está en uso");
      }
    });
  }
}
