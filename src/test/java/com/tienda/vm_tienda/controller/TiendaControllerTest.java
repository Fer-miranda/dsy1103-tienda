package com.tienda.vm_tienda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.vm_tienda.assembler.TiendaModelAssembler;
import com.tienda.vm_tienda.model.Tienda;
import com.tienda.vm_tienda.service.TiendaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TiendaController.class)
public class TiendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TiendaService tiendaService;

    @MockitoBean
    private TiendaModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTiendas() throws Exception {
        Tienda tienda1 = new Tienda(1, "Tienda A", "Dirección A");
        Tienda tienda2 = new Tienda(2, "Tienda B", "Dirección B");

        when(tiendaService.findAll()).thenReturn(List.of(tienda1, tienda2));
        when(assembler.toModel(any(Tienda.class))).thenReturn(EntityModel.of(tienda1));

        mockMvc.perform(get("/api/v1/tiendas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));

        verify(tiendaService, times(1)).findAll();
    }

    @Test
    void testGetAllTiendasVacio() throws Exception {
        when(tiendaService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/tiendas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));

        verify(tiendaService, times(1)).findAll();
    }

    @Test
    void testGetTiendaById() throws Exception {
        Tienda tienda = new Tienda(1, "Tienda Test", "Dirección Test");
        
        when(tiendaService.findById(1)).thenReturn(tienda);
        when(assembler.toModel(tienda)).thenReturn(EntityModel.of(tienda));

        mockMvc.perform(get("/api/v1/tiendas/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));

        verify(tiendaService, times(1)).findById(1);
    }

    @Test
    void testGetTiendaByIdNoEncontrada() throws Exception {
        when(tiendaService.findById(999)).thenThrow(new NoSuchElementException("Tienda no encontrada con ID: 999"));

        mockMvc.perform(get("/api/v1/tiendas/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tienda no encontrada con ID: 999"));

        verify(tiendaService, times(1)).findById(999);
    }

    @Test
    void testCrearTienda() throws Exception {
        Tienda tiendaNueva = new Tienda(null, "Nueva Tienda", "Nueva Dirección");
        Tienda tiendaGuardada = new Tienda(1, "Nueva Tienda", "Nueva Dirección");

        when(tiendaService.save(any(Tienda.class))).thenReturn(tiendaGuardada);
        when(assembler.toModel(tiendaGuardada)).thenReturn(EntityModel.of(tiendaGuardada));

        mockMvc.perform(post("/api/v1/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaNueva)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentType("application/hal+json"));

        verify(tiendaService, times(1)).save(any(Tienda.class));
    }

    @Test
    void testCrearTiendaConflictoIntegridad() throws Exception {
        Tienda tienda = new Tienda(null, "Tienda Duplicada", "Dirección");

        when(tiendaService.save(any(Tienda.class))).thenThrow(new DataIntegrityViolationException("Violación de integridad"));

        mockMvc.perform(post("/api/v1/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tienda)))
                .andExpect(status().isConflict());

        verify(tiendaService, times(1)).save(any(Tienda.class));
    }

    @Test
    void testCrearTiendaErrorGeneral() throws Exception {
        Tienda tienda = new Tienda(null, "Tienda Error", "Dirección");

        when(tiendaService.save(any(Tienda.class))).thenThrow(new RuntimeException("Error general"));

        mockMvc.perform(post("/api/v1/tiendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tienda)))
                .andExpect(status().isBadRequest());

        verify(tiendaService, times(1)).save(any(Tienda.class));
    }

    @Test
    void testActualizarTienda() throws Exception {
        Tienda tiendaActualizada = new Tienda(null, "Tienda Modificada", "Dirección Modificada");
        Tienda tiendaGuardada = new Tienda(1, "Tienda Modificada", "Dirección Modificada");

        when(tiendaService.update(eq(1), any(Tienda.class))).thenReturn(tiendaGuardada);
        when(assembler.toModel(tiendaGuardada)).thenReturn(EntityModel.of(tiendaGuardada));

        mockMvc.perform(put("/api/v1/tiendas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tiendaActualizada)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));

        verify(tiendaService, times(1)).update(eq(1), any(Tienda.class));
    }

    @Test
    void testActualizarTiendaNoEncontrada() throws Exception {
        Tienda tienda = new Tienda(null, "Tienda", "Dirección");

        when(tiendaService.update(eq(999), any(Tienda.class))).thenThrow(new NoSuchElementException("Tienda no encontrada con ID: 999"));

        mockMvc.perform(put("/api/v1/tiendas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tienda)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tienda no encontrada con ID: 999"));

        verify(tiendaService, times(1)).update(eq(999), any(Tienda.class));
    }

    @Test
    void testActualizarTiendaErrorGeneral() throws Exception {
        Tienda tienda = new Tienda(null, "Tienda", "Dirección");

        when(tiendaService.update(eq(1), any(Tienda.class))).thenThrow(new RuntimeException("Error general"));

        mockMvc.perform(put("/api/v1/tiendas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tienda)))
                .andExpect(status().isBadRequest());

        verify(tiendaService, times(1)).update(eq(1), any(Tienda.class));
    }

    @Test
    void testEliminarTienda() throws Exception {
        doNothing().when(tiendaService).delete(1);

        mockMvc.perform(delete("/api/v1/tiendas/1"))
                .andExpect(status().isNoContent());

        verify(tiendaService, times(1)).delete(1);
    }

    @Test
    void testEliminarTiendaNoEncontrada() throws Exception {
        doThrow(new NoSuchElementException("Tienda no encontrada con ID: 999")).when(tiendaService).delete(999);

        mockMvc.perform(delete("/api/v1/tiendas/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Tienda no encontrada con ID: 999"));

        verify(tiendaService, times(1)).delete(999);
    }

    @Test
    void testEliminarTiendaErrorInterno() throws Exception {
        doThrow(new RuntimeException("Error interno")).when(tiendaService).delete(1);

        mockMvc.perform(delete("/api/v1/tiendas/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno al eliminar la tienda"));

        verify(tiendaService, times(1)).delete(1);
    }
}