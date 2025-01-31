package rccommerce.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Table(name = "tb_address")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String street;

    @Column(length = 10)
    private String number;

    @Column(length = 50)
    private String complement;

    @Column(length = 50)
    private String district;

    @Column(length = 50)
    private String city;

    @Column(length = 2)
    private String state;

    @Column(length = 8)
    private String zipCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "suplier_id", nullable = true)
    private Suplier suplier;

    public void setState(String state) {
        this.state = state.toUpperCase();
    }

    public void setVipCode(String zipCode) {
        this.zipCode = zipCode.replaceAll("[^0-9]", "");
    }
}
