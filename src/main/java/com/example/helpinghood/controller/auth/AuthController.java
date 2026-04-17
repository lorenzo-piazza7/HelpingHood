package com.example.helpinghood.controller.auth;

import com.example.helpinghood.service.auth.RegistrationService;
import org.springframework.security.core.Authentication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ConditionalOnProperty(name = "app.auth.enabled", havingValue = "true", matchIfMissing = true)
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/auth";
    }

    @GetMapping("/auth")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/home";
        }
        return "pages/auth";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "pages/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nome,
                           @RequestParam String cognome,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "giovane") String ruolo,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        RegistrationService.RegistrationResult result = registrationService.register(
                new RegistrationService.RegistrationRequest(nome, cognome, email, password, ruolo)
        );

        if (!result.success()) {
            model.addAttribute("errorMessage", result.message());
            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("email", email);
            model.addAttribute("ruolo", ruolo);
            return "pages/register";
        }

        redirectAttributes.addFlashAttribute("successMessage", result.message());
        return "redirect:/auth";
    }
}
