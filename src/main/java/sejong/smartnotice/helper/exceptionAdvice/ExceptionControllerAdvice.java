package sejong.smartnotice.helper.exceptionAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice(basePackages = "sejong.smartnotice.controller.viewController")
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ModelAndView handleIllegalStateException(IllegalStateException e) {
        log.error("[IllegalStateException e]");
        return new ModelAndView("error/400");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ModelAndView handleNullPointerException(NullPointerException e) {
        log.error("[NullPointerException e]");
        return new ModelAndView("error/400");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ModelAndView handleAccessDeniedException(AccessDeniedException e, Model model) {
        log.error("[AccessDeniedException e] -> {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return new ModelAndView("error/403");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ModelAndView handleSecurityException(SecurityException e) {
        log.error("[SecurityException e]", e);
        return new ModelAndView("error/403");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ModelAndView handleException(Exception e) {
        log.error("[Exception e]", e);
        return new ModelAndView("error/500");
    }

}
