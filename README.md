# 🟢 Helping Hood

> **"Meno scroll, più esperienza reale."**

Helping Hood è una piattaforma web di prossimità che connette giovani disponibili a svolgere piccoli lavori con privati, famiglie, anziani, enti locali e piccole organizzazioni che necessitano di servizi semplici su base giornaliera o per poche ore.

Progetto sviluppato durante l'**Hackathon "Hack the Future of Wellbeing" — VI Edizione**
ITIS Badoni, Lecco | Aprile 2026 | Organizzato da Softeam

---

## 📌 Il Problema

La Gen Z è la generazione più connessa della storia, ma spesso la meno attiva fisicamente e socialmente nel proprio territorio. Il team ha identificato tre problematiche interconnesse:

- **Sedentarietà digitale** — tempo online passivo a scapito dell'attività fisica e degli spostamenti nel territorio
- **Mancanza di prime esperienze lavorative** — assenza di strumenti semplici e locali pensati per studenti delle superiori o del primo anno universitario
- **Disconnessione dalla comunità locale** — reti sociali globali ma scarso rapporto con il proprio quartiere e i bisogni concreti di chi ci vive

---

## 💡 La Soluzione

Helping Hood usa il digitale come **ponte verso l'azione reale**: spostarsi, incontrare, lavorare, guadagnare e costruire fiducia nel proprio quartiere.

Il meccanismo chiave è l'**Algoritmo di Prossimità**: a differenza delle grandi piattaforme di gig economy, Helping Hood privilegia il matching iper-locale. I lavori proposti si trovano a distanza raggiungibile a piedi o in bicicletta (raggio configurabile 1–5 km).

### Categorie di servizio
Ripetizioni · Babysitting · Spesa e commissioni · Aiuto tecnologico · Lavori domestici · Giardinaggio · Trasloco e trasporto · Supporto a eventi · Compagnia agli anziani

---

## 🔄 Come Funziona

### Flow 1 — Offro Aiuto (giovani/studenti)
1. **Onboarding** — crea il profilo con competenze, disponibilità, zona e mezzo di spostamento
2. **Matching geolocalizzato** — l'algoritmo filtra gli annunci nelle vicinanze compatibili con il profilo
3. **Candidatura** — invia un breve messaggio e attendi la risposta
4. **Reputazione** — ricevi compenso e recensione; accumula badge progressivi (*Prima esperienza*, *Affidabile*, *Vicino di quartiere*…)

### Flow 2 — Cerco Aiuto (famiglie, anziani, enti)
1. **Inserimento annuncio** — pubblica una richiesta con descrizione, compenso, luogo e fascia oraria
2. **Ricezione candidature** — visualizza i profili con valutazione e badge
3. **Chat integrata** — definisci i dettagli logistici dell'incontro
4. **Feedback** — lascia una recensione a lavoro completato

---

## 🛠️ Stack Tecnologico

| Layer | Tecnologie |
|---|---|
| Front-end | React 18, Vite, Tailwind CSS, Framer Motion, Lucide React |
| Database | MySQL (schema completo con 15 tabelle, viste, stored procedure, trigger) |
| Landing Page | HTML5, CSS3, JavaScript |
| Design | Canva, Google Fonts (Bricolage Grotesque + Inter) |
| Social | Instagram [@helpinghood2026](https://instagram.com/helpinghood2026) |

---

## 💰 Modello Finanziario

Helping Hood utilizza un sistema di **Marketplace Escrow** tramite partner autorizzati (es. Stripe Connect), in conformità con la Direttiva PSD2.

- Pubblicazione degli annunci **gratuita** per i richiedenti
- Al momento dell'accettazione del task, l'importo viene "congelato" in un wallet neutrale
- A lavoro completato: **split payment automatico** tra quota lavoratore e service fee della piattaforma

### Tutela fiscale — La "bolla" dei €4.900
Il sistema monitora i guadagni in tempo reale. Superati i €4.900 netti annui, la piattaforma inibisce automaticamente la possibilità di accettare nuovi task retribuiti, garantendo il **Triplo Esonero** (Art. 44 D.L. 269/2003): esonero INPS, esonero fiscale e esonero dichiarativo.

---

## 🚀 Sviluppi Futuri

- **Integrazione PCTO** — collegare i lavoretti al percorso di Alternanza Scuola-Lavoro
- **Curriculum digitale progressivo** — profilo professionale esportabile generato dall'esperienza concreta
- **Assistente AI integrato** — agente intelligente per supportare onboarding, matching e comunicazione tra utenti

## 🗺️ Mappa dei lavori

La pagina `/lavori` include una mappa interattiva basata su Leaflet e OpenStreetMap. Gli annunci con coordinate salvate vengono mostrati come marker; nella pagina di creazione/modifica è possibile cercare città/indirizzo oppure selezionare manualmente un punto sulla mappa.

OpenStreetMap non fornisce una propria chiave API per i tile. In sviluppo viene usato il tile endpoint pubblico; in produzione è consigliato configurare un provider compatibile e autorizzato tramite:

```properties
MAP_TILE_URL=https://{s}.provider.example/{z}/{x}/{y}.png?key=...
MAP_ATTRIBUTION=Attribuzione richiesta dal provider
```

Le variabili vengono lette da `app.maps.tile-url` e `app.maps.attribution`. Il geocoding usa Nominatim con richieste esplicite dell'utente; per carichi elevati va usato un servizio di geocoding dedicato nel rispetto dei relativi limiti.

---

## 👥 Il Team

| Nome | Ruolo |
|---|---|
| Lorenzo Piazza | Project Manager & Backend Developer |
| Andrea Cederle | Frontend Developer |
| Alessio Tilola | Graphic Designer |
| Riccardo Belcastro | Social Media Manager |
| Marco Scala | Content & Documentation Coordinator |

---

## 🌐 Link

- **Sito web:** [helpinghood.duckdns.org](https://helpinghood.duckdns.org)
- **Instagram:** [@helpinghood2026](https://instagram.com/helpinghood2026)

---

*Hackathon "Hack the Future of Wellbeing" — VI Edizione · ITIS Badoni, Lecco · Aprile 2026 · Organizzato da Softeam*
