package rccommerce.services.exceptions;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlingAspect {
//
//    // Tratamento para DataIntegrityViolationException
//    @AfterThrowing(pointcut = "execution(* rccommerce.services.interfaces.GenericService.*(..))", throwing = "exception")
//    public void handleDataIntegrityViolation(DataIntegrityViolationException exception) {
//        if (exception.toString().contains("EMAIL NULLS FIRST")) {
//            throw new DatabaseException("Email informado já existe");
//        }
//        if (exception.toString().contains("NAME NULLS FIRST")) {
//            throw new DatabaseException("Nome informado já existe");
//        }
//        if (exception.toString().contains("CPF NULLS FIRST")) {
//            throw new DatabaseException("CPF informado já existe");
//        }
//        throw new DatabaseException("Violação de integridade no banco de dados");
//    }
//
//    // Tratamento para EntityNotFoundException
//    @AfterThrowing(pointcut = "execution(* rccommerce.services.interfaces.GenericService.*(..))", throwing = "exception")
//    public void handleEntityNotFoundException(EntityNotFoundException exception) {
//        throw new ResourceNotFoundException(exception.getMessage());
//    }
//    
//    // Tratamento para NoSuchElementException
//    @AfterThrowing(pointcut = "execution(* rccommerce.services.interfaces.GenericService.*(..))", throwing = "exception")
//    public void handleNoSuchElementException(NoSuchElementException exception) {
//    	throw new ResourceNotFoundException(exception.getMessage());
//    }
}

