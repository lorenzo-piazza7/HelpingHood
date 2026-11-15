package com.example.helpinghood.controller.auth;

import com.example.helpinghood.service.auth.RegistrationService;
import org.springframework.security.core.Authentication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

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
        // Evita di mostrare la login quando la sessione e gia valida.
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/home";
        }
        return "pages/auth";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        populateBirthDateRange(model);
        return "pages/register";
    }

    @PostMapping("/register")
    public Object register(@RequestParam String nome,
                           @RequestParam String cognome,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confermaPassword,
                           @RequestParam(defaultValue = "giovane") String ruolo,
                           @RequestParam String telefono,
                           @RequestParam String dataNascita,
                           @RequestParam String citta,
                           @RequestParam String indirizzo,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        // Tutta la logica di validazione/salvataggio e centralizzata nel service.
        RegistrationService.RegistrationResult result = registrationService.register(
                new RegistrationService.RegistrationRequest(
                        nome,
                        cognome,
                        email,
                        password,
                        confermaPassword,
                        ruolo,
                        telefono,
                        dataNascita,
                        citta,
                        indirizzo
                )
        );

        if (!result.success()) {
            // In caso di errore ripopoliamo il form per non perdere i dati inseriti.
            model.addAttribute("errorMessage", result.message());
            model.addAttribute("nome", nome);
            model.addAttribute("cognome", cognome);
            model.addAttribute("email", email);
            model.addAttribute("ruolo", ruolo);
            model.addAttribute("telefono", telefono);
            model.addAttribute("dataNascita", dataNascita);
            model.addAttribute("citta", citta);
            model.addAttribute("indirizzo", indirizzo);
            populateBirthDateRange(model);
            return "pages/register";
        }

        redirectAttributes.addFlashAttribute("successMessage", result.message());
        RedirectView redirect = new RedirectView("/auth");
        redirect.setStatusCode(HttpStatus.SEE_OTHER);
        return redirect;
    }

    private void populateBirthDateRange(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("minDataNascita", LocalDate.of(1900, 1, 1));
        model.addAttribute("maxDataNascita", today);
    }
}
