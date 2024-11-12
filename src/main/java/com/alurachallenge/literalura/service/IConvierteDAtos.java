package com.alurachallenge.literalura.service;

public interface IConvierteDAtos {

    <T> T obtenerDatos(String json, Class<T> clase);
}
