package com.example.booktrader.repository;

import com.example.booktrader.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro,Long> {

    Optional<List<Livro>> getLivrosByTituloContainingIgnoreCase(String titulo);

}
