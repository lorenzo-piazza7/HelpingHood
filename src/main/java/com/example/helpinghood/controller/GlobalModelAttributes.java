package com.example.helpinghood.controller;

import com.example.helpinghood.model.Utente;
import com.example.helpinghood.service.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UtenteService utenteService;

    public GlobalModelAttributes(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @ModelAttribute("isGiovane")
    public boolean isGiovane(Authentication authentication) {
        return hasRole(authentication, "giovane");
    }

    @ModelAttribute("isRichiedente")
    public boolean isRichiedente(Authentication authentication) {
        return hasRole(authentication, "richiedente");
    }

    @ModelAttribute("canCreateJob")
    public boolean canCreateJob(Authentication authentication) {
        return hasRole(authentication, "richiedente");
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || authentication.getName() == null) {
            return false;
        }
        Utente utente = utenteService.findActiveByEmail(authentication.getName()).orElse(null);
        return utente != null && role.equalsIgnoreCase(utente.getRuolo());
    }
}

