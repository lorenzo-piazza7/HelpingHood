package com.example.helpinghood.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DemoDataService {

    public List<Category> categories() {
        return List.of(
                new Category(1, "Ripetizioni", "📚", "#2E7D32"),
                new Category(2, "Babysitting", "🧸", "#388E3C"),
                new Category(3, "Spesa e commissioni", "🛒", "#43A047"),
                new Category(4, "Aiuto tech", "📱", "#4CAF50"),
                new Category(8, "Supporto eventi", "🎪", "#C8E6C9")
        );
    }

    public List<Job> jobs() {
        return List.of(
                new Job(1, "Cerco babysitter per martedì pomeriggio", "Ho due nipotini di 4 e 6 anni, cerco supporto per gioco e merenda.", "Babysitting", "Lecco - Pescarenico", 1.2, 3.0, LocalDate.of(2026, 4, 21), LocalTime.of(15, 0), new BigDecimal("8.00"), true, "Signora Verdi"),
                new Job(2, "Ripetizioni di matematica (terza media)", "Cerco uno studente paziente per geometria e frazioni, 2 pomeriggi a settimana.", "Ripetizioni", "Lecco - Centro", 0.8, 1.5, LocalDate.of(2026, 4, 22), LocalTime.of(16, 0), new BigDecimal("10.00"), false, "Famiglia Ferrari"),
                new Job(3, "Spesa al supermercato per venerdì", "Serve aiuto per fare la spesa con lista già pronta.", "Spesa e commissioni", "Lecco - Stazione", 1.6, 2.0, LocalDate.of(2026, 4, 19), LocalTime.of(10, 0), new BigDecimal("7.00"), true, "Signora Verdi"),
                new Job(4, "Aiuto smartphone per anziana", "Configurare Android e spiegare WhatsApp in modo semplice.", "Aiuto tech", "Lecco - Caleotto", 2.4, 2.0, LocalDate.of(2026, 4, 20), LocalTime.of(15, 30), new BigDecimal("20.00"), false, "Luca Ferrari"),
                new Job(5, "Supporto evento Lecco in Fiore", "Accoglienza visitatori e supporto logistico durante la manifestazione.", "Supporto eventi", "Lecco - Piazza Manzoni", 1.0, 6.0, LocalDate.of(2026, 4, 25), LocalTime.of(9, 0), new BigDecimal("0.00"), true, "Comune di Lecco")
        );
    }

    public Job jobById(long id) {
        return jobs().stream().filter(job -> job.id() == id).findFirst().orElse(jobs().get(0));
    }

    public List<YoungProfile> youngProfiles() {
        return List.of(
                new YoungProfile(1, "Matteo Rossi", 17, "Studente al Badoni, appassionato di tecnologia e ripetizioni.",
                        List.of("Matematica", "Fisica", "Aiuto tech"), "Lun-Ven 16:00-19:00", "Lecco centro", 4.8, 7),
                new YoungProfile(2, "Sara Colombo", 18, "Amo i bambini e la natura. Disponibile nel pomeriggio e weekend.",
                        List.of("Babysitting", "Compagnia anziani", "Spesa"), "Mar-Sab 14:00-19:00", "Lecco Pescarenico", 4.9, 5),
                new YoungProfile(3, "Luca Ferrari", 16, "Bravo in matematica e supporto smartphone per anziani.",
                        List.of("Ripetizioni", "Aiuto tech"), "Lun-Gio 15:30-18:30", "Lecco Caleotto", 4.7, 3)
        );
    }

    public YoungProfile youngProfileById(long id) {
        return youngProfiles().stream().filter(profile -> profile.id() == id).findFirst().orElse(youngProfiles().get(0));
    }

    public List<Review> reviews() {
        return List.of(
                new Review("Signora Verdi", "Sara Colombo", 5, "Puntuale, dolce con i bambini e molto affidabile."),
                new Review("Matteo Rossi", "Famiglia Ferrari", 4, "Ambiente sereno, esperienza utile e ben organizzata."),
                new Review("Comune di Lecco", "Giulia Bianchi", 5, "Grande energia durante l'evento, consigliata.")
        );
    }

    public List<ChatMessage> chatMessages() {
        return List.of(
                new ChatMessage("Signora Verdi", "Ciao Sara! Sei disponibile martedì alle 15?", "15:02", false),
                new ChatMessage("Tu", "Ciao! Sì certo, ci sono. Porto qualcosa?", "15:05", true),
                new ChatMessage("Signora Verdi", "No grazie, pensiamo a tutto noi 😊", "15:06", false)
        );
    }

    public DashboardStats youngStats() {
        return new DashboardStats(12, 4, 2, 4.9);
    }

    public DashboardStats requesterStats() {
        return new DashboardStats(5, 18, 3, 4.7);
    }

    public record Category(long id, String name, String icon, String colorHex) {
    }

    public record Job(long id,
                      String title,
                      String description,
                      String category,
                      String zone,
                      double distanceKm,
                      double durationHours,
                      LocalDate day,
                      LocalTime time,
                      BigDecimal hourlyCompensation,
                      boolean urgent,
                      String requesterName) {
    }

    public record YoungProfile(long id,
                               String name,
                               int age,
                               String bio,
                               List<String> skills,
                               String availability,
                               String zone,
                               double rating,
                               int jobsDone) {
    }

    public record Review(String author, String target, int rating, String text) {
    }

    public record ChatMessage(String sender, String text, String time, boolean mine) {
    }

    public record DashboardStats(int nearbyJobs, int applications, int activeChats, double reputation) {
    }
}
