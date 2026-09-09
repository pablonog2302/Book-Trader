package com.example.booktrader.controller;


import com.example.booktrader.DTO.AtualizaStatusUsuario;
import com.example.booktrader.DTO.UsuarioRequest;
import com.example.booktrader.DTO.UsuarioResponse;
import com.example.booktrader.entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    public String consultaUsuario(){
        return "Hello World";
    }

    @GetMapping("/{id}")
    public Usuario consultaUsuarioPorId(@PathVariable Long id){
        Usuario usuario = new Usuario();
        usuario.setNome("Pablo");
        usuario.setCpf("12345678910");
        usuario.setDataNascimento("23022006");

        return usuario;
    }

    @GetMapping("/empresa/{empresaId}")
    public Usuario consultaUsuarioPorEmpresas(@PathVariable Long empresaId){
        Usuario usuarioContrutorCompleto =
                new Usuario("Pablo", "12345678910", "23022006");

        return usuarioContrutorCompleto;
    }

    @PostMapping("")
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@RequestBody UsuarioRequest usuarioRequest){

        if(usuarioRequest.getCpf().isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Usuario usuarioBanco = new Usuario();

        usuarioBanco.setNome(usuarioRequest.getNome());
        usuarioBanco.setCpf(usuarioRequest.getNome());
        usuarioBanco.setDataNascimento(usuarioRequest.getNome());

        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");



        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastrado Com Sucesso"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest){

        //consulta no banco
        Usuario usuarioBanco = new Usuario();


        if (usuarioBanco != null){
            usuarioBanco.setNome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getNome());
            usuarioBanco.setDataNascimento(usuarioRequest.getNome());

            usuarioBanco.setDataAtualizacao(LocalDateTime.now());
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastro Com Sucesso"));
        }

        return ResponseEntity.notFound().build();


    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponse>
    AtualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusUsuario usuarioRequest){

        //consulta no banco
        Usuario usuarioBanco = new Usuario();


        if (usuarioBanco != null){

            usuarioBanco.setStatus(usuarioRequest.getStatus());


            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastro Com Sucesso"));
        }

        return ResponseEntity.notFound().build();


    }


    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarStatus(@PathVariable Long id){

        //consulta no banco
        Usuario usuarioBanco = new Usuario();


        if (usuarioBanco != null){

            usuarioBanco.setStatus("D");


            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();


    }


}
