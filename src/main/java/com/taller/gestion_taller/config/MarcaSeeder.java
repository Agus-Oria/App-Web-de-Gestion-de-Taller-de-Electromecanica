package com.taller.gestion_taller.config;

import com.taller.gestion_taller.entity.Marca;
import com.taller.gestion_taller.repository.MarcaRepository;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MarcaSeeder implements ApplicationRunner {

    private static final List<String> MARCAS = List.of(
            "AGRALE",
            "ALFA ROMEO",
            "ARCFOX",
            "AUDI",
            "BAIC",
            "BMW",
            "BYD",
            "CHANGAN",
            "CHERY",
            "CHEVROLET",
            "CHRYSLER",
            "CITROEN",
            "DFSK",
            "DODGE",
            "DOMY",
            "DONGFENG",
            "DS AUTOMOBILES",
            "FAW",
            "FERRARI",
            "FIAT",
            "FORD",
            "FORTHING",
            "FOTON",
            "GAC",
            "GEELY",
            "GREAT WALL",
            "HAVAL",
            "HONDA",
            "HYUNDAI",
            "ISUZU",
            "JAC",
            "JAGUAR",
            "JEEP",
            "JETOUR",
            "JMC",
            "JMEV",
            "KAIYI",
            "КIA",
            "KYC",
            "LAND ROVER",
            "LEAPMOTOR",
            "LEXUS",
            "LIFAN",
            "LOTUS",
            "LYNK&CO",
            "MASERATI",
            "MAXUS",
            "McLAREN",
            "MERCEDES BENZ",
            "MG",
            "MINI COOPER",
            "MITSUBISHI",
            "NISSAN",
            "PEUGEOT",
            "PORSCHE",
            "RAM",
            "RELY",
            "RENAULT",
            "SHINERAY",
            "SKYWELL",
            "SMART",
            "SOUEAST",
            "SSANGYONG",
            "SUBARU",
            "SUZUKI",
            "TOYOTA",
            "VOLKSWAGEN",
            "VOLVO",
            "ZANELLA");

    private final MarcaRepository marcaRepository;

    public MarcaSeeder(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (marcaRepository.count() > 0) {
            return;
        }
        MARCAS.stream()
                .map(nombre -> {
                    Marca marca = new Marca();
                    marca.setNombre(nombre.trim());
                    return marca;
                })
                .forEach(marcaRepository::save);
    }
}
