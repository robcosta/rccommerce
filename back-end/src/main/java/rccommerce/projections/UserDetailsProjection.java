package rccommerce.projections;

public interface UserDetailsProjection {

	Long getUserId();
	String getUsername();
	String getPassword();
	Long getRoleId();
	String getAuthority();
	Long getPermissionId();
	String getPermissionAuthority();
}
