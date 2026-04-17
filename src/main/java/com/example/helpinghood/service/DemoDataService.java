package com.example.helpinghood.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Service
public class DemoDataService {

    private final long demoGiovaneId;
    private final long demoRichiedenteId;
    private final double baseLat;
    private final double baseLon;

    private final JdbcClient jdbcClient;

    public DemoDataService(ObjectProvider<JdbcClient> jdbcClientProvider,
                           @Value("${app.demo.giovane-id:2}") long demoGiovaneId,
                           @Value("${app.demo.richiedente-id:5}") long demoRichiedenteId,
                           @Value("${app.demo.base-lat:45.8565}") double baseLat,
                           @Value("${app.demo.base-lon:9.3967}") double baseLon) {
        this.jdbcClient = jdbcClientProvider.getIfAvailable();
        this.demoGiovaneId = demoGiovaneId;
        this.demoRichiedenteId = demoRichiedenteId;
        this.baseLat = baseLat;
        this.baseLon = baseLon;
    }

    public List<Category> categories() {
        if (jdbcClient == null) {
            return List.of();
        }


        return safe(() -> jdbcClient.sql("""
                        SELECT id, nome, icona, colore_hex
                        FROM categorie
                        ORDER BY nome
                        """)
                .query((rs, rowNum) -> new Category(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("icona"),
                        rs.getString("colore_hex")
                )).list(), List.of());
    }

    public List<Job> jobs() {
        if (jdbcClient == null) {
            return List.of();
        }


        return safe(() -> jdbcClient.sql("""
                        SELECT id, titolo, descrizione, categoria_nome, citta, luogo_note,
                               latitudine, longitudine, compenso_importo, data_inizio,
                               ora_inizio, durata_ore, urgente, richiedente_nome
                        FROM v_annunci_attivi
                        ORDER BY urgente DESC, data_inizio ASC, ora_inizio ASC
                        """)
                .query((rs, rowNum) -> {
                    Double lat = (Double) rs.getObject("latitudine");
                    Double lon = (Double) rs.getObject("longitudine");
                    return new Job(
                            rs.getLong("id"),
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getString("categoria_nome"),
                            zone(rs.getString("citta"), rs.getString("luogo_note")),
                            distanceKm(lat, lon),
                            rs.getBigDecimal("durata_ore") != null ? rs.getBigDecimal("durata_ore").doubleValue() : 0,
                            rs.getObject("data_inizio", LocalDate.class),
                            rs.getObject("ora_inizio", LocalTime.class),
                            rs.getBigDecimal("compenso_importo") != null ? rs.getBigDecimal("compenso_importo") : BigDecimal.ZERO,
                            rs.getBoolean("urgente"),
                            rs.getString("richiedente_nome")
                    );
                }).list(), List.of());
    }

    public Job jobById(long id) {
        if (jdbcClient == null) {
            return defaultJob();
        }


        return safe(() -> jdbcClient.sql("""
                        SELECT id, titolo, descrizione, categoria_nome, citta, luogo_note,
                               latitudine, longitudine, compenso_importo, data_inizio,
                               ora_inizio, durata_ore, urgente, richiedente_nome
                        FROM v_annunci_attivi
                        WHERE id = ?
                        """)
                .param(id)
                .query((rs, rowNum) -> {
                    Double lat = (Double) rs.getObject("latitudine");
                    Double lon = (Double) rs.getObject("longitudine");
                    return new Job(
                            rs.getLong("id"),
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getString("categoria_nome"),
                            zone(rs.getString("citta"), rs.getString("luogo_note")),
                            distanceKm(lat, lon),
                            rs.getBigDecimal("durata_ore") != null ? rs.getBigDecimal("durata_ore").doubleValue() : 0,
                            rs.getObject("data_inizio", LocalDate.class),
                            rs.getObject("ora_inizio", LocalTime.class),
                            rs.getBigDecimal("compenso_importo") != null ? rs.getBigDecimal("compenso_importo") : BigDecimal.ZERO,
                            rs.getBoolean("urgente"),
                            rs.getString("richiedente_nome")
                    );
                })
                .optional()
                .orElseGet(() -> jobs().stream().findFirst().orElse(defaultJob())), defaultJob());
    }

    public List<YoungProfile> youngProfiles() {
        if (jdbcClient == null) {
            return List.of();
        }


        return safe(() -> jdbcClient.sql("""
                        SELECT vp.id,
                               vp.nome,
                               vp.cognome,
                               vp.anni,
                               vp.bio,
                               vp.citta,
                               vp.punteggio_medio,
                               vp.n_lavori_completati,
                               COALESCE(GROUP_CONCAT(DISTINCT c.nome ORDER BY c.nome SEPARATOR ','), '') AS skills,
                               COALESCE(GROUP_CONCAT(DISTINCT CONCAT(LEFT(d.giorno,3), ' ',
                                   TIME_FORMAT(d.ora_inizio, '%H:%i'), '-', TIME_FORMAT(d.ora_fine, '%H:%i'))
                                   ORDER BY d.giorno SEPARATOR ' | '), 'Da concordare') AS availability
                        FROM v_profilo_giovane vp
                        JOIN profili_giovani pg ON pg.utente_id = vp.id
                        LEFT JOIN giovane_competenze gc ON gc.giovane_id = pg.id
                        LEFT JOIN competenze c ON c.id = gc.competenza_id
                        LEFT JOIN disponibilita d ON d.giovane_id = pg.id
                        GROUP BY vp.id, vp.nome, vp.cognome, vp.anni, vp.bio, vp.citta,
                                 vp.punteggio_medio, vp.n_lavori_completati
                        ORDER BY vp.punteggio_medio DESC, vp.n_lavori_completati DESC
                        """)
                .query((rs, rowNum) -> new YoungProfile(
                        rs.getLong("id"),
                        rs.getString("nome") + " " + rs.getString("cognome"),
                        rs.getInt("anni"),
                        rs.getString("bio"),
                        parseSkills(rs.getString("skills")),
                        rs.getString("availability"),
                        rs.getString("citta"),
                        rs.getBigDecimal("punteggio_medio") != null ? rs.getBigDecimal("punteggio_medio").doubleValue() : 0,
                        rs.getInt("n_lavori_completati")
                )).list(), List.of());
    }

    public YoungProfile youngProfileById(long id) {
        return youngProfiles().stream().filter(profile -> profile.id() == id).findFirst().orElse(youngProfiles().stream().findFirst().orElse(defaultProfile()));
    }

    public List<Review> reviews() {
        if (jdbcClient == null) {
            return List.of();
        }


        return safe(() -> jdbcClient.sql("""
                        SELECT CONCAT(a.nome, ' ', a.cognome) AS autore,
                               CONCAT(d.nome, ' ', d.cognome) AS destinatario,
                               r.voto,
                               COALESCE(r.testo, 'Nessun commento') AS testo
                        FROM recensioni r
                        JOIN utenti a ON a.id = r.autore_id
                        JOIN utenti d ON d.id = r.destinatario_id
                        ORDER BY r.created_at DESC
                        LIMIT 20
                        """)
                .query((rs, rowNum) -> new Review(
                        rs.getString("autore"),
                        rs.getString("destinatario"),
                        rs.getInt("voto"),
                        rs.getString("testo")
                )).list(), List.of());
    }

    public List<ChatMessage> chatMessages() {
        if (jdbcClient == null) {
            return List.of();
        }


        return safe(() -> jdbcClient.sql("""
                        SELECT CASE WHEN m.mittente_id = ? THEN 'Tu'
                                    ELSE CONCAT(u.nome, ' ', u.cognome)
                               END AS mittente,
                               m.testo,
                               DATE_FORMAT(m.inviato_at, '%H:%i') AS orario,
                               (m.mittente_id = ?) AS mio
                        FROM messaggi m
                        JOIN utenti u ON u.id = m.mittente_id
                        WHERE m.conversazione_id = (
                            SELECT id
                            FROM conversazioni
                            ORDER BY COALESCE(ultimo_messaggio_at, created_at) DESC
                            LIMIT 1
                        )
                        ORDER BY m.inviato_at ASC
                        """)
                .param(demoGiovaneId)
                .param(demoGiovaneId)
                .query((rs, rowNum) -> new ChatMessage(
                        rs.getString("mittente"),
                        rs.getString("testo"),
                        rs.getString("orario"),
                        rs.getBoolean("mio")
                )).list(), List.of());
    }

    public DashboardStats youngStats() {
        if (jdbcClient == null) {
            return new DashboardStats(0, 0, 0, 0);
        }


        Integer nearbyJobs = safe(() -> jdbcClient.sql("SELECT COUNT(*) FROM v_annunci_attivi")
                .query(Integer.class)
                .single(), 0);

        Integer applications = safe(() -> jdbcClient.sql("SELECT COUNT(*) FROM candidature WHERE giovane_utente_id = ?")
                .param(demoGiovaneId)
                .query(Integer.class)
                .single(), 0);

        Integer activeChats = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM conversazioni
                        WHERE partecipante_1 = ? OR partecipante_2 = ?
                        """)
                .param(demoGiovaneId)
                .param(demoGiovaneId)
                .query(Integer.class)
                .single(), 0);

        Double reputation = safe(() -> jdbcClient.sql("SELECT punteggio_medio FROM profili_giovani WHERE utente_id = ?")
                .param(demoGiovaneId)
                .query(Double.class)
                .optional()
                .orElse(0.0), 0.0);

        return new DashboardStats(nearbyJobs, applications, activeChats, reputation);
    }

    public DashboardStats requesterStats() {
        if (jdbcClient == null) {
            return new DashboardStats(0, 0, 0, 0);
        }


        Integer activeJobs = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM annunci
                        WHERE richiedente_id = ? AND stato = 'attivo' AND visibile = 1
                        """)
                .param(demoRichiedenteId)
                .query(Integer.class)
                .single(), 0);

        Integer receivedApplications = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM candidature c
                        JOIN annunci a ON a.id = c.annuncio_id
                        WHERE a.richiedente_id = ?
                        """)
                .param(demoRichiedenteId)
                .query(Integer.class)
                .single(), 0);

        Integer activeChats = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM conversazioni
                        WHERE partecipante_1 = ? OR partecipante_2 = ?
                        """)
                .param(demoRichiedenteId)
                .param(demoRichiedenteId)
                .query(Integer.class)
                .single(), 0);

        Double reputation = safe(() -> jdbcClient.sql("SELECT punteggio_medio FROM profili_richiedenti WHERE utente_id = ?")
                .param(demoRichiedenteId)
                .query(Double.class)
                .optional()
                .orElse(0.0), 0.0);

        return new DashboardStats(activeJobs, receivedApplications, activeChats, reputation);
    }


    private Job defaultJob() {
        return new Job(0,
                "Nessun annuncio disponibile",
                "Al momento non ci sono annunci attivi nel database.",
                "Altro",
                "Lecco",
                0,
                0,
                LocalDate.now(),
                LocalTime.NOON,
                BigDecimal.ZERO,
                false,
                "HelpingHood");
    }

    private YoungProfile defaultProfile() {
        return new YoungProfile(0,
                "Profilo non disponibile",
                0,
                "Il profilo richiesto non è disponibile.",
                List.of("Disponibile"),
                "Da concordare",
                "Lecco",
                0,
                0);
    }

    private String zone(String city, String note) {
        if (city == null || city.isBlank()) {
            return note == null ? "Zona non specificata" : note;
        }
        if (note == null || note.isBlank()) {
            return city;
        }
        return city + " - " + note;
    }

    private double distanceKm(Double lat, Double lon) {
        if (lat == null || lon == null) {
            return 0.0;
        }
        final double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat - baseLat);
        double dLon = Math.toRadians(lon - baseLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(baseLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadius * c * 10.0) / 10.0;
    }

    private List<String> parseSkills(String rawSkills) {
        if (rawSkills == null || rawSkills.isBlank()) {
            return List.of("Disponibile");
        }
        return Arrays.stream(rawSkills.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .toList();
    }

    private <T> T safe(Supplier<T> operation, T fallback) {
        try {
            return operation.get();
        } catch (RuntimeException ex) {
            return fallback;
        }
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
