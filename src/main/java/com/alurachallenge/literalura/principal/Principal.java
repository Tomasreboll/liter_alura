package com.alurachallenge.literalura.principal;

import com.alurachallenge.literalura.model.Datos;
import com.alurachallenge.literalura.model.DatosAutores;
import com.alurachallenge.literalura.model.DatosLibros;
import com.alurachallenge.literalura.model.RDatosLibros;
import com.alurachallenge.literalura.repository.Repository;
import com.alurachallenge.literalura.repository.RepositoryAutor;
import com.alurachallenge.literalura.service.ConsumoAPI;
import com.alurachallenge.literalura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    // Todas las variables gobales a utilizar
    private static final String URL_BASE = "http://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    private Repository repository;
    private RepositoryAutor repositoryAutor;
    private List<DatosLibros> allLibros;
    private List<DatosAutores> allAutores;
    private DatosLibros dato;
    private Integer opcionUsuario;
    private List<DatosLibros> librosEnLaBaseDtos;

    public Principal(Repository repository, RepositoryAutor repositoryAutor) {
        this.repository = repository;
        this.repositoryAutor = repositoryAutor;
    }

    public void mensajeMenu() {
        System.out.println("""
                ------------------------------------------------
                Seleccionba una de las opciones:
                1- Buscar libro por título.
                2- Listar libros registrados.
                3- Listar autores registrados.
                4- Listar autores vivos en un determinado año.
                5- Listar libros por idioma.
                6- Top 10 libros más descargados.
    
                
                
                0- Salir.
                ------------------------------------------------
                """);
    }

    public void iniciaTodo() {

        opcionUsuario = -1;
        while (opcionUsuario != 0) {
            mensajeMenu();
            opcionUsuario = teclado.nextInt();
            teclado.nextLine();

            switch (opcionUsuario) {
                case 1:
                    busquedaPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnPorAño();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    obtenerlos10LibrosMasDescargados();
                    break;
                case 0:
                    System.out.println("Saliendo del programa.......");
                    break;
                default:
                    System.out.println("Ingresa una opción valida =D");
            }


        }
    }

    private void busquedaPorTitulo() {
        System.out.println("Ingresa el título que estas buscando:");
        var tituloBuscado = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloBuscado.replace(" ", "+"));
        var datosBusqueda = convierteDatos.obtenerDatos(json, Datos.class);

        var resultadoFinal = datosBusqueda.resultados().stream().findFirst();
        if (resultadoFinal.isPresent()) {
            dato = resultadoFinal.get();

            // para tartar al autor:
            tratameintoAutor();

            // para ver si el libro está repitido
            librosEnLaBaseDtos = repository.findByTitle(dato.getTitle());
            tratamientoLibro();

        } else {
            System.out.println("No se encontraron resultados =(");
        }
    }

    public void tratamientoLibro(){
        if (librosEnLaBaseDtos.isEmpty()) {
            repository.save(dato);
            System.out.println(dato);
            System.out.println("Libro guardado en la base de datos =D");
        } else {
            System.out.println(dato);
            System.out.println("Este libro ya se encuentra en la base de datos ;D");
        }

    }

    // Este metodo es para ver si el autor ya esta en la base datos, para evitar que este se duplique en la base de datos
    private void tratameintoAutor(){
        allAutores = new ArrayList<>();
        for (var autorData : dato.getAuthors()) {
            List<DatosAutores> autoresExistentes = repositoryAutor.findByName(autorData.getName());
            DatosAutores autor;
            if (autoresExistentes.isEmpty()){
                autor = new DatosAutores();
                autor.setName(autorData.getName());
                autor.setBirth_year(autorData.getBirth_year());
                autor.setDeath_year(autorData.getDeath_year());
                repositoryAutor.save(autor);
            }else {
                autor = autoresExistentes.get(0);
            }
            allAutores.add(autor);
        }
        dato.setAuthors(allAutores);
    }

    private void listarLibrosRegistrados() {
        allLibros = repository.findAll();
        System.out.println("**** Estos son todos los libros que has registrado hasta el momento *****\n");

        allLibros.stream()
                .forEach(l -> System.out.println(
                "---------- Título ---------\n" +
                "- Título: " + l.getTitle() + "\n" +
                "- Autor: " + l.getAuthors().get(0) + "\n" +
                "- Idioma: " + l.getLanguages().get(0) + "\n" +
                "- N° de descargas: " + l.getDownload_count() + "\n" +
                "--------------------------\n"
                ));
    }

    private void listarAutoresRegistrados() {
        // Obtener todos la informacion
        allAutores = repositoryAutor.findAll();
        allLibros = repository.findAllWithAuthors();
        System.out.println("***** Estos son los autores registrados *****");

        // Agrupar libros por autor
        Map<Long, List<String>> librosPorAutor = new HashMap<>();
        for (DatosLibros libro : allLibros) {
            for (DatosAutores autor : libro.getAuthors()) {
                // Usar una lista de títulos en lugar de objetos libro para evitar duplicados
                librosPorAutor
                        .computeIfAbsent(autor.getId(), k -> new ArrayList<>())
                        .add(libro.getTitle());
            }
        }
        // Imprimir información de cada autor
        for (DatosAutores autor : allAutores) {
            List<String> titulosDelAutor = librosPorAutor.getOrDefault(autor.getId(), new ArrayList<>());
            System.out.println(
                    "------------ Autor ------------\n" +
                            "Autor: " + autor.getName() + "\n" +
                            "Fecha de nacimiento: " + autor.getBirth_year() + "\n" +
                            "Fecha de muerte: " + autor.getDeath_year() + "\n" +
                            "Libros: " + (titulosDelAutor.isEmpty()
                            ? "No hay títulos registrados."
                            : titulosDelAutor.stream()
                            .distinct() // Asegurarse de que los títulos sean únicos
                            .collect(Collectors.joining(" - "))) + "\n" +
                            "-------------------------------"
            );
        }
    }

    private void listarAutoresVivosEnPorAño(){
        System.out.println("Escribe el año por el que quieres buscar");
        var anoBuscado = teclado.nextInt();
        teclado.nextLine();
        allAutores = repositoryAutor.findAuthorsByDeathYearLessThanEqual(anoBuscado);
        if (allAutores.isEmpty()){
            System.out.println("No se encontraron autores vivos durante el año "+ anoBuscado+" =(");
        } else {
            System.out.println("----- Autores vivos durante "+anoBuscado+"-----\n");
            allAutores.stream()
                    .forEach(a ->
                            System.out.println(
                            "-------------Autor-------------\n"+
                            "Autor: " + a.getName() + "\n" +
                            "Fecha de nacimiento: " + a.getBirth_year() + "\n"+
                            "Fecha de Fallecimiento: " + a.getDeath_year() + "\n"+
                            "-------------------------------"
                            ));
        }

    }

    private String language;
    private void listarLibrosPorIdioma() {
        System.out.println(
                "Estos son los idiomas disponibles:\n"+
                "1 - es (Español)\n"+
                "2 - en (Inglés)\n"+
                "3 - fr (Francés)\n"+
                "4 - pt (Portugués)\n"+
                "\n"+
                "Elige una de las opciones en el menú."
        );

        var idiomaSeleccionado = teclado.nextInt();
        teclado.nextLine();
        switch (idiomaSeleccionado){
            case 1:
                System.out.println("Estos son los libros en Español:");
                language = "es";
                obeterLosLibrosPorIdioma();
                break;
            case 2:
                System.out.println("Estos son los libros en Inglés:");
                language = "en";
                obeterLosLibrosPorIdioma();
                break;
            case 3:
                System.out.println("Estos son los libros en Francés:");
                language = "fr";
                obeterLosLibrosPorIdioma();
                break;
            case 4:
                System.out.println("Estos son los libros en Portugués:");
                language = "pt";
                obeterLosLibrosPorIdioma();
                break;
            default:
                System.out.println("Escoge una de las opciones en el menú");
        }

    }

    public void obeterLosLibrosPorIdioma(){
        allLibros = repository.findByLanguage(language);
        if (allLibros.isEmpty()){
            System.out.println(
                    "------------------------------------------------\n"+
                    "No hay registro de libros en ese idioma =(");
        } else {
            allLibros.stream().forEach(l ->
                    System.out.println(
                            "---------- Título ---------\n" +
                            "- Título: " + l.getTitle() + "\n" +
                            "- Autor: " + l.getAuthors().get(0) + "\n" +
                            "- Idioma: " + l.getLanguages().get(0) + "\n" +
                            "- N° de descargas: " + l.getDownload_count() + "\n" +
                            "--------------------------\n"
                    ));
        }
    }

    private void obtenerlos10LibrosMasDescargados() {
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = convierteDatos.obtenerDatos(json, Datos.class);

        // Optener el top 10
        System.out.println("--- Estas son los 10 libros con más descargas ---");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::getDownload_count).reversed())
                .limit(10).forEach(l ->
                        System.out.println(
                                "---------- Título ---------\n" +
                                        "- Título: " + l.getTitle() + "\n" +
                                        "- Autor: " + l.getAuthors().get(0) + "\n" +
                                        "- Idioma: " + l.getLanguages().get(0) + "\n" +
                                        "- N° de descargas: " + l.getDownload_count() + "\n" +
                                        "--------------------------\n"
                        ));
        System.out.println("-------------------------------------------------");
    }


}
