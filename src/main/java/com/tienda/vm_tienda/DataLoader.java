package com.tienda.vm_tienda;

import com.tienda.vm_tienda.model.Tienda;
import com.tienda.vm_tienda.service.TiendaService;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TiendaService tiendaService;

    @Override
    public void run(String... args) throws Exception {

        if (!tiendaService.findAll().isEmpty()) {
            System.out.println("Tiendas ya existentes, no se crearán nuevas.");
            return;
        }

        Faker faker = new Faker();

        System.out.println("DataLoader: Cargando datos de prueba...");

        for (int i = 0; i < 5; i++) {
            Tienda tienda = new Tienda();
            tienda.setNombre("Tienda " + faker.company().name());
            tienda.setDireccion(faker.address().streetAddress());
            tiendaService.save(tienda);
        }

        System.out.println("DataLoader: 5 tiendas generadas OK.");
    }
}