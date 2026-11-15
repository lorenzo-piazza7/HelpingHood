package com.example.helpinghood.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class DemoDataService {

    private static final Logger log = LoggerFactory.getLogger(DemoDataService.class);

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
                        SELECT a.id,
                               a.titolo,
                               a.descrizione,
                               COALESCE(c.nome, 'Altro') AS categoria_nome,
                               a.citta,
                               a.luogo_note,
                               a.latitudine,
                               a.longitudine,
                               a.compenso_importo,
                               a.data_inizio,
                               a.ora_inizio,
                               a.durata_ore,
                               a.stato,
                               a.urgente,
                               CONCAT(u.nome, ' ', u.cognome) AS richiedente_nome
                        FROM annunci a
                        LEFT JOIN categorie c ON c.id = a.categoria_id
                        LEFT JOIN utenti u ON u.id = a.richiedente_id
                        WHERE a.visibile = 1
                        ORDER BY a.created_at DESC, a.urgente DESC, a.data_inizio ASC, a.ora_inizio ASC
                        """)
                .query((rs, rowNum) -> {
                    Number latNum = (Number) rs.getObject("latitudine");
                    Number lonNum = (Number) rs.getObject("longitudine");
                    Double lat = latNum != null ? latNum.doubleValue() : null;
                    Double lon = lonNum != null ? lonNum.doubleValue() : null;
                    return new Job(
                            rs.getLong("id"),
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getString("categoria_nome"),
                            zone(rs.getString("citta"), rs.getString("luogo_note")),
                            distanceKm(lat, lon),
                            lat,
                            lon,
                            rs.getBigDecimal("durata_ore") != null ? rs.getBigDecimal("durata_ore").doubleValue() : 0,
                            rs.getObject("data_inizio", LocalDate.class),
                            rs.getObject("ora_inizio", LocalTime.class),
                            rs.getBigDecimal("compenso_importo") != null ? rs.getBigDecimal("compenso_importo") : BigDecimal.ZERO,
                            rs.getString("stato"),
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
                        SELECT a.id,
                               a.titolo,
                               a.descrizione,
                               COALESCE(c.nome, 'Altro') AS categoria_nome,
                               a.citta,
                               a.luogo_note,
                               a.latitudine,
                               a.longitudine,
                               a.compenso_importo,
                               a.data_inizio,
                               a.ora_inizio,
                               a.durata_ore,
                               a.stato,
                               a.urgente,
                               CONCAT(u.nome, ' ', u.cognome) AS richiedente_nome
                        FROM annunci a
                        LEFT JOIN categorie c ON c.id = a.categoria_id
                        LEFT JOIN utenti u ON u.id = a.richiedente_id
                        WHERE a.id = ? AND a.visibile = 1
                        """)
                .param(id)
                .query((rs, rowNum) -> {
                    Number latNum = (Number) rs.getObject("latitudine");
                    Number lonNum = (Number) rs.getObject("longitudine");
                    Double lat = latNum != null ? latNum.doubleValue() : null;
                    Double lon = lonNum != null ? lonNum.doubleValue() : null;
                    return new Job(
                            rs.getLong("id"),
                            rs.getString("titolo"),
                            rs.getString("descrizione"),
                            rs.getString("categoria_nome"),
                            zone(rs.getString("citta"), rs.getString("luogo_note")),
                            distanceKm(lat, lon),
                            lat,
                            lon,
                            rs.getBigDecimal("durata_ore") != null ? rs.getBigDecimal("durata_ore").doubleValue() : 0,
                            rs.getObject("data_inizio", LocalDate.class),
                            rs.getObject("ora_inizio", LocalTime.class),
                            rs.getBigDecimal("compenso_importo") != null ? rs.getBigDecimal("compenso_importo") : BigDecimal.ZERO,
                            rs.getString("stato"),
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
                               r.destinatario_id,
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
                        rs.getString("testo"),
                        rs.getLong("destinatario_id")
                )).list(), List.of());
    }

    public List<Review> reviewsForTarget(long targetUserId) {
        return reviews().stream()
                .filter(review -> review.targetUserId() == targetUserId)
                .toList();
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

    public List<ChatMessage> aiChatForYoungMock() {
        return List.of(
                new ChatMessage("AI HelpingHood",
                        "Ciao! Posso cercarti lavori vicini con impegno breve (1-3 ore). Vuoi priorita oggi o domani?",
                        "09:12",
                        false),
                new ChatMessage("Tu",
                        "Oggi pomeriggio, massimo 2 ore e distanza entro 3 km.",
                        "09:13",
                        true),
                new ChatMessage("AI HelpingHood",
                        "Perfetto. Ho trovato 3 opzioni: 1) supporto compiti (2h, 2.1 km), 2) commissione farmacia (1h, 1.4 km), 3) aiuto PC base (1.5h, 2.8 km).",
                        "09:13",
                        false),
                new ChatMessage("AI HelpingHood",
                        "Consiglio: apri prima il lavoro 'aiuto PC base', e il piu vicino al tuo profilo competenze.",
                        "09:14",
                        false)
        );
    }

    public List<ChatMessage> aiChatForRequesterMock() {
        return List.of(
                new ChatMessage("AI HelpingHood",
                        "Ciao! Posso aiutarti a trovare giovani con esperienza tech (PC, rete, smartphone, coding).",
                        "10:02",
                        false),
                new ChatMessage("Tu",
                        "Cerco un profilo per configurare rete Wi-Fi e sistemare due laptop entro questa settimana.",
                        "10:03",
                        true),
                new ChatMessage("AI HelpingHood",
                        "Ottimo. Ti suggerisco di filtrare per skill: 'Informatica', 'Riparazioni', 'Supporto digitale'.",
                        "10:03",
                        false),
                new ChatMessage("AI HelpingHood",
                        "Nel tuo raggio risultano 4 giovani tech con rating >= 4.0. Vuoi che ti prepari un breve testo annuncio ottimizzato?",
                        "10:04",
                        false)
        );
    }

    public DashboardStats youngStats() {
        return youngStats(demoGiovaneId);
    }

    public DashboardStats youngStats(long giovaneUtenteId) {
        if (jdbcClient == null) {
            return new DashboardStats(0, 0, 0, 0);
        }


        Integer nearbyJobs = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM annunci
                        WHERE visibile = 1
                          AND stato IN ('attivo', 'prenotato', 'in_corso')
                        """)
                .query(Integer.class)
                .single(), 0);

        Integer applications = safe(() -> jdbcClient.sql("SELECT COUNT(*) FROM candidature WHERE giovane_utente_id = ?")
                .param(giovaneUtenteId)
                .query(Integer.class)
                .single(), 0);

        Integer activeChats = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM conversazioni
                        WHERE partecipante_1 = ? OR partecipante_2 = ?
                        """)
                .param(giovaneUtenteId)
                .param(giovaneUtenteId)
                .query(Integer.class)
                .single(), 0);

        Double reputation = safe(() -> jdbcClient.sql("SELECT punteggio_medio FROM profili_giovani WHERE utente_id = ?")
                .param(giovaneUtenteId)
                .query(Double.class)
                .optional()
                .orElse(0.0), 0.0);

        return new DashboardStats(nearbyJobs, applications, activeChats, reputation);
    }

    public DashboardStats requesterStats() {
        return requesterStats(demoRichiedenteId);
    }

    public DashboardStats requesterStats(long richiedenteUtenteId) {
        if (jdbcClient == null) {
            return new DashboardStats(0, 0, 0, 0);
        }


        Integer activeJobs = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM annunci
                        WHERE richiedente_id = ? AND stato = 'attivo' AND visibile = 1
                        """)
                .param(richiedenteUtenteId)
                .query(Integer.class)
                .single(), 0);

        Integer receivedApplications = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM candidature c
                        JOIN annunci a ON a.id = c.annuncio_id
                        WHERE a.richiedente_id = ?
                        """)
                .param(richiedenteUtenteId)
                .query(Integer.class)
                .single(), 0);

        Integer activeChats = safe(() -> jdbcClient.sql("""
                        SELECT COUNT(*)
                        FROM conversazioni
                        WHERE partecipante_1 = ? OR partecipante_2 = ?
                        """)
                .param(richiedenteUtenteId)
                .param(richiedenteUtenteId)
                .query(Integer.class)
                .single(), 0);

        Double reputation = safe(() -> jdbcClient.sql("SELECT punteggio_medio FROM profili_richiedenti WHERE utente_id = ?")
                .param(richiedenteUtenteId)
                .query(Double.class)
                .optional()
                .orElse(0.0), 0.0);

        return new DashboardStats(activeJobs, receivedApplications, activeChats, reputation);
    }

    public List<RequesterYoungProfile> nearbyYoungForRequester(long richiedenteUtenteId, int limit) {
        int boundedLimit = Math.max(1, limit);
        if (jdbcClient == null) {
            return List.of();
        }

        RequesterBase requesterBase = requesterBase(richiedenteUtenteId);
        return safe(() -> jdbcClient.sql("""
                        SELECT vp.id,
                               vp.nome,
                               vp.cognome,
                               vp.anni,
                               COALESCE(vp.bio, 'Profilo in aggiornamento.') AS bio,
                               COALESCE(vp.citta, 'Lecco') AS citta,
                               vp.latitudine,
                               vp.longitudine,
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
                        GROUP BY vp.id, vp.nome, vp.cognome, vp.anni, vp.bio, vp.citta, vp.latitudine, vp.longitudine,
                                 vp.punteggio_medio, vp.n_lavori_completati
                        ORDER BY CASE WHEN LOWER(COALESCE(vp.citta, '')) = LOWER(?) THEN 0 ELSE 1 END,
                                 vp.punteggio_medio DESC,
                                 vp.n_lavori_completati DESC
                        LIMIT ?
                        """)
                .param(requesterBase.city())
                .param(boundedLimit)
                .query((rs, rowNum) -> {
                    Number latNum = (Number) rs.getObject("latitudine");
                    Number lonNum = (Number) rs.getObject("longitudine");
                    Double lat = latNum != null ? latNum.doubleValue() : null;
                    Double lon = lonNum != null ? lonNum.doubleValue() : null;
                    return new RequesterYoungProfile(
                            rs.getLong("id"),
                            rs.getString("nome") + " " + rs.getString("cognome"),
                            rs.getInt("anni"),
                            rs.getString("bio"),
                            parseSkills(rs.getString("skills")),
                            rs.getString("availability"),
                            rs.getString("citta"),
                            rs.getBigDecimal("punteggio_medio") != null ? rs.getBigDecimal("punteggio_medio").doubleValue() : 0,
                            rs.getInt("n_lavori_completati"),
                            distanceKm(requesterBase.lat(), requesterBase.lon(), lat, lon)
                    );
                }).list(), List.of());
    }

    public List<RequesterYoungActivity> activeYoungForRequester(long richiedenteUtenteId, int limit) {
        int boundedLimit = Math.max(1, limit);
        if (jdbcClient == null) {
            return List.of();
        }

        return safe(() -> jdbcClient.sql("""
                        SELECT c.id AS candidatura_id,
                               c.stato,
                               DATE_FORMAT(c.data_candidatura, '%d/%m/%Y') AS data_candidatura,
                               a.id AS annuncio_id,
                               a.titolo,
                               COALESCE(a.citta, 'Lecco') AS citta,
                               COALESCE(a.compenso_importo, 0) AS compenso,
                               g.id AS giovane_id,
                               CONCAT(g.nome, ' ', g.cognome) AS giovane_nome
                        FROM candidature c
                        JOIN annunci a ON a.id = c.annuncio_id
                        JOIN utenti g ON g.id = c.giovane_utente_id
                        WHERE a.richiedente_id = ?
                          AND a.stato IN ('attivo', 'in_corso')
                          AND c.stato IN ('inviata', 'vista', 'accettata')
                        ORDER BY c.data_candidatura DESC
                        LIMIT ?
                        """)
                .param(richiedenteUtenteId)
                .param(boundedLimit)
                .query((rs, rowNum) -> new RequesterYoungActivity(
                        rs.getLong("candidatura_id"),
                        rs.getLong("annuncio_id"),
                        rs.getString("titolo"),
                        rs.getLong("giovane_id"),
                        rs.getString("giovane_nome"),
                        rs.getString("citta"),
                        rs.getBigDecimal("compenso"),
                        rs.getString("stato"),
                        rs.getString("data_candidatura")
                )).list(), List.of());
    }

    public List<RequesterChatPreview> requesterChats(long richiedenteUtenteId, int limit) {
        int boundedLimit = Math.max(1, limit);
        if (jdbcClient == null) {
            return List.of();
        }

        return safe(() -> jdbcClient.sql("""
                        SELECT c.id AS conversazione_id,
                               c.annuncio_id AS annuncio_id,
                               gi.id AS giovane_id,
                               CONCAT(gi.nome, ' ', gi.cognome) AS giovane_nome,
                               COALESCE(a.titolo, 'Conversazione generale') AS annuncio_titolo,
                               COALESCE(m.testo, 'Nessun messaggio recente') AS ultimo_messaggio,
                               DATE_FORMAT(COALESCE(m.inviato_at, c.created_at), '%d/%m %H:%i') AS ultimo_aggiornamento,
                               COALESCE(SUM(CASE WHEN mm.letto = 0 AND mm.mittente_id <> ? THEN 1 ELSE 0 END), 0) AS non_letti
                        FROM conversazioni c
                        JOIN utenti gi ON gi.id = CASE WHEN c.partecipante_1 = ? THEN c.partecipante_2 ELSE c.partecipante_1 END
                        LEFT JOIN annunci a ON a.id = c.annuncio_id
                        LEFT JOIN messaggi m ON m.id = (
                            SELECT m2.id
                            FROM messaggi m2
                            WHERE m2.conversazione_id = c.id
                            ORDER BY m2.inviato_at DESC
                            LIMIT 1
                        )
                        LEFT JOIN messaggi mm ON mm.conversazione_id = c.id
                        WHERE c.partecipante_1 = ? OR c.partecipante_2 = ?
                        GROUP BY c.id, gi.id, gi.nome, gi.cognome, a.titolo, m.testo, m.inviato_at, c.created_at
                        ORDER BY COALESCE(m.inviato_at, c.created_at) DESC
                        LIMIT ?
                        """)
                .param(richiedenteUtenteId)
                .param(richiedenteUtenteId)
                .param(richiedenteUtenteId)
                .param(richiedenteUtenteId)
                .param(boundedLimit)
                .query((rs, rowNum) -> new RequesterChatPreview(
                        rs.getLong("conversazione_id"),
                        rs.getLong("annuncio_id"),
                        rs.getLong("giovane_id"),
                        rs.getString("giovane_nome"),
                        rs.getString("annuncio_titolo"),
                        rs.getString("ultimo_messaggio"),
                        rs.getString("ultimo_aggiornamento"),
                        rs.getInt("non_letti")
                )).list(), List.of());
    }

    public List<NearbyYoungJob> nearbyJobsForYoung(long giovaneUtenteId, int limit) {
        int boundedLimit = Math.max(1, limit);

        List<NearbyYoungJob> fallback = jobs().stream()
                .sorted(Comparator.comparingDouble(Job::distanceKm)
                        .thenComparing((job) -> job.day() != null ? job.day() : LocalDate.MAX)
                        .thenComparing((job) -> job.time() != null ? job.time() : LocalTime.MAX))
                .limit(boundedLimit)
                .map(job -> new NearbyYoungJob(
                        job.id(),
                        job.title(),
                        job.category(),
                        job.zone(),
                        job.distanceKm(),
                        job.day(),
                        job.time(),
                        job.urgent(),
                        "attivo"
                ))
                .toList();

        if (jdbcClient == null) {
            return fallback;
        }

        return safe(() -> jdbcClient.sql("""
                        SELECT a.id,
                               a.titolo,
                               c.nome AS categoria_nome,
                               COALESCE(a.citta, 'Lecco') AS citta,
                               a.luogo_note,
                               a.latitudine,
                               a.longitudine,
                               a.data_inizio,
                               a.ora_inizio,
                               a.urgente,
                               a.stato
                        FROM annunci a
                        LEFT JOIN categorie c ON c.id = a.categoria_id
                        WHERE a.visibile = 1
                          AND a.stato IN ('attivo', 'prenotato', 'in_corso', 'completato')
                        ORDER BY FIELD(a.stato, 'attivo', 'prenotato', 'in_corso', 'completato'),
                                 a.data_inizio ASC,
                                 a.ora_inizio ASC
                        LIMIT ?
                        """)
                .param(boundedLimit)
                .query((rs, rowNum) -> {
                    Number latNum = (Number) rs.getObject("latitudine");
                    Number lonNum = (Number) rs.getObject("longitudine");
                    Double lat = latNum != null ? latNum.doubleValue() : null;
                    Double lon = lonNum != null ? lonNum.doubleValue() : null;
                    return new NearbyYoungJob(
                            rs.getLong("id"),
                            rs.getString("titolo"),
                            rs.getString("categoria_nome") != null ? rs.getString("categoria_nome") : "Altro",
                            zone(rs.getString("citta"), rs.getString("luogo_note")),
                            distanceKm(lat, lon),
                            rs.getObject("data_inizio", LocalDate.class),
                            rs.getObject("ora_inizio", LocalTime.class),
                            rs.getBoolean("urgente"),
                            rs.getString("stato")
                    );
                })
                .list(), fallback);
    }

    public List<YoungApplication> youngApplications(long giovaneUtenteId) {
        if (jdbcClient == null) {
            return List.of();
        }

        return safe(() -> jdbcClient.sql("""
                        SELECT c.id,
                               a.id AS annuncio_id,
                               a.titolo,
                               COALESCE(a.citta, 'Lecco') AS citta,
                               c.stato,
                               DATE_FORMAT(c.data_candidatura, '%d/%m/%Y') AS data_invio,
                               COALESCE(a.compenso_importo, 0) AS compenso,
                               CONCAT(r.nome, ' ', r.cognome) AS richiedente
                        FROM candidature c
                        JOIN annunci a ON a.id = c.annuncio_id
                        JOIN utenti r ON r.id = a.richiedente_id
                        WHERE c.giovane_utente_id = ?
                        ORDER BY c.data_candidatura DESC
                        LIMIT 8
                        """)
                .param(giovaneUtenteId)
                .query((rs, rowNum) -> new YoungApplication(
                        rs.getLong("id"),
                        rs.getLong("annuncio_id"),
                        rs.getString("titolo"),
                        rs.getString("citta"),
                        rs.getString("stato"),
                        rs.getString("data_invio"),
                        rs.getBigDecimal("compenso"),
                        rs.getString("richiedente")
                ))
                .list(), List.of());
    }

    public List<YoungActiveChat> activeChatsForYoung(long giovaneUtenteId) {
        if (jdbcClient == null) {
            return List.of();
        }

        return safe(() -> jdbcClient.sql("""
                        SELECT c.id,
                               CONCAT(u.nome, ' ', u.cognome) AS controparte,
                               COALESCE(a.titolo, 'Conversazione generale') AS annuncio_titolo,
                               COALESCE(m.testo, 'Nessun messaggio recente') AS ultimo_messaggio,
                               DATE_FORMAT(COALESCE(m.inviato_at, c.created_at), '%d/%m %H:%i') AS ultimo_aggiornamento,
                               COALESCE(SUM(CASE WHEN mm.letto = 0 AND mm.mittente_id <> ? THEN 1 ELSE 0 END), 0) AS non_letti
                        FROM conversazioni c
                        JOIN utenti u ON u.id = CASE WHEN c.partecipante_1 = ? THEN c.partecipante_2 ELSE c.partecipante_1 END
                        LEFT JOIN annunci a ON a.id = c.annuncio_id
                        LEFT JOIN messaggi m ON m.id = (
                            SELECT m2.id
                            FROM messaggi m2
                            WHERE m2.conversazione_id = c.id
                            ORDER BY m2.inviato_at DESC
                            LIMIT 1
                        )
                        LEFT JOIN messaggi mm ON mm.conversazione_id = c.id
                        WHERE c.partecipante_1 = ? OR c.partecipante_2 = ?
                        GROUP BY c.id, u.nome, u.cognome, a.titolo, m.testo, m.inviato_at, c.created_at
                        ORDER BY COALESCE(m.inviato_at, c.created_at) DESC
                        LIMIT 8
                        """)
                .param(giovaneUtenteId)
                .param(giovaneUtenteId)
                .param(giovaneUtenteId)
                .param(giovaneUtenteId)
                .query((rs, rowNum) -> new YoungActiveChat(
                        rs.getLong("id"),
                        rs.getString("controparte"),
                        rs.getString("annuncio_titolo"),
                        rs.getString("ultimo_messaggio"),
                        rs.getString("ultimo_aggiornamento"),
                        rs.getInt("non_letti")
                ))
                .list(), List.of());
    }

    // Mockup: ogni lavoro aderito ha una chat dedicata separata dalla Chat AI.
    public List<WorkChatPreview> youngWorkChatsMock(long giovaneUtenteId) {
        return youngApplications(giovaneUtenteId).stream()
                .filter(application -> {
                    String status = application.status() == null ? "" : application.status().toLowerCase();
                    return !"rifiutata".equals(status) && !"ritirata".equals(status);
                })
                .limit(8)
                .map(application -> new WorkChatPreview(
                        application.jobId(),
                        application.jobTitle(),
                        application.requesterName(),
                        application.status(),
                        buildMockLastMessage(application.status()),
                        application.sentAt(),
                        unreadCountForStatus(application.status())
                ))
                .toList();
    }

    public List<ChatMessage> jobChatMessagesMock(long giovaneUtenteId, long jobId) {
        return youngApplications(giovaneUtenteId).stream()
                .filter(application -> application.jobId() == jobId)
                .findFirst()
                .map(application -> List.of(
                        new ChatMessage(application.requesterName(),
                                "Ciao! Grazie per aver aderito a '" + application.jobTitle() + "'. Organizziamoci qui.",
                                "09:40",
                                false),
                        new ChatMessage("Tu",
                                "Perfetto, confermo disponibilita. Possiamo sentirci poco prima dell'orario previsto.",
                                "09:44",
                                true),
                        new ChatMessage(application.requesterName(),
                                "Ottimo, ci aggiorniamo qui per ogni dettaglio operativo.",
                                "09:47",
                                false)
                ))
                .orElseGet(() -> List.of(
                        new ChatMessage("Sistema", "Questa chat lavoro non e disponibile per il tuo account.", "--:--", false)
                ));
    }

    private String buildMockLastMessage(String status) {
        if (status == null) {
            return "Nuova conversazione lavoro avviata.";
        }
        return switch (status.toLowerCase()) {
            case "accettata" -> "Candidatura confermata: pianifica i dettagli operativi in chat.";
            case "vista" -> "Richiesta letta: resta disponibile per un riscontro.";
            case "inviata" -> "Candidatura inviata: puoi usare questa chat appena il richiedente risponde.";
            default -> "Conversazione lavoro aggiornata.";
        };
    }

    private int unreadCountForStatus(String status) {
        if (status == null) {
            return 0;
        }
        return "inviata".equalsIgnoreCase(status) ? 1 : 0;
    }


    private Job defaultJob() {
        return new Job(0,
                "Nessun annuncio disponibile",
                "Al momento non ci sono annunci attivi nel database.",
                "Altro",
                "Lecco",
                0,
                baseLat,
                baseLon,
                0,
                LocalDate.now(),
                LocalTime.NOON,
                BigDecimal.ZERO,
                "attivo",
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
        return distanceKm(baseLat, baseLon, lat, lon);
    }

    private double distanceKm(Double fromLat, Double fromLon, Double lat, Double lon) {
        if (fromLat == null || fromLon == null || lat == null || lon == null) {
            return 0.0;
        }
        final double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat - fromLat);
        double dLon = Math.toRadians(lon - fromLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(fromLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadius * c * 10.0) / 10.0;
    }

    private RequesterBase requesterBase(long richiedenteUtenteId) {
        if (jdbcClient == null) {
            return new RequesterBase("", null, null);
        }

        Map<String, Object> requester = safe(() -> jdbcClient.sql("""
                        SELECT COALESCE(citta, '') AS citta,
                               latitudine,
                               longitudine
                        FROM utenti
                        WHERE id = ?
                        """)
                .param(richiedenteUtenteId)
                .query()
                .singleRow(), Map.of());

        Number latNum = (Number) requester.get("latitudine");
        Number lonNum = (Number) requester.get("longitudine");
        return new RequesterBase(
                String.valueOf(requester.getOrDefault("citta", "")),
                latNum != null ? latNum.doubleValue() : null,
                lonNum != null ? lonNum.doubleValue() : null
        );
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
            log.debug("Fallback demo data value used due to runtime exception", ex);
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
                      Double latitude,
                      Double longitude,
                      double durationHours,
                      LocalDate day,
                      LocalTime time,
                      BigDecimal hourlyCompensation,
                      String status,
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

    public record Review(String author, String target, int rating, String text, long targetUserId) {
    }

    public record ChatMessage(String sender, String text, String time, boolean mine) {
    }

    public record DashboardStats(int nearbyJobs, int applications, int activeChats, double reputation) {
    }

    public record YoungApplication(long id,
                                   long jobId,
                                   String jobTitle,
                                   String city,
                                   String status,
                                   String sentAt,
                                   BigDecimal compensation,
                                   String requesterName) {
    }

    public record YoungActiveChat(long id,
                                  String counterpart,
                                  String jobTitle,
                                  String lastMessage,
                                  String lastUpdated,
                                  int unreadCount) {
    }

    public record WorkChatPreview(long jobId,
                                  String jobTitle,
                                  String counterpart,
                                  String applicationStatus,
                                  String lastMessage,
                                  String lastUpdated,
                                  int unreadCount) {
    }

    public record NearbyYoungJob(long id,
                                 String title,
                                 String category,
                                 String zone,
                                 double distanceKm,
                                 LocalDate day,
                                 LocalTime time,
                                 boolean urgent,
                                 String status) {
    }

    public record RequesterYoungProfile(long id,
                                        String name,
                                        int age,
                                        String bio,
                                        List<String> skills,
                                        String availability,
                                        String zone,
                                        double rating,
                                        int jobsDone,
                                        double distanceKm) {
    }

    public record RequesterYoungActivity(long applicationId,
                                         long jobId,
                                         String jobTitle,
                                         long youngId,
                                         String youngName,
                                         String city,
                                         BigDecimal compensation,
                                         String status,
                                         String appliedAt) {
    }

    public record RequesterChatPreview(long conversationId,
                                       long jobId,
                                       long youngId,
                                       String youngName,
                                       String jobTitle,
                                       String lastMessage,
                                       String lastUpdated,
                                       int unreadCount) {
    }

    private record RequesterBase(String city, Double lat, Double lon) {
    }
}
