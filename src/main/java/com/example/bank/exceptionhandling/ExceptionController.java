package com.example.bank.exceptionhandling;

import com.example.bank.service.exception.DAOException;
import com.example.bank.service.exception.ServiceException;
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
        redirectAttributes.addFlashAttribute("UIExceptionDTO",
                new UIExceptionDTO(HttpStatus.NOT_FOUND,daoException.getMessage()));
        log.warn("DAOException -> {}", daoException.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(ServiceException.class)
    public String daoException(ServiceException serviceException,
                               RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("UIExceptionDTO",
                new UIExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong"));
        log.warn("ServiceException -> {}",serviceException.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String globalException(Exception exception,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("UIExceptionDTO",
                new UIExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong"));
        log.warn("Exception->{}",exception.getMessage());
        return "redirect:/";
    }
}
