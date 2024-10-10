package rccommerce.services.interfaces;

public interface Convertible<T,U> {
	T convertDTO();
	U convertMinDTO();
}
