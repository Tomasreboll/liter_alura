package com.alurachallenge.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "libros")
public class DatosLibros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    private List<String> languages;
    private Integer download_count;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "libros_autores",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<DatosAutores> authors;

    // Constructor por defecto
    public DatosLibros() {
    }

    // Constructor para la conexion con el Record:
    public DatosLibros(RDatosLibros r){
        this.title = r.title();
        this.authors = new ArrayList<>();
        for (RDatosAutores rAutores : r.authors()){
            this.authors.add(new DatosAutores(rAutores));
        }
        this.languages = r.languages();
        this.download_count = r.download_count();

    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DatosAutores> getAuthors() {
        return authors;
    }

    public void setAuthors(List<DatosAutores> authors) {
        this.authors = authors;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Integer getDownload_count() {
        return download_count;
    }

    public void setDownload_count(Integer download_count) {
        this.download_count = download_count;
    }

    @Override
    public String toString() {
        String autores = authors.stream()
                .map(DatosAutores::toString)
                .collect(Collectors.joining(", "));
        String idiomas = this.languages.stream()
                .collect(Collectors.joining(", "));

        return "----------Títulos---------\n" +
                "- Título: " + title + "\n" +
                "- Autor: " + autores + "\n" +
                "- Idioma: " + idiomas + "\n" +
                "- N° de descargas: " + download_count + "\n" +
                "--------------------------\n";
    }
}
