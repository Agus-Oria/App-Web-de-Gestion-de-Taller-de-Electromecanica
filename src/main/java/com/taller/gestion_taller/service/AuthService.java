package com.taller.gestion_taller.service;

import com.taller.gestion_taller.dto.AuthResponseDto;
import com.taller.gestion_taller.dto.LoginRequestDto;
import com.taller.gestion_taller.dto.RegistroRequestDto;
import com.taller.gestion_taller.entity.Usuario;
import com.taller.gestion_taller.exception.BadRequestException;
import com.taller.gestion_taller.exception.DuplicateResourceException;
import com.taller.gestion_taller.repository.UsuarioRepository;
import com.taller.gestion_taller.security.JwtService;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String ROL_USUARIO = "ROLE_USER";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponseDto registrar(RegistroRequestDto dto) {
        String username = normalizarUsername(dto.username());
        if (usuarioRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Ya existe un usuario con nombre " + username);
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(ROL_USUARIO);
        usuario.setFechaAlta(Instant.now());
        usuarioRepository.save(usuario);
        return buildResponse(usuario);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        String username = normalizarUsername(dto.username());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, dto.password()));
        } catch (AuthenticationException exception) {
            throw new BadRequestException("Usuario o contrasena incorrectos");
        }
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario o contrasena incorrectos"));
        return buildResponse(usuario);
    }

    private AuthResponseDto buildResponse(Usuario usuario) {
        return new AuthResponseDto(
                jwtService.generarToken(usuario),
                usuario.getUsername(),
                usuario.getRol(),
                jwtService.obtenerExpiracionMs());
    }

    private String normalizarUsername(String username) {
        return username.trim().toLowerCase();
    }
}
