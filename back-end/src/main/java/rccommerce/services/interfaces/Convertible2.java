package rccommerce.services.interfaces;

/**
 * Interface para conversão bidirecional entre entidades e DTOs. Permite
 * conversão de uma entidade para seus DTOs (completo e mínimo) e vice-versa.
 *
 * @param <ENTITY> Tipo da entidade (ex: Client, Product)
 * @param <DTO> Tipo do DTO completo (ex: ClientDTO, ProductDTO)
 * @param <MINDTO> Tipo do DTO mínimo (ex: ClientMinDTO, ProductMinDTO)
 *
 * @author Rafael Carvalho
 * @version 1.0
 */
public interface Convertible2<ENTITY, DTO, MINDTO> {

    /**
     * Converte um DTO em uma entidade.
     *
     * @param dto O DTO a ser convertido em entidade
     * @return A entidade convertida
     */
    ENTITY convertEntity(DTO dto);

    /**
     * Converte uma entidade em um DTO completo.
     *
     * @return O DTO completo convertido
     */
    DTO convertDTO();

    /**
     * Converte uma entidade em um DTO mínimo.
     *
     * @return O DTO mínimo convertido
     */
    MINDTO convertMinDTO();

}
