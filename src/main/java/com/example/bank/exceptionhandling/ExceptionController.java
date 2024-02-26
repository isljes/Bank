package com.example.bank.exceptionhandling;

import com.example.bank.customexception.DAOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(DAOException.class)
    public String daoException(DAOException daoException,
                               RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("UIExceptionDTO",new UIExceptionDTO(HttpStatus.NOT_FOUND,daoException.getMessage()));
        log.warn("DAOException : {}",daoException.getMessage());
        return "redirect:/welcome";
    }

    @ExceptionHandler(Exception.class)
    public String globalException(Exception exception,RedirectAttributes redirectAttributes){
        //redirectAttributes.addFlashAttribute("UIExceptionDTO",new UIExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR,exception.getMessage()));
        System.out.println(exception.getMessage()+exception.getCause());
        return "redirect:/welcome";
    }
}
