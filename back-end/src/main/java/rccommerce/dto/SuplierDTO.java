package rccommerce.dto;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Suplier;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SuplierDTO {

    private Long id;

    @NotBlank(message = "Campo requerido")
    @Size(min = 3, max = 80, message = "Nome precisa ter de 3 a 80 caracteres.")
    private String name;

    @NotBlank(message = "Campo requerido")
    @CNPJ
    private String cnpj;

    public SuplierDTO(Suplier entity) {
        id = entity.getId();
        name = entity.getName();
        cnpj = entity.getCnpj();
    }

    public String getCnpj() {
        return cnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
}
