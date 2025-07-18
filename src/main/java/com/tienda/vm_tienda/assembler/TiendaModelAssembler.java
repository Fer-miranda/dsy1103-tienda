package com.tienda.vm_tienda.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.tienda.vm_tienda.controller.TiendaController;
import com.tienda.vm_tienda.model.Tienda;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TiendaModelAssembler implements RepresentationModelAssembler<Tienda, EntityModel<Tienda>> {

    @Override
    public EntityModel<Tienda> toModel(Tienda tienda) {
        return EntityModel.of(
            tienda,
            linkTo(methodOn(TiendaController.class).getTiendaById(tienda.getIdTienda())).withSelfRel(),
            linkTo(methodOn(TiendaController.class).getAllTiendas()).withRel("tiendas"),
            linkTo(methodOn(TiendaController.class).actualizarTienda(tienda.getIdTienda(), null)).withRel("actualizar"),
            linkTo(methodOn(TiendaController.class).eliminarTienda(tienda.getIdTienda())).withRel("eliminar")
        );
    }
}