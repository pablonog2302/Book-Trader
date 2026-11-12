package com.example.booktrader.repository;

import com.example.booktrader.entities.Livro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro,Long> {

    Optional<List<Livro>> getLivrosByTituloContainingIgnoreCase(String titulo);

    // bloqueia os livros durante a troca para evitar transferencias simultaneas
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select livro from Livro livro where livro.id in :ids order by livro.id")
    List<Livro> findAllByIdsParaAtualizacao(@Param("ids") List<Long> ids);

}
