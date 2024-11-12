package com.alurachallenge.literalura.repository;

import com.alurachallenge.literalura.model.DatosAutores;
import com.alurachallenge.literalura.model.DatosLibros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Repository extends JpaRepository<DatosLibros, Long> {

    // Para buscar por titulo
    List<DatosLibros> findByTitle(String tituloBuscado);

    // Para Buscar por libro por autor
    @Query("SELECT l FROM DatosLibros l LEFT JOIN FETCH l.authors")
    List<DatosLibros> findAllWithAuthors();

    // Para buscar por idioma los libros:
    @Query(value = "SELECT * FROM libros l WHERE :language = ANY(l.languages)", nativeQuery = true)
    List<DatosLibros> findByLanguage(@Param("language") String language);




}
