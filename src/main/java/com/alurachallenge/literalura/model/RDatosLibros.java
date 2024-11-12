package com.alurachallenge.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RDatosLibros(
         @JsonAlias("title") String title,
         @JsonAlias("authors") List<RDatosAutores>authors,
         @JsonAlias("languages") List<String> languages,
         @JsonAlias("download_count") Integer download_count
) {
}
