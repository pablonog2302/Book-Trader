package com.example.booktrader.repository;

import com.example.booktrader.entities.OfertaDeTroca;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfertaDeTrocaRepository extends JpaRepository<OfertaDeTroca,Long> {

    List<OfertaDeTroca> findByUsuarioSolicitanteIdOrderByDataCadastroDesc(Long solicitanteId);

    List<OfertaDeTroca> findByUsuarioDestinatarioIdOrderByDataCadastroDesc(Long usuarioId);

    boolean existsByLivroDesejadoId(Long livroId);

    // o bloqueio impede duas decisoes simultaneas sobre a mesma oferta
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select oferta from OfertaDeTroca oferta where oferta.id = :id")
    Optional<OfertaDeTroca> findByIdParaAtualizacao(@Param("id") Long id);

    // bloqueia as ofertas pendentes na mesma ordem antes de alterar livros compartilhados
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select oferta from OfertaDeTroca oferta where oferta.status = :status order by oferta.id")
    List<OfertaDeTroca> findByStatusParaAtualizacao(@Param("status") String status);
}
