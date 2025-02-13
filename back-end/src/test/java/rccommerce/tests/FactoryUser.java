package rccommerce.tests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import rccommerce.dto.ClientDTO;
import rccommerce.dto.OperatorDTO;
import rccommerce.dto.UserDTO;
import rccommerce.dto.mindto.UserMinDTO;
import rccommerce.entities.Address;
import rccommerce.entities.Client;
import rccommerce.entities.Operator;
import rccommerce.entities.Permission;
import rccommerce.entities.Role;
import rccommerce.entities.User;
import rccommerce.projections.UserDetailsProjection;

public class FactoryUser {

    public static User createUser() {
        User user = new User(6L, "Administrador", "admin@gmail.com", "123456");
        user.addRole(createRoleAdmin());
        user.addRole(createRoleOperator());
        user.addPermission(createPermissionAll());
        return user;
    }

    public static User createNewUser() {
        return new User();
    }

    public static Role createRoleAdmin() {
        Role role = new Role(1L, "ROLE_ADMIN");
        return role;
    }

    public static Role createRoleOperator() {
        Role role = new Role(3L, "ROLE_OPERATOR");
        return role;
    }

    public static Role createRoleClient() {
        Role role = new Role(2L, "ROLE_CLIENT");
        return role;
    }

    public static Permission createPermissionAll() {
        Permission permission = new Permission(1L, "PERMISSION_ALL");
        return permission;
    }

    public static Permission createPermissionNone() {
        Permission permission = new Permission(1L, "PERMISSION_NONE");
        return permission;
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser());
    }

    public static UserDTO createUserDTO(User user) {
        return new UserDTO(user);
    }

    public static UserMinDTO createUserMinDTO() {
        return new UserMinDTO(createUser());
    }

    public static UserMinDTO createUserMinDTO(User user) {
        return new UserMinDTO(user);
    }

    public static List<UserDetailsProjection> createUserDetails() {
        List<UserDetailsProjection> list = new ArrayList<>();
        list.add(new UserDetailsImpl("robert@gmail.com", "$2a$10$Adpk5tdO8yFkIX.6IspH.OTF0dOxx2D9kx3drL6q4/1uLhoB/Ahze",
                1L, "ROLE_CLIENT"));
        return list;
    }

    public static Operator createOperatorAdmin() {
        Operator operator = new Operator(7L, "Ana Pink", "ana@gmail.com", "An-1234", new BigDecimal(1.5));
        operator.addRole(createRoleAdmin());
        operator.addPermission(createPermissionAll());
        return operator;
    }

    public static Operator createNewOperator() {
        return new Operator();
    }

    // public static Operator createOperator(User user) {
    // Operator operator = new Operator(user.getId(), user.getName(),
    // user.getEmail(), user.getPassword(), 1.5);
    // operator.addRole(createRoleAdmin());
    // operator.addPermission(createPermissionAll());
    // return operator;
    // }
    public static OperatorDTO createOperatorDTO(Operator operator) {
        OperatorDTO operatorDto = new OperatorDTO(operator);
        return operatorDto;
    }

    public static Client createClient() {
        Client client = new Client(7L, "Maria Pink", "maria@gmail.com", "An-1234", "59395734019");
        client.addRole(createRoleClient());
        client.addPermission(createPermissionNone());
        client.addAddresses(createAddress());
        return client;
    }

    public static Client createNewClient() {
        return new Client();
    }

    public static ClientDTO createClientDTO(Client client) {
        ClientDTO clientDto = new ClientDTO(client);
        return clientDto;
    }

    public static User createUserWithRolesAndPermissions() {
        User user = new User(1L, "Admin Test", "admin.test@gmail.com", "123456");
        user.addRole(createRoleAdmin());
        user.addRole(createRoleOperator());
        user.addPermission(createPermissionAll());
        return user;
    }

    public static Address createAddress() {
        return new Address(1L, "Main Street", "123", "Apt 4B", "12345-678", "City", "State", "Country", null, null);
    }
}

// Classe auxilar para implementação de uma instância de UserDetailsProjection
class UserDetailsImpl implements UserDetailsProjection {

    private String username;
    private String password;
    private Long roleId;
    private String authority;

    public UserDetailsImpl(String username, String password, Long roleId, String authority) {
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.authority = authority;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public Long getUserId() {
        return null;
    }

    @Override
    public Long getPermissionId() {
        return null;
    }

    @Override
    public String getPermissionAuthority() {
        return null;
    }
}
