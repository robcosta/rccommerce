package rccommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rccommerce.entities.Address;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddressDTO {

    @NotBlank(message = "O nome da rua é obrigatório")
    @Size(max = 100, message = "O nome da rua deve ter no máximo 100 caracteres")
    private String street;

    @NotBlank(message = "O número é obrigatório")
    @Size(max = 10, message = "O número deve ter no máximo 10 caracteres")
    private String number;

    @Size(max = 50, message = "O complemento deve ter no máximo 50 caracteres")
    private String complement;

    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 50, message = "O bairro deve ter no máximo 50 caracteres")
    private String district;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 50, message = "A cidade deve ter no máximo 50 caracteres")
    private String city;

    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "[a-z A-Z]{2}", message = "O estado deve conter 2 letras")
    private String state;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 00000-000 ou 00000000")
    private String zipCode;

    public AddressDTO(Address entity) {
        this.street = entity.getStreet();
        this.number = entity.getNumber();
        this.complement = entity.getComplement();
        this.district = entity.getDistrict();
        this.city = entity.getCity();
        this.state = entity.getState();
        this.zipCode = entity.getZipCode();
    }

    public String getZipCode() {
        return zipCode.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }
}
