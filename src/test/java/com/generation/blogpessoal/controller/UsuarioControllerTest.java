package com.generation.blogpessoal.controller;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start() {
        usuarioRepository.deleteAll();
        usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", " "));
    }

    @Test
    @DisplayName("Cadastrar Um Usuário")
    public void deveCriarUmUsuario() {
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo Antunes", "paulo_antunes@email.com.br",
                "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
    }

    @Test
    @DisplayName("Não deve permitir duplicação do Usuário")
    public void naoDeveDuplicarUsuario() {
        usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
    }

    @Test
    @DisplayName("Atualiza um Usuario")
    public void deveAtualizarUmUsuario() {
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Paes", "juliana_paes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
                "Juliana Paes Morais", "juliana_paes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg");
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
        assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
        assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());

    }

    @Test
    @DisplayName("Listar todos os Usuários")
    public void deveMostrarTodosUsuarios() {
        usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sato", "sabrina_sato@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));
        usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Paes", "ricardo_paes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }


    @Test
    @DisplayName("Listar Usuário por Id")
    public void deveMostrarUsuarioPorId(){
        Optional<Usuario> cadastro = usuarioService.cadastrarUsuario(new Usuario(1L, "Juliana Amora", "juliana_amora@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        ResponseEntity<Usuario> resposta = testRestTemplate
                .withBasicAuth("root@root.com", "rootroot")
                .exchange("/usuarios/{id}", HttpMethod.GET, null, Usuario.class, cadastro.get().getId());

        assertEquals(cadastro.get().getId(), resposta.getBody().getId());
    }

    @Test
    @DisplayName("Logar")
    public void deveLogar(){
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Paes", "juliana_paes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario("juliana_paes@email.com.br", "13465278" ));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/logar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.UNAUTHORIZED, corpoResposta.getStatusCode());
    }
}