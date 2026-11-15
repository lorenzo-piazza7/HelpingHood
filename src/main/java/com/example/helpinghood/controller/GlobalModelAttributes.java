package com.example.helpinghood.controller;

import com.example.helpinghood.model.Utente;
import com.example.helpinghood.service.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Value;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UtenteService utenteService;
    private final String mapTileUrl;
    private final String mapAttribution;

    public GlobalModelAttributes(UtenteService utenteService,
                                 @Value("${app.maps.tile-url}") String mapTileUrl,
                                 @Value("${app.maps.attribution}") String mapAttribution) {
        this.utenteService = utenteService;
        this.mapTileUrl = mapTileUrl;
        this.mapAttribution = mapAttribution;
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

    @ModelAttribute("mapTileUrl")
    public String mapTileUrl() {
        return mapTileUrl;
    }

    @ModelAttribute("mapAttribution")
    public String mapAttribution() {
        return mapAttribution;
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || authentication.getName() == null) {
            return false;
        }
        // Leggiamo il ruolo dal dominio applicativo, non solo dalle authority,
        // cosi le view usano la stessa fonte dati dei controller.
        Utente utente = utenteService.findActiveByEmail(authentication.getName()).orElse(null);
        return utente != null && role.equalsIgnoreCase(utente.getRuolo());
    }
}

