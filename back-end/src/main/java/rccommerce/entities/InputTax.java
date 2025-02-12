package rccommerce.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class InputTax extends Tax {
    // Herda todos os campos da classe Tax
}
