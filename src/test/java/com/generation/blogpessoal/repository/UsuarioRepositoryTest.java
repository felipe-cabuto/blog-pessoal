package com.generation.blogpessoal.repository;

import com.generation.blogpessoal.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start() {
        usuarioRepository.save(new Usuario(0L, "João Silva", "joao@gmail.com", "12345678", "www.foto.com.br"));
        usuarioRepository.save(new Usuario(0L, "Maria da Silva", "maria@gmail.com", "12345678", "www.foto.com.br"));
        usuarioRepository.save(new Usuario(0L, "Renata Souza Silva", "renata@gmail.com", "12345678", "www.foto.com.br"));
        usuarioRepository.save(new Usuario(0L, "Paula Lima", "paula@gmail.com", "12345678", "www.foto.com.br"));
    }

    @Test
    @DisplayName("Retorna 1 usuario") // nome do método
    public void deveRetornarUmUsuario() {

        Optional<Usuario> usuario = usuarioRepository.findByUsuario("joao@gmail.com");

        assertTrue(usuario.get().getUsuario().equals("joao@gmail.com"));
    }


    @Test
    @DisplayName("Retorna 3 usuarios")
    public void deveRetornarTresUsuarios() {
        List<Usuario> listaDeUsuarios = usuarioRepository.findAllByNomeContainingIgnoreCase("Silva");

        assertEquals(3, listaDeUsuarios.size());

        assertTrue(listaDeUsuarios.get(0).getNome().equals("João Silva"));
        assertTrue(listaDeUsuarios.get(1).getNome().equals("Maria da Silva"));
        assertTrue(listaDeUsuarios.get(2).getNome().equals("Renata Souza Silva"));

    }

    @AfterAll
    public void end() {
        usuarioRepository.deleteAll();
    }

}