package rccommerce.tests;

import rccommerce.dto.ClientDTO;
import rccommerce.entities.Address;
import rccommerce.entities.Client;
import rccommerce.entities.Role;

public class FactoryClient {

    public static Client createClient() {
        Client client = new Client(1L, "John Black", "john@gmail.com", "Test@123", "83563189048");
        client.addRole(new Role(4L, "ROLE_CLIENT"));
        client.addAddresses(createAddress());
        return client;
    }

    public static ClientDTO createClientDTO() {
        Client client = createClient();
        return new ClientDTO(client);
    }

    public static ClientDTO createClientDTOWithFormattedCPF() {
        Client client = new Client(1L, "John Black", "john@gmail.com", "Test@123", "835.631.890-48");
        client.addRole(new Role(4L, "ROLE_CLIENT"));
        client.addAddresses(createAddress());
        return new ClientDTO(client);
    }

    public static Client createClientWithDifferentData() {
        Client client = new Client(2L, "Maria Silva", "maria@gmail.com", "Test@123", "47742716031");
        client.addRole(new Role(4L, "ROLE_CLIENT"));
        client.addAddresses(createAddress());
        return client;
    }

    private static Address createAddress() {
        return Address.builder()
                .street("Rua do Comércio")
                .number("321")
                .complement("Sala 2")
                .district("Downtown")
                .city("Rio de Janeiro")
                .state("RJ")
                .zipCode("20002000")
                .build();
    }
}
