package rccommerce.util;

public interface Convertible<T,U> {
	T convertDTO();
	U convertMinDTO();
}
