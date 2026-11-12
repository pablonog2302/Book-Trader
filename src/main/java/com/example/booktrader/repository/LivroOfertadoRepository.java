package com.example.booktrader.repository;

import com.example.booktrader.entities.LivroOfertado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroOfertadoRepository extends JpaRepository<LivroOfertado, Long> {

    List<LivroOfertado> findByOfertaDeTrocaIdOrderById(Long ofertaId);

    List<LivroOfertado> findByLivroIdIn(List<Long> livroIds);

    boolean existsByLivroId(Long livroId);
}
