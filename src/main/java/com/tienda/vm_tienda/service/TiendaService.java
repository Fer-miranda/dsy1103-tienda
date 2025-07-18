package com.tienda.vm_tienda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tienda.vm_tienda.model.Tienda;
import com.tienda.vm_tienda.repository.TiendaRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TiendaService {

    @Autowired
    private TiendaRepository tiendaRepository;

    public List<Tienda> findAll() {
        return tiendaRepository.findAll();
    }

    public Tienda findById(Integer id) {
        return tiendaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tienda no encontrada con ID: " + id));
    }

    public Tienda save(Tienda tienda) {
        tiendaRepository.findByNombreAndDireccion(tienda.getNombre(), tienda.getDireccion())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Ya existe una tienda con ese nombre y dirección");
                });

        return tiendaRepository.save(tienda);
    }

    public Tienda update(Integer id, Tienda tiendaActualizada) {
        Tienda tienda = findById(id);
        tienda.setNombre(tiendaActualizada.getNombre());
        tienda.setDireccion(tiendaActualizada.getDireccion());
        return tiendaRepository.save(tienda);
    }

    public void delete(Integer id) {
        Tienda tienda = findById(id);
        tiendaRepository.delete(tienda);
    }
}
