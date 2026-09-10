package com.example.booktrader.controller;


import com.example.booktrader.DTO.AtualizaStatusUsuario;
import com.example.booktrader.DTO.UsuarioRequest;
import com.example.booktrader.DTO.UsuarioResponse;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;



    @GetMapping
    public List<Usuario> consultaUsuario(){

        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> consultaUsuarioPorId(@PathVariable Long id){

        var usuario = usuarioRepository.findById(id).orElse(null);

        if(usuario == null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(usuario);
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
        usuarioBanco.setCpf(usuarioRequest.getCpf());
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
        usuarioBanco.setSenha(usuarioRequest.getSenha());

        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");


        //salvando no banco
        usuarioRepository.save(usuarioBanco);

        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastrado Com Sucesso"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest){

        //consulta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);


        if (usuarioBanco != null){
            usuarioBanco.setNome(usuarioRequest.getNome());
            usuarioBanco.setCpf(usuarioRequest.getCpf());
            usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento());
            usuarioBanco.setSenha(usuarioRequest.getSenha());

            usuarioBanco.setDataAtualizacao(LocalDateTime.now());

            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastro Com Sucesso"));
        }

        return ResponseEntity.notFound().build();


    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponse>
    AtualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusUsuario usuarioRequest){

        //consulta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);


        if (usuarioBanco != null){

            usuarioBanco.setStatus(usuarioRequest.getStatus());


            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastro Com Sucesso"));
        }

        return ResponseEntity.notFound().build();


    }


    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarStatus(@PathVariable Long id){

        //consulta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);


        if (usuarioBanco != null){

            usuarioBanco.setStatus("D");


            usuarioRepository.save(usuarioBanco);
            return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Excluído Com Sucesso"));
        }

        return ResponseEntity.notFound().build();




    }


}
