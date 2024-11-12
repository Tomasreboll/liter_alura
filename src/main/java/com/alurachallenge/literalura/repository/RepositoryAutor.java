package com.alurachallenge.literalura.repository;

import com.alurachallenge.literalura.model.DatosAutores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RepositoryAutor extends JpaRepository<DatosAutores, Long> {

    // Para encontrar por el nombre del autor
    List<DatosAutores> findByName(String name);

    // Para buscar si durante x año se econtraba vivo
    @Query("SELECT a FROM DatosAutores a WHERE a.death_year >= :year")
    List<DatosAutores> findAuthorsByDeathYearLessThanEqual(@Param("year") Integer year);

}
