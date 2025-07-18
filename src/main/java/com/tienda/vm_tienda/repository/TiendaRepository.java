package com.tienda.vm_tienda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.vm_tienda.model.Tienda;

public interface TiendaRepository extends JpaRepository<Tienda, Integer> {
    Optional<Tienda> findByNombreAndDireccion(String nombre, String direccion);
}