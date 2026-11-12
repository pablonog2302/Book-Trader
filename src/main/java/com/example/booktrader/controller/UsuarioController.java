package com.example.booktrader.controller;


import com.example.booktrader.DTO.AtualizaStatusUsuario;
import com.example.booktrader.DTO.UsuarioConsultaResponse;
import com.example.booktrader.DTO.UsuarioRequest;
import com.example.booktrader.DTO.UsuarioResponse;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.EmpresaRepository;
import com.example.booktrader.repository.UsuarioRepository;
import com.example.booktrader.utils.CpfUtils;
import com.example.booktrader.utils.OnlyNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    //
    @Autowired
    private EmpresaRepository empresaRepository;


    @GetMapping
    public List<UsuarioConsultaResponse> consultaUsuario(){

        return usuarioRepository.findAll().stream().map(UsuarioConsultaResponse::new).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioConsultaResponse> consultaUsuarioPorId(@PathVariable Long id){

        var usuario = usuarioRepository.findById(id).orElse(null);

        if(usuario == null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(new UsuarioConsultaResponse(usuario));
    }

    @GetMapping("/empresa/{empresaId}")
    public Usuario consultaUsuarioPorEmpresas(@PathVariable Long empresaId){
        Usuario usuarioContrutorCompleto =
                new Usuario("Pablo", "12345678910", "23022006");

        return usuarioContrutorCompleto;
    }

    @PostMapping("")
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@RequestBody UsuarioRequest usuarioRequest){

        if (usuarioRequest == null
                || usuarioRequest.getNome() == null || usuarioRequest.getNome().isBlank()
                || usuarioRequest.getDataNascimento() == null || usuarioRequest.getDataNascimento().isBlank()
                || usuarioRequest.getSenha() == null || usuarioRequest.getSenha().isBlank()
                || !CpfUtils.isValidCpf(usuarioRequest.getCpf())) {
            return ResponseEntity.badRequest().body(
                    new UsuarioResponse(null, "Informe nome, CPF valido, data de nascimento e senha."));
        }

        String cpf = OnlyNumber.apenasNumeros(usuarioRequest.getCpf());
        if (usuarioRepository.existsUsuarioByCpf(cpf)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new UsuarioResponse(null, "Ja existe um usuario cadastrado com este CPF."));
        }

        var empresaBanco = usuarioRequest.getEmpresa_id() == null
                ? null
                : empresaRepository.findById(usuarioRequest.getEmpresa_id()).orElse(null);
        if (usuarioRequest.getEmpresa_id() != null && empresaBanco == null) {
            return ResponseEntity.badRequest().body(
                    new UsuarioResponse(null, "Empresa nao encontrada."));
        }

        Usuario usuarioBanco = new Usuario();

        usuarioBanco.setNome(usuarioRequest.getNome().trim());
        usuarioBanco.setCpf(cpf);
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento().trim());
        usuarioBanco.setSenha(usuarioRequest.getSenha());
        usuarioBanco.setEmpresa(empresaBanco);

        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");


        //salvando no banco
        try {
            usuarioRepository.save(usuarioBanco);
        } catch (DataIntegrityViolationException erro) {
            if (usuarioRepository.existsUsuarioByCpf(cpf)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new UsuarioResponse(null, "Ja existe um usuario cadastrado com este CPF."));
            }
            throw erro;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new UsuarioResponse(usuarioBanco.getId(), "Usuario cadastrado com sucesso."));

    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest){

        //consulta no banco
        Usuario usuarioBanco = usuarioRepository.findById(id).orElse(null);


        if (usuarioBanco != null){

            //chamamos
            var empresaBanco = empresaRepository.findById(usuarioRequest.getEmpresa_id()).orElse(null);
            if (empresaBanco == null){
            }

            usuarioBanco.setEmpresa(empresaBanco);
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
