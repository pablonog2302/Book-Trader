package com.example.booktrader.controller;


import entities.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    public String ConsultaUsuario(){
        return "Hello World";
    }

    @GetMapping("/{id}")
    public Usuario ConsultaUsuarioPorId(@PathVariable Long id){
        Usuario usuario = new Usuario();
        usuario.setNome("Pablo");
        usuario.setCpf("12345678910");
        usuario.setDataNascimento("23022006");

        return usuario;
    }

    @GetMapping("/empresa/{empresaId}")
    public Usuario ConsultaUsuarioPorEmpresas(@PathVariable Long empresaId){
        Usuario usuarioContrutorCompleto =
                new Usuario("Pablo", "12345678910", "23022006");

        return usuarioContrutorCompleto;
    }

    @PostMapping("")
    public ResponseEntity<Usuario> CadastrarUsuario(@RequestBody Usuario usuarioRequest){

        if(usuarioRequest.getCpf().isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(usuarioRequest);

    }

}
