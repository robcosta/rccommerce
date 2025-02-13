package rccommerce.tests;

import rccommerce.dto.SuplierDTO;
import rccommerce.dto.mindto.SuplierMinDTO;
import rccommerce.entities.Suplier;

public class FactorySuplier {

    public static Suplier createSuplier() {
        Suplier suplier = new Suplier(7L, "América Close", "02745767000175");
        return suplier;
    }

    public static Suplier createNewsuplier() {
        return new Suplier();
    }

    public static SuplierDTO createSuplierDTO() {
        return new SuplierDTO(createSuplier());
    }

    public static SuplierDTO createSuplierDTO(Suplier entity) {
        return new SuplierDTO(entity);
    }

    public static SuplierMinDTO createSuplierMinDTO() {
        return new SuplierMinDTO(createSuplier());
    }
}
