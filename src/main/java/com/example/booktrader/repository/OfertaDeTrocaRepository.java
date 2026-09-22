package com.example.booktrader.repository;

import com.example.booktrader.entities.OfertaDeTroca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfertaDeTrocaRepository extends JpaRepository<OfertaDeTroca,Long> {

    Optional<List<OfertaDeTroca>> getOfertaDeTrocaByLivroOfertadoContainingIgnoreCase(String livroOfertado);

    Optional<List<OfertaDeTroca>> getOfertaDeTrocaByStatusContainingIgnoreCase(String status);
}
