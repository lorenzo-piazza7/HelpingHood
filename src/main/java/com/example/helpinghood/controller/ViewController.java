package com.example.helpinghood.controller;

import com.example.helpinghood.model.Annuncio;
import com.example.helpinghood.model.Candidatura;
import com.example.helpinghood.model.Categoria;
import com.example.helpinghood.model.Conversazione;
import com.example.helpinghood.model.Lavoro;
import com.example.helpinghood.model.Messaggio;
import com.example.helpinghood.model.ProfiloGiovane;
import com.example.helpinghood.model.Recensione;
import com.example.helpinghood.model.ProfiloRichiedente;
import com.example.helpinghood.model.Utente;
import com.example.helpinghood.repository.ConversazioneRepository;
import com.example.helpinghood.repository.LavoroRepository;
import com.example.helpinghood.repository.MessaggioRepository;
import com.example.helpinghood.service.AnnuncioService;
import com.example.helpinghood.service.CandidaturaService;
import com.example.helpinghood.service.CategoriaService;
import com.example.helpinghood.service.DemoDataService;
import com.example.helpinghood.service.ProfiloGiovaneService;
import com.example.helpinghood.service.ProfiloRichiedenteService;
import com.example.helpinghood.service.RecensioneService;
import com.example.helpinghood.service.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    private final DemoDataService demoDataService;
    private final UtenteService utenteService;
    private final ProfiloGiovaneService profiloGiovaneService;
    private final AnnuncioService annuncioService;
    private final CandidaturaService candidaturaService;
    private final CategoriaService categoriaService;
    private final ConversazioneRepository conversazioneRepository;
    private final MessaggioRepository messaggioRepository;
    private final RecensioneService recensioneService;
    private final LavoroRepository lavoroRepository;
    private final ProfiloRichiedenteService profiloRichiedenteService;

    public ViewController(DemoDataService demoDataService,
                          UtenteService utenteService,
                          ProfiloGiovaneService profiloGiovaneService,
                          AnnuncioService annuncioService,
                          CandidaturaService candidaturaService,
                          CategoriaService categoriaService,
                          ConversazioneRepository conversazioneRepository,
                          MessaggioRepository messaggioRepository,
                          RecensioneService recensioneService,
                          LavoroRepository lavoroRepository,
                          ProfiloRichiedenteService profiloRichiedenteService) {
        this.demoDataService = demoDataService;
        this.utenteService = utenteService;
        this.profiloGiovaneService = profiloGiovaneService;
        this.annuncioService = annuncioService;
        this.candidaturaService = candidaturaService;
        this.categoriaService = categoriaService;
        this.conversazioneRepository = conversazioneRepository;
        this.messaggioRepository = messaggioRepository;
        this.recensioneService = recensioneService;
        this.lavoroRepository = lavoroRepository;
        this.profiloRichiedenteService = profiloRichiedenteService;
    }

    @GetMapping("/home")
    public String landing(Model model, Authentication authentication) {
        model.addAttribute("categories", demoDataService.categories());
        Utente utente = getAuthenticatedUser(authentication);
        if (utente != null && "richiedente".equalsIgnoreCase(utente.getRuolo())) {
            model.addAttribute("createdJobs", annuncioService.findByRichiedente(utente.getId()));
        } else if (utente != null && "giovane".equalsIgnoreCase(utente.getRuolo())) {
            model.addAttribute("nearbyJobs", demoDataService.jobsForYoung(utente.getId()).stream().limit(3).toList());
        } else {
            model.addAttribute("nearbyJobs", demoDataService.jobs().stream().limit(3).toList());
        }
        return "pages/landing";
    }

    @GetMapping("/dashboard/giovane")
    public String youngDashboard(Model model, Authentication authentication) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        model.addAttribute("stats", demoDataService.youngStats(utente.getId()));
        model.addAttribute("nearbyJobs", demoDataService.nearbyJobsForYoung(utente.getId(), 6));
        model.addAttribute("sentApplications", demoDataService.youngApplications(utente.getId()));
        model.addAttribute("activeChats", demoDataService.youngWorkChatsMock(utente.getId()));

        DemoDataService.YoungProfile baseProfile = demoDataService.youngProfiles().stream().findFirst().orElse(
                new DemoDataService.YoungProfile(0, "Profilo non disponibile", 0, "Nessun profilo disponibile.",
                        java.util.List.of("Disponibile"), "Da concordare", "Lecco", 0, 0)
        );

        String displayName = (utente.getNome() + " " + utente.getCognome()).trim();

        model.addAttribute("profile", new DemoDataService.YoungProfile(
                baseProfile.id(),
                displayName,
                baseProfile.age(),
                baseProfile.bio(),
                baseProfile.skills(),
                baseProfile.availability(),
                baseProfile.zone(),
                baseProfile.rating(),
                baseProfile.jobsDone()
        ));
        return "pages/dashboard-young";
    }

    @GetMapping("/dashboard/richiedente")
    public String requesterDashboard(Model model, Authentication authentication) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        model.addAttribute("displayName", (utente.getNome() + " " + utente.getCognome()).trim());
        model.addAttribute("stats", demoDataService.requesterStats(utente.getId()));
        model.addAttribute("nearbyYoungs", demoDataService.nearbyYoungForRequester(utente.getId(), 6));
        model.addAttribute("activeYoungs", demoDataService.activeYoungForRequester(utente.getId(), 6));
        model.addAttribute("youngChats", demoDataService.requesterChats(utente.getId(), 6));
        model.addAttribute("createdJobs", annuncioService.findByRichiedente(utente.getId()));
        return "pages/dashboard-requester";
    }

    @GetMapping("/giovani")
    public String youngsDirectory(Model model, Authentication authentication) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }

        model.addAttribute("youngs", demoDataService.youngProfiles());
        model.addAttribute("categories", categoriaService.findAll());
        return "pages/youngs";
    }

    @GetMapping("/lavori")
    public String jobs(Model model, Authentication authentication) {
        Utente utente = getAuthenticatedUser(authentication);
        boolean isGiovane = utente != null && "giovane".equalsIgnoreCase(utente.getRuolo());

        Set<Long> myRequestedJobIds = Set.of();
        if (isGiovane) {
            myRequestedJobIds = candidaturaService.findByGiovane(utente.getId()).stream()
                    .filter(candidatura -> candidatura.getStato() == null
                            || (!"rifiutata".equalsIgnoreCase(candidatura.getStato())
                            && !"ritirata".equalsIgnoreCase(candidatura.getStato())))
                    .map(Candidatura::getAnnuncioId)
                    .collect(Collectors.toSet());
        }

        model.addAttribute("jobs", isGiovane
                ? demoDataService.jobsForYoung(utente.getId())
                : demoDataService.jobs());
        model.addAttribute("categories", demoDataService.categories());
        model.addAttribute("isGiovaneViewer", isGiovane);
        model.addAttribute("myRequestedJobIds", myRequestedJobIds);
        return "pages/jobs";
    }

    @GetMapping("/lavori/{id}")
    public String jobDetail(@PathVariable long id, Model model, Authentication authentication) {
        Optional<Annuncio> annuncioOptional = annuncioService.findById(id);
        if (annuncioOptional.isEmpty()) {
            return "redirect:/lavori";
        }

        Annuncio annuncio = annuncioOptional.get();
        Utente utente = getAuthenticatedUser(authentication);
        boolean isGiovane = utente != null && "giovane".equalsIgnoreCase(utente.getRuolo());

        String categoriaNome = categoriaService.findAll().stream()
                .filter(categoria -> categoria.getId().equals(annuncio.getCategoriaId()))
                .map(Categoria::getNome)
                .findFirst()
                .orElse("Altro");

        String richiedenteNome = utenteService.findById(annuncio.getRichiedenteId())
                .map(user -> (user.getNome() + " " + user.getCognome()).trim())
                .orElse("Richiedente");

        boolean alreadyRequested = false;
        if (isGiovane) {
            alreadyRequested = candidaturaService.findByAnnuncioAndGiovane(annuncio.getId(), utente.getId())
                    .map(this::isActiveApplication)
                    .orElse(false);
        }

        model.addAttribute("annuncio", annuncio);
        model.addAttribute("categoriaNome", categoriaNome);
        model.addAttribute("richiedenteNome", richiedenteNome);
        model.addAttribute("isGiovaneViewer", isGiovane);
        model.addAttribute("jobAlreadyRequested", alreadyRequested);
        model.addAttribute("mapLatitude", annuncio.getLatitudine());
        model.addAttribute("mapLongitude", annuncio.getLongitudine());
        return "pages/job-detail";
    }

    @PostMapping("/lavori/{id}/richiedi")
    @Transactional
    public String requestJob(@PathVariable long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        Annuncio annuncio = annuncioService.findById(id).orElse(null);
        if (annuncio == null || !annuncio.isVisibile()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Annuncio non disponibile.");
            return "redirect:/lavori";
        }

        if (!"attivo".equalsIgnoreCase(annuncio.getStato())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Questo lavoro e gia preso.");
            return "redirect:/lavori/" + id;
        }

        if (candidaturaService.findByAnnuncioAndGiovane(id, utente.getId())
                .filter(this::isActiveApplication)
                .isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Hai gia richiesto questo lavoro.");
            return "redirect:/lavori/" + id;
        }

        // In questo mockup la richiesta viene subito considerata accettata,
        // quindi il lavoro passa direttamente a "in_corso".
        Candidatura candidatura = new Candidatura();
        candidatura.setAnnuncioId(id);
        candidatura.setGiovaneUtenteId(utente.getId());
        candidatura.setMessaggio("Richiesta lavoro inviata dal giovane (mockup).\nContattami in chat per i dettagli.");
        candidatura.setStato("accettata");
        candidaturaService.save(candidatura);

        annuncio.setStato("in_corso");
        annuncio.setNCandidature((annuncio.getNCandidature() == null ? 0 : annuncio.getNCandidature()) + 1);
        annuncioService.save(annuncio);

        // Creiamo la chat se assente, altrimenti riusiamo quella gia esistente.
        Optional<Conversazione> existingConversation = conversazioneRepository
                .findByAnnuncioIdAndParticipants(id, utente.getId(), annuncio.getRichiedenteId());
        Conversazione conversazione = existingConversation.orElseGet(() -> {
            Conversazione nuova = new Conversazione();
            nuova.setAnnuncioId(id);
            nuova.setPartecipante1(utente.getId());
            nuova.setPartecipante2(annuncio.getRichiedenteId());
            return conversazioneRepository.save(nuova);
        });

        Messaggio messaggio = new Messaggio();
        messaggio.setConversazioneId(conversazione.getId());
        messaggio.setMittenteId(utente.getId());
        messaggio.setTesto("Ciao! Ho richiesto il lavoro '" + annuncio.getTitolo() + "'. Sono disponibile a partire da oggi.");
        messaggio.setLetto(false);
        messaggioRepository.save(messaggio);

        conversazione.setUltimoMessaggioAt(LocalDateTime.now());
        conversazioneRepository.save(conversazione);

        redirectAttributes.addFlashAttribute("successMessage", "Richiesta inviata: lavoro segnato come preso e chat attivata.");
        return "redirect:/lavori/" + id;
    }

    @PostMapping("/lavori/{id}/annulla")
    @Transactional
    public String cancelJobRequest(@PathVariable long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        Optional<Candidatura> candidaturaOptional = candidaturaService.findByAnnuncioAndGiovane(id, utente.getId())
                .filter(this::isActiveApplication);
        if (candidaturaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nessuna richiesta attiva da annullare.");
            return "redirect:/lavori";
        }

        Annuncio annuncio = annuncioService.findById(id).orElse(null);
        if (annuncio != null) {
            // L'annuncio torna disponibile e si allinea il contatore candidature.
            annuncio.setStato("attivo");
            int count = annuncio.getNCandidature() == null ? 0 : annuncio.getNCandidature();
            annuncio.setNCandidature(Math.max(0, count - 1));
            annuncioService.save(annuncio);
        }

        Optional<Conversazione> conversation = conversazioneRepository.findByAnnuncioIdAndParticipants(
                id,
                utente.getId(),
                annuncio != null ? annuncio.getRichiedenteId() : utente.getId()
        );
        conversation.ifPresent(c -> {
            // Nel modello corrente, annullare la richiesta chiude anche la chat legata a quel lavoro.
            messaggioRepository.deleteByConversazioneId(c.getId());
            conversazioneRepository.delete(c);
        });

        candidaturaService.delete(candidaturaOptional.get());
        redirectAttributes.addFlashAttribute("successMessage", "Richiesta annullata: annuncio riattivato.");
        return "redirect:/lavori";
    }

    @GetMapping("/profili/giovani/{id}")
    public String youngProfile(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        if (demoDataService.youngProfiles().stream().noneMatch(profile -> profile.id() == id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Profilo giovane non trovato.");
            return "redirect:/giovani";
        }
        model.addAttribute("profile", demoDataService.youngProfileById(id));
        model.addAttribute("reviews", loadReviewCardsForTarget(id));
        return "pages/young-profile-public";
    }

    @GetMapping("/dashboard/richiedente/lavori")
    public String requesterJobs(Authentication authentication, Model model) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        model.addAttribute("createdJobs", annuncioService.findByRichiedente(utente.getId()));
        return "pages/requester-jobs";
    }

    @GetMapping("/dashboard/richiedente/giovani/{youngId}/dettagli")
    public String requesterYoungDetails(@PathVariable long youngId,
                                        @RequestParam(required = false) Long jobId,
                                        Authentication authentication,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        List<DemoDataService.RequesterYoungActivity> activeJobs = demoDataService.activeYoungForRequester(utente.getId(), 64).stream()
                .filter(active -> active.youngId() == youngId)
                .toList();

        if (activeJobs.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Questo giovane non e attivo nei tuoi lavori.");
            return "redirect:/dashboard/richiedente";
        }

        Long selectedJobId = jobId;
        final Long requestedJobId = selectedJobId;
        if (selectedJobId == null || activeJobs.stream().noneMatch(active -> active.jobId() == requestedJobId)) {
            selectedJobId = activeJobs.get(0).jobId();
        }

        model.addAttribute("youngProfile", demoDataService.youngProfileById(youngId));
        model.addAttribute("youngReviews", loadReviewCardsForTarget(youngId));
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("selectedJobId", selectedJobId);
        model.addAttribute("youngId", youngId);
        return "pages/requester-young-detail";
    }

    @PostMapping("/dashboard/richiedente/giovani/{youngId}/commento")
    @Transactional
    public String addYoungReview(@PathVariable long youngId,
                                 @RequestParam long jobId,
                                 @RequestParam int rating,
                                 @RequestParam(required = false) String comment,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        Annuncio annuncio = annuncioService.findByIdAndRichiedente(jobId, utente.getId()).orElse(null);
        if (annuncio == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lavoro non trovato.");
            return "redirect:/dashboard/richiedente";
        }

        Candidatura candidatura = candidaturaService.findByAnnuncioAndGiovane(jobId, youngId).orElse(null);
        if (candidatura == null || !isActiveApplication(candidatura)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nessun giovane attivo trovato su questo lavoro.");
            return "redirect:/dashboard/richiedente/giovani/" + youngId + "/dettagli?jobId=" + jobId;
        }

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Il rating deve essere compreso tra 1 e 5.");
            return "redirect:/dashboard/richiedente/giovani/" + youngId + "/dettagli?jobId=" + jobId;
        }

        // La recensione e sempre agganciata a un record "lavoro" consistente.
        Lavoro lavoro = ensureLavoroRecord(candidatura, annuncio, utente.getId(), youngId);
        Recensione recensione = recensioneService.findByLavoroAndAutore(lavoro.getId(), utente.getId())
                .orElseGet(Recensione::new);
        recensione.setLavoroId(lavoro.getId());
        recensione.setAutoreId(utente.getId());
        recensione.setDestinatarioId(youngId);
        recensione.setVoto(rating);
        recensione.setTesto(isBlank(comment) ? "Feedback lasciato dal richiedente." : comment.trim());
        recensione.setVisibile(true);
        recensioneService.save(recensione);

        recalculateYoungReputation(youngId);
        redirectAttributes.addFlashAttribute("successMessage", "Commento e rating salvati.");
        return "redirect:/dashboard/richiedente/giovani/" + youngId + "/dettagli?jobId=" + jobId;
    }

    @PostMapping("/dashboard/richiedente/lavori/{jobId}/completa")
    @Transactional
    public String completeWork(@PathVariable long jobId,
                               @RequestParam long youngId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        Annuncio annuncio = annuncioService.findByIdAndRichiedente(jobId, utente.getId()).orElse(null);
        if (annuncio == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lavoro non trovato.");
            return "redirect:/dashboard/richiedente";
        }

        Candidatura candidatura = candidaturaService.findByAnnuncioAndGiovane(jobId, youngId).orElse(null);
        if (candidatura == null || !isActiveApplication(candidatura)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Il giovane non e piu attivo su questo lavoro.");
            return "redirect:/dashboard/richiedente";
        }

        Lavoro lavoro = ensureLavoroRecord(candidatura, annuncio, utente.getId(), youngId);
        if ("completato".equalsIgnoreCase(lavoro.getStato())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Questo lavoro è già stato completato.");
            return "redirect:/dashboard/richiedente";
        }
        // Chiusura lavoro: aggiorniamo timeline e stato definitivo.
        lavoro.setStato("completato");
        if (lavoro.getDataInizioEff() == null) {
            lavoro.setDataInizioEff(LocalDateTime.now());
        }
        lavoro.setDataFineEff(LocalDateTime.now());
        lavoroRepository.save(lavoro);

        annuncio.setStato("completato");
        annuncio.setVisibile(false);
        int candidatureCount = annuncio.getNCandidature() == null ? 0 : annuncio.getNCandidature();
        annuncio.setNCandidature(Math.max(0, candidatureCount - 1));
        annuncioService.save(annuncio);

        // Conserviamo la candidatura: lavori.candidatura_id ha una FK ON DELETE CASCADE
        // e cancellarla eliminerebbe anche lo storico del lavoro.
        candidatura.setDataRisposta(LocalDateTime.now());
        candidaturaService.save(candidatura);

        conversazioneRepository.findByAnnuncioIdAndParticipants(jobId, youngId, utente.getId()).ifPresent(conversazione -> {
            messaggioRepository.deleteByConversazioneId(conversazione.getId());
            conversazioneRepository.delete(conversazione);
        });

        profiloGiovaneService.findByUtenteId(youngId).ifPresent(profilo -> {
            int completedJobs = profilo.getNLavoriCompletati() == null ? 0 : profilo.getNLavoriCompletati();
            profilo.setNLavoriCompletati(completedJobs + 1);
            profiloGiovaneService.save(profilo);
        });

        recalculateYoungReputation(youngId);
        redirectAttributes.addFlashAttribute("successMessage", "Lavoro completato correttamente.");
        return "redirect:/dashboard/richiedente";
    }

    @GetMapping("/profilo/richiedente")
    public String requesterPrivateProfile(Authentication authentication, Model model) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        ProfiloRichiedente profilo = profiloRichiedenteService.findByUtenteId(utente.getId())
                .orElseGet(() -> {
                    ProfiloRichiedente nuovoProfilo = new ProfiloRichiedente();
                    nuovoProfilo.setUtenteId(utente.getId());
                    nuovoProfilo.setTipo("privato");
                    return profiloRichiedenteService.save(nuovoProfilo);
                });

        model.addAttribute("utente", utente);
        model.addAttribute("profilo", profilo);
        populateBirthDateRange(model);
        return "pages/requester-profile";
    }

    @PostMapping("/profilo/richiedente")
    @Transactional
    public String updateRequesterPrivateProfile(@RequestParam String nome,
                                                @RequestParam String cognome,
                                                @RequestParam String email,
                                                @RequestParam String telefono,
                                                @RequestParam(required = false) String dataNascita,
                                                @RequestParam String citta,
                                                @RequestParam(required = false) String cap,
                                                @RequestParam String indirizzo,
                                                @RequestParam(required = false) String bio,
                                                @RequestParam String tipo,
                                                @RequestParam(required = false) String nomeOrg,
                                                @RequestParam(required = false) String sitoWeb,
                                                Authentication authentication,
                                                Model model,
                                                RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        ProfiloRichiedente profilo = profiloRichiedenteService.findByUtenteId(utente.getId())
                .orElseGet(() -> {
                    ProfiloRichiedente nuovoProfilo = new ProfiloRichiedente();
                    nuovoProfilo.setUtenteId(utente.getId());
                    return nuovoProfilo;
                });

        if (isBlank(nome) || isBlank(cognome) || isBlank(email) || isBlank(telefono)
                || isBlank(citta) || isBlank(indirizzo) || isBlank(tipo)) {
            model.addAttribute("errorMessage", "Compila tutti i campi obbligatori.");
            bindRequesterProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/requester-profile";
        }

        String normalizedType = tipo.trim().toLowerCase();
        if (!Set.of("privato", "famiglia", "anziano", "ente_pubblico", "associazione", "piccola_impresa")
                .contains(normalizedType)) {
            model.addAttribute("errorMessage", "Tipo di profilo non valido.");
            bindRequesterProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/requester-profile";
        }

        LocalDate birthDate = parseDate(dataNascita);
        if (!isBlank(dataNascita) && birthDate == null) {
            model.addAttribute("errorMessage", "Data di nascita non valida.");
            bindRequesterProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/requester-profile";
        }
        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            model.addAttribute("errorMessage", "La data di nascita non puo essere futura.");
            bindRequesterProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/requester-profile";
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.equalsIgnoreCase(utente.getEmail()) && utenteService.existsByEmail(normalizedEmail)) {
            model.addAttribute("errorMessage", "Email gia in uso.");
            bindRequesterProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/requester-profile";
        }

        utente.setNome(nome.trim());
        utente.setCognome(cognome.trim());
        utente.setEmail(normalizedEmail);
        utente.setTelefono(telefono.trim());
        utente.setDataNascita(birthDate);
        utente.setCitta(citta.trim());
        utente.setCap(isBlank(cap) ? null : cap.trim());
        utente.setIndirizzo(indirizzo.trim());
        utente.setBio(isBlank(bio) ? null : bio.trim());

        profilo.setUtenteId(utente.getId());
        profilo.setTipo(normalizedType);
        profilo.setNomeOrg(isBlank(nomeOrg) ? null : nomeOrg.trim());
        profilo.setSitoWeb(isBlank(sitoWeb) ? null : sitoWeb.trim());

        utenteService.save(utente);
        profiloRichiedenteService.save(profilo);
        refreshAuthenticationName(authentication, normalizedEmail);

        redirectAttributes.addFlashAttribute("successMessage", "Profilo richiedente aggiornato con successo.");
        return "redirect:/profilo/richiedente";
    }

    @GetMapping("/profilo/giovane")
    public String youngPrivateProfile(Authentication authentication, Model model) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        ProfiloGiovane profilo = profiloGiovaneService.findByUtenteId(utente.getId())
                .orElseGet(() -> {
                    ProfiloGiovane nuovoProfilo = new ProfiloGiovane();
                    nuovoProfilo.setUtenteId(utente.getId());
                    nuovoProfilo.setDisponibile(true);
                    nuovoProfilo.setRaggioKm(5);
                    nuovoProfilo.setOreSettimanaMax(10);
                    return profiloGiovaneService.save(nuovoProfilo);
                });

        model.addAttribute("utente", utente);
        model.addAttribute("profilo", profilo);
        populateBirthDateRange(model);
        return "pages/young-profile";
    }

    @PostMapping("/profilo/giovane")
    @Transactional
    public String updateYoungPrivateProfile(@RequestParam String nome,
                                            @RequestParam String cognome,
                                            @RequestParam String email,
                                            @RequestParam String telefono,
                                            @RequestParam String dataNascita,
                                            @RequestParam String citta,
                                            @RequestParam(required = false) String cap,
                                            @RequestParam String indirizzo,
                                            @RequestParam(required = false) String bio,
                                            @RequestParam(required = false) String avatarUrl,
                                            @RequestParam(required = false) String latitudine,
                                            @RequestParam(required = false) String longitudine,
                                            @RequestParam Integer anni,
                                            @RequestParam(required = false) String scuolaOUniversita,
                                            @RequestParam(required = false) String annoStudio,
                                            @RequestParam Integer raggioKm,
                                            @RequestParam(required = false) String mezzoSpostamento,
                                            @RequestParam(defaultValue = "false") boolean disponibile,
                                            @RequestParam Integer oreSettimanaMax,
                                            @RequestParam String compensoMinOra,
                                            Authentication authentication,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {

        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        ProfiloGiovane profilo = profiloGiovaneService.findByUtenteId(utente.getId())
                .orElseGet(() -> {
                    ProfiloGiovane nuovoProfilo = new ProfiloGiovane();
                    nuovoProfilo.setUtenteId(utente.getId());
                    return nuovoProfilo;
                });

        if (isBlank(nome) || isBlank(cognome) || isBlank(email) || isBlank(telefono)
                || isBlank(dataNascita) || isBlank(citta) || isBlank(indirizzo)) {
            model.addAttribute("errorMessage", "Compila tutti i campi obbligatori.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dataNascita.trim());
        } catch (DateTimeParseException ex) {
            model.addAttribute("errorMessage", "Data di nascita non valida.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }
        if (birthDate.isAfter(LocalDate.now())) {
            model.addAttribute("errorMessage", "La data di nascita non puo essere futura.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }

        int eta = Period.between(birthDate, LocalDate.now()).getYears();
        // Doppio controllo: eta ricavata dalla data + campo anni compilato nel profilo.
        if (anni == null || anni < 14 || anni > 25 || eta < 14 || eta > 25) {
            model.addAttribute("errorMessage", "Eta consentita tra 14 e 25 anni.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }

        String normalizedEmail = email.trim().toLowerCase();
        if (!normalizedEmail.equalsIgnoreCase(utente.getEmail()) && utenteService.existsByEmail(normalizedEmail)) {
            model.addAttribute("errorMessage", "Email gia in uso.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }

        if (raggioKm == null || raggioKm < 1 || raggioKm > 100
                || oreSettimanaMax == null || oreSettimanaMax < 1 || oreSettimanaMax > 80) {
            model.addAttribute("errorMessage", "Raggio e ore settimanali devono essere valori validi.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }

        BigDecimal compenso;
        try {
            compenso = new BigDecimal(compensoMinOra.trim());
        } catch (NumberFormatException ex) {
            model.addAttribute("errorMessage", "Compenso minimo non valido.");
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }

        utente.setNome(nome.trim());
        utente.setCognome(cognome.trim());
        utente.setEmail(normalizedEmail);
        utente.setTelefono(telefono.trim());
        utente.setDataNascita(birthDate);
        utente.setCitta(citta.trim());
        utente.setCap(cap == null ? null : cap.trim());
        utente.setIndirizzo(indirizzo.trim());
        utente.setBio(bio == null ? null : bio.trim());
        utente.setAvatarUrl(avatarUrl == null ? null : avatarUrl.trim());
        BigDecimal latitude = parseBigDecimal(latitudine);
        BigDecimal longitude = parseBigDecimal(longitudine);
        List<String> coordinateErrors = new ArrayList<>();
        validateDecimalInput(coordinateErrors, latitudine, latitude, "Latitudine");
        validateDecimalInput(coordinateErrors, longitudine, longitude, "Longitudine");
        if (!coordinateErrors.isEmpty()) {
            model.addAttribute("errorMessage", String.join(" ", coordinateErrors));
            bindYoungProfileModel(model, utente, profilo);
            populateBirthDateRange(model);
            return "pages/young-profile";
        }
        utente.setLatitudine(latitude);
        utente.setLongitudine(longitude);

        profilo.setUtenteId(utente.getId());
        profilo.setAnni(anni);
        profilo.setScuolaOUniversita(isBlank(scuolaOUniversita) ? null : scuolaOUniversita.trim());
        profilo.setAnnoStudio(isBlank(annoStudio) ? null : annoStudio.trim());
        profilo.setRaggioKm(raggioKm);
        profilo.setMezzoSpostamento(isBlank(mezzoSpostamento) ? null : mezzoSpostamento.trim());
        profilo.setDisponibile(disponibile);
        profilo.setOreSettimanaMax(oreSettimanaMax);
        profilo.setCompensoMinOra(compenso);
        // Campi statistici non modificabili da utente giovane.

        utenteService.save(utente);
        profiloGiovaneService.save(profilo);
        refreshAuthenticationName(authentication, normalizedEmail);

        redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo.");
        return "redirect:/profilo/giovane";
    }

    @GetMapping("/annunci/nuovo")
    public String newJob(Model model, Authentication authentication) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        bindNewJobDefaults(model);
        return "pages/new-job";
    }

    @PostMapping("/annunci")
    public String createJob(@RequestParam String titolo,
                            @RequestParam Long categoriaId,
                            @RequestParam String descrizione,
                            @RequestParam String citta,
                            @RequestParam(required = false) String indirizzo,
                            @RequestParam(required = false) String luogoNote,
                            @RequestParam(required = false) String latitudine,
                            @RequestParam(required = false) String longitudine,
                            @RequestParam(required = false) String dataInizio,
                            @RequestParam(required = false) String oraInizio,
                            @RequestParam(required = false) String durataOre,
                            @RequestParam String compensoImporto,
                            @RequestParam(defaultValue = "false") boolean urgente,
                            Authentication authentication,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        List<String> errors = validateJobForm(titolo, descrizione, citta, compensoImporto);
        BigDecimal compenso = parseBigDecimal(compensoImporto);
        BigDecimal durata = parseBigDecimal(durataOre);
        LocalDate giorno = parseDate(dataInizio);
        LocalTime orario = parseTime(oraInizio);
        BigDecimal latitude = parseBigDecimal(latitudine);
        BigDecimal longitude = parseBigDecimal(longitudine);

        if (compenso == null || compenso.compareTo(BigDecimal.valueOf(5)) < 0) {
            errors.add("Il compenso minimo per ogni lavoro e di 5 EUR.");
        }
        if (durataOre != null && !durataOre.isBlank() && durata == null) {
            errors.add("Durata non valida.");
        }
        if (dataInizio != null && !dataInizio.isBlank() && giorno == null) {
            errors.add("Data non valida.");
        }
        if (oraInizio != null && !oraInizio.isBlank() && orario == null) {
            errors.add("Orario non valido.");
        }
        validateDecimalInput(errors, latitudine, latitude, "Latitudine");
        validateDecimalInput(errors, longitudine, longitude, "Longitudine");
        if (durata != null && durata.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("La durata deve essere maggiore di zero.");
        }
        if (categoriaId == null || categoriaService.findAll().stream()
                .noneMatch(categoria -> categoria.getId().equals(categoriaId))) {
            errors.add("Categoria non valida.");
        }
        validateCoordinates(errors, latitude, longitude);

        if (!errors.isEmpty()) {
            bindNewJobDefaults(model);
            bindJobFormValues(model, titolo, categoriaId, descrizione, citta, indirizzo, luogoNote,
                    dataInizio, oraInizio, durataOre, compensoImporto, latitudine, longitudine, urgente);
            model.addAttribute("errorMessage", String.join(" ", errors));
            return "pages/new-job";
        }

        Annuncio annuncio = new Annuncio();
        annuncio.setRichiedenteId(utente.getId());
        annuncio.setCategoriaId(categoriaId);
        annuncio.setTitolo(titolo.trim());
        annuncio.setDescrizione(descrizione.trim());
        annuncio.setCitta(citta.trim());
        annuncio.setIndirizzo(isBlank(indirizzo) ? null : indirizzo.trim());
        annuncio.setLuogoNote(isBlank(luogoNote) ? null : luogoNote.trim());
        annuncio.setLatitudine(latitude);
        annuncio.setLongitudine(longitude);
        annuncio.setDataInizio(giorno);
        annuncio.setOraInizio(orario);
        annuncio.setDurataOre(durata);
        annuncio.setCompensoImporto(compenso);
        annuncio.setCompensoTipo("orario");
        annuncio.setValuta("EUR");
        annuncio.setUrgente(urgente);
        annuncio.setVisibile(true);
        annuncio.setStato("attivo");
        annuncioService.save(annuncio);

        redirectAttributes.addFlashAttribute("successMessage", "Lavoro creato con successo.");
        return "redirect:/dashboard/richiedente/lavori";
    }

    @GetMapping("/annunci/{id}/modifica")
    public String editJob(@PathVariable long id,
                          Authentication authentication,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        Optional<Annuncio> annuncioOptional = annuncioService.findByIdAndRichiedente(id, utente.getId());
        if (annuncioOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lavoro non trovato o non autorizzato.");
            return "redirect:/dashboard/richiedente/lavori";
        }

        bindNewJobDefaults(model);
        model.addAttribute("isEdit", true);
        model.addAttribute("formAction", "/annunci/" + id + "/modifica");
        model.addAttribute("jobId", id);

        Annuncio annuncio = annuncioOptional.get();
        bindJobFormValues(model,
                annuncio.getTitolo(),
                annuncio.getCategoriaId(),
                annuncio.getDescrizione(),
                annuncio.getCitta(),
                annuncio.getIndirizzo(),
                annuncio.getLuogoNote(),
                annuncio.getDataInizio() != null ? annuncio.getDataInizio().toString() : "",
                annuncio.getOraInizio() != null ? annuncio.getOraInizio().toString() : "",
                annuncio.getDurataOre() != null ? annuncio.getDurataOre().toPlainString() : "",
                annuncio.getCompensoImporto() != null ? annuncio.getCompensoImporto().toPlainString() : "",
                annuncio.getLatitudine() != null ? annuncio.getLatitudine().toPlainString() : "",
                annuncio.getLongitudine() != null ? annuncio.getLongitudine().toPlainString() : "",
                annuncio.isUrgente());
        return "pages/new-job";
    }

    @PostMapping("/annunci/{id}/modifica")
    public String updateJob(@PathVariable long id,
                            @RequestParam String titolo,
                            @RequestParam Long categoriaId,
                            @RequestParam String descrizione,
                            @RequestParam String citta,
                            @RequestParam(required = false) String indirizzo,
                            @RequestParam(required = false) String luogoNote,
                            @RequestParam(required = false) String latitudine,
                            @RequestParam(required = false) String longitudine,
                            @RequestParam(required = false) String dataInizio,
                            @RequestParam(required = false) String oraInizio,
                            @RequestParam(required = false) String durataOre,
                            @RequestParam String compensoImporto,
                            @RequestParam(defaultValue = "false") boolean urgente,
                            Authentication authentication,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        Annuncio annuncio = annuncioService.findByIdAndRichiedente(id, utente.getId()).orElse(null);
        if (annuncio == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lavoro non trovato o non autorizzato.");
            return "redirect:/dashboard/richiedente/lavori";
        }

        List<String> errors = validateJobForm(titolo, descrizione, citta, compensoImporto);
        BigDecimal compenso = parseBigDecimal(compensoImporto);
        BigDecimal durata = parseBigDecimal(durataOre);
        LocalDate giorno = parseDate(dataInizio);
        LocalTime orario = parseTime(oraInizio);
        BigDecimal latitude = parseBigDecimal(latitudine);
        BigDecimal longitude = parseBigDecimal(longitudine);

        if (compenso == null || compenso.compareTo(BigDecimal.valueOf(5)) < 0) {
            errors.add("Il compenso minimo per ogni lavoro e di 5 EUR.");
        }
        if (durataOre != null && !durataOre.isBlank() && durata == null) {
            errors.add("Durata non valida.");
        }
        if (dataInizio != null && !dataInizio.isBlank() && giorno == null) {
            errors.add("Data non valida.");
        }
        if (oraInizio != null && !oraInizio.isBlank() && orario == null) {
            errors.add("Orario non valido.");
        }
        validateDecimalInput(errors, latitudine, latitude, "Latitudine");
        validateDecimalInput(errors, longitudine, longitude, "Longitudine");
        if (durata != null && durata.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("La durata deve essere maggiore di zero.");
        }
        if (categoriaId == null || categoriaService.findAll().stream()
                .noneMatch(categoria -> categoria.getId().equals(categoriaId))) {
            errors.add("Categoria non valida.");
        }
        validateCoordinates(errors, latitude, longitude);

        if (!errors.isEmpty()) {
            bindNewJobDefaults(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("formAction", "/annunci/" + id + "/modifica");
            model.addAttribute("jobId", id);
            bindJobFormValues(model, titolo, categoriaId, descrizione, citta, indirizzo, luogoNote,
                    dataInizio, oraInizio, durataOre, compensoImporto, latitudine, longitudine, urgente);
            model.addAttribute("errorMessage", String.join(" ", errors));
            return "pages/new-job";
        }

        annuncio.setCategoriaId(categoriaId);
        annuncio.setTitolo(titolo.trim());
        annuncio.setDescrizione(descrizione.trim());
        annuncio.setCitta(citta.trim());
        annuncio.setIndirizzo(isBlank(indirizzo) ? null : indirizzo.trim());
        annuncio.setLuogoNote(isBlank(luogoNote) ? null : luogoNote.trim());
        annuncio.setLatitudine(latitude);
        annuncio.setLongitudine(longitude);
        annuncio.setDataInizio(giorno);
        annuncio.setOraInizio(orario);
        annuncio.setDurataOre(durata);
        annuncio.setCompensoImporto(compenso);
        annuncio.setUrgente(urgente);
        annuncioService.save(annuncio);

        redirectAttributes.addFlashAttribute("successMessage", "Lavoro aggiornato con successo.");
        return "redirect:/dashboard/richiedente/lavori";
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }

        if ("giovane".equalsIgnoreCase(utente.getRuolo())) {
            model.addAttribute("chatTitle", "Chat AI - Opportunita vicine");
            model.addAttribute("chatSubtitle", "Mockup: trova lavori vicini con poche ore in base alle tue preferenze.");
            model.addAttribute("messages", demoDataService.aiChatForYoungMock());
            model.addAttribute("chatPlaceholder", "Es. Cerca lavori entro 3 km per oggi pomeriggio");
        } else if ("richiedente".equalsIgnoreCase(utente.getRuolo())) {
            model.addAttribute("chatTitle", "Chat AI - Match giovani tech");
            model.addAttribute("chatSubtitle", "Mockup: identifica giovani con esperienza tech per i tuoi lavori.");
            model.addAttribute("messages", demoDataService.aiChatForRequesterMock());
            model.addAttribute("chatPlaceholder", "Es. Cerco giovane con competenze reti e assistenza PC");
        } else {
            model.addAttribute("chatTitle", "Chat AI");
            model.addAttribute("chatSubtitle", "Assistente rapido per suggerimenti e supporto.");
            model.addAttribute("messages", demoDataService.chatMessages());
            model.addAttribute("chatPlaceholder", "Scrivi un messaggio...");
        }

        return "pages/chat";
    }

    @GetMapping("/chat/lavori")
    public String workChats(Authentication authentication, Model model) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo()) && !"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        model.addAttribute("workChats", buildWorkChats(utente));
        return "pages/work-chats";
    }

    @GetMapping("/chat/lavori/{jobId}")
    public String workChatDetail(@PathVariable long jobId, Authentication authentication, Model model) {
        Utente utente = getAuthenticatedUser(authentication);
        if (utente == null) {
            return "redirect:/auth";
        }
        if (!"giovane".equalsIgnoreCase(utente.getRuolo()) && !"richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return "redirect:/home";
        }

        List<DemoDataService.WorkChatPreview> workChats = buildWorkChats(utente);
        if (workChats.stream().noneMatch(chat -> chat.jobId() == jobId)) {
            return "redirect:/chat/lavori";
        }

        model.addAttribute("selectedJob", demoDataService.jobById(jobId));
        model.addAttribute("workChats", workChats);
        model.addAttribute("messages", "giovane".equalsIgnoreCase(utente.getRuolo())
                ? demoDataService.jobChatMessagesMock(utente.getId(), jobId)
                : loadRequesterChatMessages(utente.getId(), jobId));
        model.addAttribute("chatJobId", jobId);
        return "pages/work-chat-detail";
    }

    @GetMapping("/recensioni")
    public String reviews(Model model) {
        model.addAttribute("reviews", demoDataService.reviews());
        model.addAttribute("profiles", demoDataService.youngProfiles());
        return "pages/reviews";
    }

    private Utente getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return utenteService.findActiveByEmail(authentication.getName()).orElse(null);
    }

    private boolean isActiveApplication(Candidatura candidatura) {
        String status = candidatura.getStato();
        return status == null
                || "inviata".equalsIgnoreCase(status)
                || "vista".equalsIgnoreCase(status)
                || "accettata".equalsIgnoreCase(status);
    }

    private void refreshAuthenticationName(Authentication authentication, String email) {
        if (authentication == null || authentication.getName().equalsIgnoreCase(email)) {
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                            email,
                            authentication.getCredentials(),
                            authentication.getAuthorities()
                )
        );
    }

    private List<DemoDataService.WorkChatPreview> buildWorkChats(Utente utente) {
        if ("giovane".equalsIgnoreCase(utente.getRuolo())) {
            return demoDataService.youngWorkChatsMock(utente.getId());
        }

        if ("richiedente".equalsIgnoreCase(utente.getRuolo())) {
            return demoDataService.requesterChats(utente.getId(), 16).stream()
                    .map(chat -> new DemoDataService.WorkChatPreview(
                            chat.jobId(),
                            chat.jobTitle(),
                            chat.youngName(),
                            "attiva",
                            chat.lastMessage(),
                            chat.lastUpdated(),
                            chat.unreadCount()
                    ))
                    .toList();
        }

        return List.of();
    }

    private List<DemoDataService.ChatMessage> loadRequesterChatMessages(Long richiedenteId, long jobId) {
        List<Conversazione> conversations = conversazioneRepository
                .findByAnnuncioIdAndPartecipanteOrderByUltimoMessaggioAtDescCreatedAtDesc(jobId, richiedenteId);
        if (conversations.isEmpty()) {
            return List.of(new DemoDataService.ChatMessage(
                    "Sistema",
                    "Nessun messaggio disponibile per questo lavoro.",
                    "--:--",
                    false
            ));
        }

        Long conversationId = conversations.get(0).getId();
        List<Messaggio> messages = messaggioRepository.findByConversazioneIdOrderByInviatoAtAsc(conversationId);
        if (messages.isEmpty()) {
            return List.of(new DemoDataService.ChatMessage(
                    "Sistema",
                    "La conversazione e attiva ma non contiene ancora messaggi.",
                    "--:--",
                    false
            ));
        }

        return messages.stream()
                .map(message -> new DemoDataService.ChatMessage(
                        utenteService.findById(message.getMittenteId())
                                .map(u -> (u.getNome() + " " + u.getCognome()).trim())
                                .orElse("Utente"),
                        message.getTesto(),
                        message.getInviatoAt() != null ? message.getInviatoAt().toLocalTime().toString() : "--:--",
                        message.getMittenteId().equals(richiedenteId)
                ))
                .toList();
    }

    private Lavoro ensureLavoroRecord(Candidatura candidatura, Annuncio annuncio, long richiedenteId, long giovaneId) {
        // Garantisce idempotenza: se esiste lo stesso lavoro lo riusa, altrimenti lo crea.
        return lavoroRepository.findByCandidaturaId(candidatura.getId()).orElseGet(() -> {
            Lavoro lavoro = new Lavoro();
            lavoro.setCandidaturaId(candidatura.getId());
            lavoro.setAnnuncioId(annuncio.getId());
            lavoro.setRichiedenteId(richiedenteId);
            lavoro.setGiovaneId(giovaneId);
            lavoro.setStato("in_corso");
            lavoro.setDataInizioEff(LocalDateTime.now());
            return lavoroRepository.save(lavoro);
        });
    }

    private void recalculateYoungReputation(long youngId) {
        // Reputazione derivata solo da recensioni visibili, con media normalizzata a 2 decimali.
        List<Recensione> reviews = recensioneService.findVisibleByDestinatario(youngId);
        BigDecimal average = reviews.isEmpty()
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(reviews.stream()
                .mapToInt(Recensione::getVoto)
                .average()
                .orElse(0.0)).setScale(2, RoundingMode.HALF_UP);

        profiloGiovaneService.findByUtenteId(youngId).ifPresent(profile -> {
            profile.setPunteggioMedio(average);
            profiloGiovaneService.save(profile);
        });
    }

    private void bindYoungProfileModel(Model model, Utente utente, ProfiloGiovane profilo) {
        model.addAttribute("utente", utente);
        model.addAttribute("profilo", profilo);
    }

    private void bindRequesterProfileModel(Model model, Utente utente, ProfiloRichiedente profilo) {
        model.addAttribute("utente", utente);
        model.addAttribute("profilo", profilo);
    }

    private List<ReviewCard> loadReviewCardsForTarget(long targetUserId) {
        return recensioneService.findVisibleByDestinatario(targetUserId).stream()
                .map(review -> new ReviewCard(
                        utenteService.findById(review.getAutoreId())
                                .map(autore -> (autore.getNome() + " " + autore.getCognome()).trim())
                                .orElse("Utente"),
                        review.getVoto(),
                        review.getTesto() == null ? "Nessun commento" : review.getTesto()
                ))
                .toList();
    }

    private void populateBirthDateRange(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("minDataNascita", today.minusYears(25));
        model.addAttribute("maxDataNascita", today.minusYears(14));
    }

    private void bindNewJobDefaults(Model model) {
        model.addAttribute("categories", categoriaService.findAll());
        model.addAttribute("formAction", "/annunci");
        model.addAttribute("isEdit", false);
    }

    private void bindJobFormValues(Model model,
                                   String titolo,
                                   Long categoriaId,
                                   String descrizione,
                                   String citta,
                                   String indirizzo,
                                   String luogoNote,
                                   String dataInizio,
                                   String oraInizio,
                                   String durataOre,
                                   String compensoImporto,
                                   String latitudine,
                                   String longitudine,
                                   boolean urgente) {
        model.addAttribute("titolo", titolo);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("descrizione", descrizione);
        model.addAttribute("citta", citta);
        model.addAttribute("indirizzo", indirizzo);
        model.addAttribute("luogoNote", luogoNote);
        model.addAttribute("dataInizio", dataInizio);
        model.addAttribute("oraInizio", oraInizio);
        model.addAttribute("durataOre", durataOre);
        model.addAttribute("compensoImporto", compensoImporto);
        model.addAttribute("latitudine", latitudine);
        model.addAttribute("longitudine", longitudine);
        model.addAttribute("urgente", urgente);
    }

    private void validateCoordinates(List<String> errors, BigDecimal latitude, BigDecimal longitude) {
        if ((latitude == null) != (longitude == null)) {
            errors.add("Inserisci entrambe le coordinate della posizione.");
            return;
        }
        if (latitude != null && (latitude.compareTo(BigDecimal.valueOf(-90)) < 0
                || latitude.compareTo(BigDecimal.valueOf(90)) > 0
                || longitude.compareTo(BigDecimal.valueOf(-180)) < 0
                || longitude.compareTo(BigDecimal.valueOf(180)) > 0)) {
            errors.add("Coordinate non valide.");
        }
    }

    private void validateDecimalInput(List<String> errors, String rawValue, BigDecimal parsedValue, String label) {
        if (!isBlank(rawValue) && parsedValue == null) {
            errors.add(label + " non valida.");
        }
    }

    private List<String> validateJobForm(String titolo, String descrizione, String citta, String compensoImporto) {
        List<String> errors = new ArrayList<>();
        if (isBlank(titolo)) {
            errors.add("Inserisci un titolo.");
        }
        if (isBlank(descrizione)) {
            errors.add("Inserisci una descrizione.");
        }
        if (isBlank(citta)) {
            errors.add("Inserisci una citta.");
        }
        if (isBlank(compensoImporto)) {
            errors.add("Inserisci il compenso orario.");
        }
        return errors;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private LocalTime parseTime(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            return LocalTime.parse(value.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private record ReviewCard(String author, int rating, String text) {
    }

}
