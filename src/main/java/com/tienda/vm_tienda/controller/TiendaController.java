package com.tienda.vm_tienda.controller;

import com.tienda.vm_tienda.assembler.TiendaModelAssembler;
import com.tienda.vm_tienda.model.Tienda;
import com.tienda.vm_tienda.service.TiendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/tiendas")
@Tag(name = "Tienda", description = "API para gestionar tiendas.")
public class TiendaController {

    @Autowired
    private TiendaService tiendaService;

    @Autowired
    private TiendaModelAssembler assembler;

    @Operation(summary = "Listar todas las tiendas", description = "Obtiene una lista de todas las tiendas disponibles.")
    @ApiResponse(responseCode = "200", description = "Lista devuelta correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Tienda>>> getAllTiendas() {
        List<EntityModel<Tienda>> tiendas = tiendaService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(
                        tiendas,
                        linkTo(methodOn(TiendaController.class).getAllTiendas()).withSelfRel()
                )
        );
    }

    @Operation(summary = "Obtener tienda por ID", description = "Busca una tienda específica por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tienda encontrada"),
        @ApiResponse(responseCode = "404", description = "Tienda no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTiendaById(@PathVariable Integer id) {
        try {
            Tienda tienda = tiendaService.findById(id);
            return ResponseEntity.ok(assembler.toModel(tienda));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tienda no encontrada con ID: " + id);
        }
    }

    @Operation(summary = "Crear nueva tienda", description = "Crea una nueva tienda en el sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tienda creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflicto de integridad de datos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Tienda>> crearTienda(@RequestBody Tienda tienda) {
        try {
            Tienda nueva = tiendaService.save(tienda);
            return ResponseEntity
                    .created(linkTo(methodOn(TiendaController.class)
                            .getTiendaById(nueva.getIdTienda())).toUri())
                    .body(assembler.toModel(nueva));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Error de integridad de datos: " + ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Error al crear la tienda: " + ex.getMessage());
        }
    }

    @Operation(summary = "Actualizar tienda", description = "Actualiza los datos de una tienda existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tienda actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Tienda no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTienda(@PathVariable Integer id, @RequestBody Tienda tienda) {
        try {
            Tienda actualizada = tiendaService.update(id, tienda);
            return ResponseEntity.ok(assembler.toModel(actualizada));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tienda no encontrada con ID: " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar la tienda: " + ex.getMessage());
        }
    }

    @Operation(summary = "Eliminar tienda", description = "Elimina una tienda del sistema por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Tienda eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Tienda no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTienda(@PathVariable Integer id) {
        try {
            tiendaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tienda no encontrada con ID: " + id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al eliminar la tienda");
        }
    }
}