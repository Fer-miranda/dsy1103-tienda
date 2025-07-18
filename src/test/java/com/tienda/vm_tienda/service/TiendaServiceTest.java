package com.tienda.vm_tienda.service;

import com.tienda.vm_tienda.model.Tienda;
import com.tienda.vm_tienda.repository.TiendaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TiendaServiceTest {

    @Autowired
    private TiendaService tiendaService;

    @MockitoBean
    private TiendaRepository tiendaRepository;

    @Test
    void testFindAll() {
        Tienda tienda1 = new Tienda(1, "Tienda A", "Dirección A");
        Tienda tienda2 = new Tienda(2, "Tienda B", "Dirección B");

        when(tiendaRepository.findAll()).thenReturn(List.of(tienda1, tienda2));

        List<Tienda> result = tiendaService.findAll();

        assertEquals(2, result.size());
        assertEquals("Tienda A", result.get(0).getNombre());
        assertEquals("Tienda B", result.get(1).getNombre());
        verify(tiendaRepository, times(1)).findAll();
    }

    @Test
    void testFindAllVacio() {
        when(tiendaRepository.findAll()).thenReturn(List.of());

        List<Tienda> result = tiendaService.findAll();

        assertTrue(result.isEmpty());
        verify(tiendaRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Tienda tienda = new Tienda(1, "Tienda Test", "Dirección Test");
        when(tiendaRepository.findById(1)).thenReturn(Optional.of(tienda));

        Tienda result = tiendaService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getIdTienda());
        assertEquals("Tienda Test", result.getNombre());
        assertEquals("Dirección Test", result.getDireccion());
        verify(tiendaRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNoEncontrado() {
        when(tiendaRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> tiendaService.findById(999)
        );

        assertEquals("Tienda no encontrada con ID: 999", exception.getMessage());
        verify(tiendaRepository, times(1)).findById(999);
    }

    @Test
    void testSave() {
        Tienda tiendaNueva = new Tienda(null, "Nueva Tienda", "Nueva Dirección");
        Tienda tiendaGuardada = new Tienda(1, "Nueva Tienda", "Nueva Dirección");

        when(tiendaRepository.save(any(Tienda.class))).thenReturn(tiendaGuardada);

        Tienda result = tiendaService.save(tiendaNueva);

        assertNotNull(result);
        assertEquals(1, result.getIdTienda());
        assertEquals("Nueva Tienda", result.getNombre());
        assertEquals("Nueva Dirección", result.getDireccion());
        verify(tiendaRepository, times(1)).save(tiendaNueva);
    }

    @Test
    void testUpdate() {
        Tienda tiendaExistente = new Tienda(1, "Tienda Original", "Dirección Original");
        Tienda tiendaActualizada = new Tienda(null, "Tienda Modificada", "Dirección Modificada");
        Tienda tiendaGuardada = new Tienda(1, "Tienda Modificada", "Dirección Modificada");

        when(tiendaRepository.findById(1)).thenReturn(Optional.of(tiendaExistente));
        when(tiendaRepository.save(any(Tienda.class))).thenReturn(tiendaGuardada);

        Tienda result = tiendaService.update(1, tiendaActualizada);

        assertNotNull(result);
        assertEquals(1, result.getIdTienda());
        assertEquals("Tienda Modificada", result.getNombre());
        assertEquals("Dirección Modificada", result.getDireccion());
        verify(tiendaRepository, times(1)).findById(1);
        verify(tiendaRepository, times(1)).save(tiendaExistente);
    }

    @Test
    void testUpdateTiendaNoEncontrada() {
        Tienda tiendaActualizada = new Tienda(null, "Tienda Modificada", "Dirección Modificada");
        
        when(tiendaRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> tiendaService.update(999, tiendaActualizada)
        );

        assertEquals("Tienda no encontrada con ID: 999", exception.getMessage());
        verify(tiendaRepository, times(1)).findById(999);
        verify(tiendaRepository, never()).save(any(Tienda.class));
    }

    @Test
    void testDelete() {
        Tienda tienda = new Tienda(1, "Tienda a Eliminar", "Dirección");
        
        when(tiendaRepository.findById(1)).thenReturn(Optional.of(tienda));
        doNothing().when(tiendaRepository).delete(tienda);

        assertDoesNotThrow(() -> tiendaService.delete(1));

        verify(tiendaRepository, times(1)).findById(1);
        verify(tiendaRepository, times(1)).delete(tienda);
    }

    @Test
    void testDeleteTiendaNoEncontrada() {
        when(tiendaRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
            NoSuchElementException.class,
            () -> tiendaService.delete(999)
        );

        assertEquals("Tienda no encontrada con ID: 999", exception.getMessage());
        verify(tiendaRepository, times(1)).findById(999);
        verify(tiendaRepository, never()).delete(any(Tienda.class));
    }
}