-- ============================================================
--  HELPING HOOD — Database Schema
--  Hackathon "Hack the Future of Wellbeing" — Istituto Badoni, Lecco
--  Piattaforma di mutuo aiuto di quartiere per giovani
--  Modello: Pure Marketplace Intermediary (nessun pagamento diretto)
-- ============================================================

-- ============================================================
-- 0. SETUP
-- ============================================================
CREATE DATABASE IF NOT EXISTS helping_hood CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE helping_hood;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. CATEGORIE LAVORETTI
-- ============================================================
CREATE TABLE categorie (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(60)  NOT NULL,
    icona         VARCHAR(60)  NOT NULL COMMENT 'nome icona Lucide/emoji',
    descrizione   TEXT,
    colore_hex    CHAR(7)      DEFAULT '#4CAF50',
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO categorie (nome, icona, descrizione, colore_hex) VALUES
('Ripetizioni',           'graduation-cap',  'Supporto scolastico e tutoraggio',                '#2E7D32'),
('Babysitting',           'baby',            'Cura e sorveglianza di bambini',                  '#388E3C'),
('Spesa e commissioni',   'shopping-cart',   'Acquisti, ritiro pacchi, piccole commissioni',   '#43A047'),
('Aiuto tech',            'monitor',         'Supporto con smartphone, PC, stampanti, TV',     '#4CAF50'),
('Lavori domestici',      'home',            'Pulizie, riordino, piccoli lavori in casa',      '#66BB6A'),
('Giardinaggio',          'leaf',            'Cura del verde, potatura, innaffiature',         '#81C784'),
('Trasloco e trasporto',  'truck',           'Aiuto nel traslocare, portare oggetti',          '#A5D6A7'),
('Supporto eventi',       'calendar',        'Allestimento, accoglienza, logistica eventi',    '#C8E6C9'),
('Compagnia anziani',     'heart',           'Conversazione, passeggiate, lettura per anziani','#1B5E20'),
('Altro',                 'more-horizontal', 'Qualsiasi altra attività non categorizzata',     '#388E3C');

-- ============================================================
-- 2. UTENTI
-- ============================================================
CREATE TABLE utenti (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(80)  NOT NULL,
    cognome         VARCHAR(80)  NOT NULL,
    email           VARCHAR(180) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    ruolo           ENUM('giovane','richiedente','admin') NOT NULL DEFAULT 'giovane',
    avatar_url      VARCHAR(500),
    telefono        VARCHAR(20),
    bio             TEXT,
    data_nascita    DATE,
    -- geolocalizzazione
    citta           VARCHAR(100) DEFAULT 'Lecco',
    cap             CHAR(5),
    indirizzo       VARCHAR(200),
    latitudine      DECIMAL(10,7),
    longitudine     DECIMAL(10,7),
    -- metadati
    verificato      TINYINT(1)   DEFAULT 0,
    attivo          TINYINT(1)   DEFAULT 1,
    ultimo_accesso  TIMESTAMP    NULL,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. PROFILI GIOVANI (estensione di utenti con ruolo='giovane')
-- ============================================================
CREATE TABLE profili_giovani (
    id                  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id           INT UNSIGNED NOT NULL UNIQUE,
    anni                TINYINT UNSIGNED,
    scuola_o_universita VARCHAR(150),
    anno_studio         VARCHAR(60),
    raggio_km           TINYINT UNSIGNED DEFAULT 5 COMMENT 'distanza max in km che il giovane è disposto a percorrere',
    mezzo_spostamento   SET('piedi','bici','monopattino','auto','moto') DEFAULT 'piedi,bici',
    disponibile         TINYINT(1) DEFAULT 1,
    ore_settimana_max   TINYINT UNSIGNED DEFAULT 10,
    compenso_min_ora    DECIMAL(6,2) DEFAULT 0.00,
    punteggio_medio     DECIMAL(3,2) DEFAULT 0.00,
    n_lavori_completati SMALLINT UNSIGNED DEFAULT 0,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utente_id) REFERENCES utenti(id) ON DELETE CASCADE
);

-- ============================================================
-- 4. COMPETENZE GIOVANI
-- ============================================================
CREATE TABLE competenze (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(80)  NOT NULL UNIQUE,
    categoria_id  INT UNSIGNED,
    FOREIGN KEY (categoria_id) REFERENCES categorie(id) ON DELETE SET NULL
);

INSERT INTO competenze (nome, categoria_id) VALUES
('Matematica',          1), ('Inglese',           1), ('Italiano',          1),
('Fisica',              1), ('Chimica',           1), ('Latino',            1),
('Cura bambini 0-3',    2), ('Cura bambini 4-10',  2), ('Babysitting serale',2),
('Spesa al supermercato',3),('Ritiro pacchi',      3),
('Supporto smartphone', 4), ('Supporto PC/Mac',    4), ('Configurazione TV', 4),
('Pulizie casa',        5), ('Riordino',           5), ('Cucina base',       5),
('Giardinaggio base',   6), ('Potatura',           6),
('Compagnia anziani',   9);

CREATE TABLE giovane_competenze (
    giovane_id    INT UNSIGNED NOT NULL,
    competenza_id INT UNSIGNED NOT NULL,
    livello       ENUM('base','intermedio','avanzato') DEFAULT 'base',
    PRIMARY KEY (giovane_id, competenza_id),
    FOREIGN KEY (giovane_id)    REFERENCES profili_giovani(id) ON DELETE CASCADE,
    FOREIGN KEY (competenza_id) REFERENCES competenze(id)      ON DELETE CASCADE
);

-- ============================================================
-- 5. DISPONIBILITÀ ORARIA GIOVANI
-- ============================================================
CREATE TABLE disponibilita (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    giovane_id  INT UNSIGNED NOT NULL,
    giorno      ENUM('lunedi','martedi','mercoledi','giovedi','venerdi','sabato','domenica') NOT NULL,
    ora_inizio  TIME NOT NULL,
    ora_fine    TIME NOT NULL,
    FOREIGN KEY (giovane_id) REFERENCES profili_giovani(id) ON DELETE CASCADE
);

-- ============================================================
-- 6. PROFILI RICHIEDENTI (privati, enti, organizzazioni)
-- ============================================================
CREATE TABLE profili_richiedenti (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id       INT UNSIGNED NOT NULL UNIQUE,
    tipo            ENUM('privato','famiglia','anziano','ente_pubblico','associazione','piccola_impresa') DEFAULT 'privato',
    nome_org        VARCHAR(150) COMMENT 'se ente/associazione/impresa',
    sito_web        VARCHAR(300),
    punteggio_medio DECIMAL(3,2) DEFAULT 0.00,
    n_annunci_totali SMALLINT UNSIGNED DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utente_id) REFERENCES utenti(id) ON DELETE CASCADE
);

-- ============================================================
-- 7. ANNUNCI / LAVORETTI
-- ============================================================
CREATE TABLE annunci (
    id                  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    richiedente_id      INT UNSIGNED NOT NULL,
    categoria_id        INT UNSIGNED,
    titolo              VARCHAR(150) NOT NULL,
    descrizione         TEXT         NOT NULL,
    -- luogo
    citta               VARCHAR(100) DEFAULT 'Lecco',
    cap                 CHAR(5),
    indirizzo           VARCHAR(200),
    latitudine          DECIMAL(10,7),
    longitudine         DECIMAL(10,7),
    luogo_note          VARCHAR(200) COMMENT 'es. vicino alla stazione, in zona Pescarenico',
    -- compenso
    compenso_tipo       ENUM('orario','fisso','volontariato') DEFAULT 'orario',
    compenso_importo    DECIMAL(8,2) DEFAULT 0.00,
    valuta              CHAR(3)      DEFAULT 'EUR',
    -- pianificazione
    data_inizio         DATE,
    ora_inizio          TIME,
    durata_ore          DECIMAL(4,1),
    ricorrenza          ENUM('una_tantum','settimanale','mensile') DEFAULT 'una_tantum',
    -- gestione
    n_giovani_cercati   TINYINT UNSIGNED DEFAULT 1,
    stato               ENUM('bozza','attivo','in_corso','completato','annullato','scaduto') DEFAULT 'attivo',
    urgente             TINYINT(1)   DEFAULT 0,
    visibile            TINYINT(1)   DEFAULT 1,
    scade_il            TIMESTAMP    NULL,
    n_candidature       SMALLINT UNSIGNED DEFAULT 0,
    -- meta
    created_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (richiedente_id) REFERENCES utenti(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id)   REFERENCES categorie(id) ON DELETE SET NULL
);

CREATE INDEX idx_annunci_stato    ON annunci(stato);
CREATE INDEX idx_annunci_categoria ON annunci(categoria_id);
CREATE INDEX idx_annunci_citta    ON annunci(citta);
CREATE INDEX idx_annunci_geo      ON annunci(latitudine, longitudine);

-- ============================================================
-- 8. TAG ANNUNCI (etichette libere)
-- ============================================================
CREATE TABLE tags (
    id    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome  VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE annuncio_tags (
    annuncio_id INT UNSIGNED NOT NULL,
    tag_id      INT UNSIGNED NOT NULL,
    PRIMARY KEY (annuncio_id, tag_id),
    FOREIGN KEY (annuncio_id) REFERENCES annunci(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)      REFERENCES tags(id)    ON DELETE CASCADE
);

-- ============================================================
-- 9. CANDIDATURE
-- ============================================================
CREATE TABLE candidature (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    annuncio_id     INT UNSIGNED NOT NULL,
    giovane_utente_id INT UNSIGNED NOT NULL,
    messaggio       TEXT COMMENT 'messaggio di presentazione opzionale',
    stato           ENUM('inviata','vista','accettata','rifiutata','ritirata') DEFAULT 'inviata',
    data_candidatura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_risposta   TIMESTAMP NULL,
    UNIQUE KEY uniq_candidatura (annuncio_id, giovane_utente_id),
    FOREIGN KEY (annuncio_id)       REFERENCES annunci(id) ON DELETE CASCADE,
    FOREIGN KEY (giovane_utente_id) REFERENCES utenti(id)  ON DELETE CASCADE
);

-- ============================================================
-- 10. LAVORI (dalla candidatura accettata alla conclusione)
-- ============================================================
CREATE TABLE lavori (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    candidatura_id  INT UNSIGNED NOT NULL UNIQUE,
    annuncio_id     INT UNSIGNED NOT NULL,
    giovane_id      INT UNSIGNED NOT NULL,
    richiedente_id  INT UNSIGNED NOT NULL,
    data_inizio_eff TIMESTAMP NULL,
    data_fine_eff   TIMESTAMP NULL,
    ore_effettive   DECIMAL(5,2),
    stato           ENUM('confermato','in_corso','completato','contestato','annullato') DEFAULT 'confermato',
    note_chiusura   TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (candidatura_id)  REFERENCES candidature(id) ON DELETE CASCADE,
    FOREIGN KEY (annuncio_id)     REFERENCES annunci(id)     ON DELETE CASCADE,
    FOREIGN KEY (giovane_id)      REFERENCES utenti(id)      ON DELETE CASCADE,
    FOREIGN KEY (richiedente_id)  REFERENCES utenti(id)      ON DELETE CASCADE
);

-- ============================================================
-- 11. RECENSIONI (bidirezionali: richiedente→giovane e giovane→richiedente)
-- ============================================================
CREATE TABLE recensioni (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    lavoro_id       INT UNSIGNED NOT NULL,
    autore_id       INT UNSIGNED NOT NULL COMMENT 'chi scrive la recensione',
    destinatario_id INT UNSIGNED NOT NULL COMMENT 'chi riceve la recensione',
    voto            TINYINT UNSIGNED NOT NULL CHECK (voto BETWEEN 1 AND 5),
    testo           TEXT,
    puntualita      TINYINT UNSIGNED CHECK (puntualita BETWEEN 1 AND 5),
    qualita         TINYINT UNSIGNED CHECK (qualita BETWEEN 1 AND 5),
    comunicazione   TINYINT UNSIGNED CHECK (comunicazione BETWEEN 1 AND 5),
    rispetto        TINYINT UNSIGNED CHECK (rispetto BETWEEN 1 AND 5),
    visibile        TINYINT(1) DEFAULT 1,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uniq_recensione (lavoro_id, autore_id),
    FOREIGN KEY (lavoro_id)       REFERENCES lavori(id) ON DELETE CASCADE,
    FOREIGN KEY (autore_id)       REFERENCES utenti(id) ON DELETE CASCADE,
    FOREIGN KEY (destinatario_id) REFERENCES utenti(id) ON DELETE CASCADE
);

-- ============================================================
-- 12. MESSAGGI / CHAT
-- ============================================================
CREATE TABLE conversazioni (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    annuncio_id     INT UNSIGNED,
    partecipante_1  INT UNSIGNED NOT NULL,
    partecipante_2  INT UNSIGNED NOT NULL,
    ultimo_messaggio_at TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (annuncio_id)    REFERENCES annunci(id) ON DELETE SET NULL,
    FOREIGN KEY (partecipante_1) REFERENCES utenti(id) ON DELETE CASCADE,
    FOREIGN KEY (partecipante_2) REFERENCES utenti(id) ON DELETE CASCADE
);

CREATE TABLE messaggi (
    id               INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    conversazione_id INT UNSIGNED NOT NULL,
    mittente_id      INT UNSIGNED NOT NULL,
    testo            TEXT         NOT NULL,
    allegato_url     VARCHAR(500),
    letto            TINYINT(1)   DEFAULT 0,
    inviato_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversazione_id) REFERENCES conversazioni(id) ON DELETE CASCADE,
    FOREIGN KEY (mittente_id)      REFERENCES utenti(id)        ON DELETE CASCADE
);

CREATE INDEX idx_messaggi_conv ON messaggi(conversazione_id, inviato_at);

-- ============================================================
-- 13. NOTIFICHE
-- ============================================================
CREATE TABLE notifiche (
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    utente_id    INT UNSIGNED NOT NULL,
    tipo         ENUM('nuova_candidatura','candidatura_accettata','candidatura_rifiutata',
                      'nuovo_messaggio','nuovo_annuncio_vicino','lavoro_completato',
                      'nuova_recensione','promemoria') NOT NULL,
    titolo       VARCHAR(150),
    testo        TEXT,
    link_interno VARCHAR(300),
    letto        TINYINT(1) DEFAULT 0,
    created_at   TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (utente_id) REFERENCES utenti(id) ON DELETE CASCADE
);

-- ============================================================
-- 14. SEGNALAZIONI (sicurezza della community)
-- ============================================================
CREATE TABLE segnalazioni (
    id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    segnalante_id  INT UNSIGNED NOT NULL,
    tipo_target    ENUM('utente','annuncio','recensione','messaggio') NOT NULL,
    target_id      INT UNSIGNED NOT NULL,
    motivo         ENUM('spam','contenuto_inappropriato','utente_falso',
                        'frode','molestia','altro') NOT NULL,
    descrizione    TEXT,
    stato          ENUM('in_attesa','in_gestione','risolta','archiviata') DEFAULT 'in_attesa',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (segnalante_id) REFERENCES utenti(id) ON DELETE CASCADE
);

-- ============================================================
-- 15. BADGE / RICONOSCIMENTI GIOVANI
-- ============================================================
CREATE TABLE badge (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(80)  NOT NULL,
    descrizione   VARCHAR(200),
    icona         VARCHAR(80),
    colore_hex    CHAR(7)
);

INSERT INTO badge (nome, descrizione, icona, colore_hex) VALUES
('Prima esperienza',  'Ha completato il suo primo lavoretto',                    'star',       '#FFD700'),
('Affidabile',        'Ha ricevuto 5 recensioni positive consecutive',             'shield-check','#4CAF50'),
('Veloce',            'Accetta e completa lavoretti in meno di 2 ore',             'zap',        '#FF9800'),
('Tutore del mese',   'Ha svolto più ripetizioni nel mese',                        'book-open',  '#2196F3'),
('Super attivo',      'Ha completato 10 o più lavoretti',                          'award',      '#9C27B0'),
('Vicino di quartiere','Ha svolto almeno 5 lavoretti nello stesso raggio di 1km', 'map-pin',    '#009688');

CREATE TABLE giovane_badge (
    giovane_id  INT UNSIGNED NOT NULL,
    badge_id    INT UNSIGNED NOT NULL,
    assegnato_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (giovane_id, badge_id),
    FOREIGN KEY (giovane_id) REFERENCES profili_giovani(id) ON DELETE CASCADE,
    FOREIGN KEY (badge_id)   REFERENCES badge(id)           ON DELETE CASCADE
);

-- ============================================================
-- 16. DATI MOCK — utenti demo
-- ============================================================

-- Password hash = bcrypt di "Password123!" (solo demo)
INSERT INTO utenti (nome, cognome, email, password_hash, ruolo, bio, data_nascita, citta, cap, latitudine, longitudine, verificato) VALUES
('Matteo',  'Rossi',     'matteo@example.com',    '$2b$12$demoHashMatteo',   'giovane',     'Studente al Badoni, appassionato di tecnologia e sempre pronto ad aiutare.',  '2007-03-14', 'Lecco', '23900', 45.8565, 9.3967, 1),
('Sara',    'Colombo',   'sara@example.com',      '$2b$12$demoHashSara',     'giovane',     'Amo i bambini e la natura. Disponibile nel pomeriggio e nei weekend.',        '2006-07-22', 'Lecco', '23900', 45.8540, 9.3980, 1),
('Luca',    'Ferrari',   'luca@example.com',      '$2b$12$demoHashLuca',     'giovane',     'Bravo in matematica e fisica, aiuto con piacere nelle ripetizioni.',            '2008-01-09', 'Lecco', '23900', 45.8600, 9.4010, 1),
('Giulia',  'Bianchi',   'giulia@example.com',    '$2b$12$demoHashGiulia',   'giovane',     'Studio lingue e adoro stare con gli anziani. Sono paziente e disponibile.',   '2007-11-30', 'Lecco', '23900', 45.8520, 9.3940, 1),
('Signora', 'Verdi',     'verdi@example.com',     '$2b$12$demoHashVerdi',    'richiedente', 'Nonna con due nipotini, cerco qualcuno che li tenga d''occhio il pomeriggio.', '1955-05-10', 'Lecco', '23900', 45.8570, 9.3990, 1),
('Prof',    'Manzoni',   'manzoni@example.com',   '$2b$12$demoHashManzoni',  'richiedente', 'Cerco ragazzi per dare ripetizioni agli studenti del primo anno.',              '1972-09-01', 'Lecco', '23900', 45.8555, 9.3960, 1),
('Comune',  'Lecco',     'comune@lecco.it',       '$2b$12$demoHashComune',   'richiedente', 'Ente pubblico. Cerchiamo volontari per eventi culturali e festività locali.',  '1900-01-01', 'Lecco', '23900', 45.8558, 9.3966, 1),
('Parrocchia','SanNicola','parrocchia@example.com','$2b$12$demoHashParr',    'richiedente', 'Cerchiamo giovani per aiuto durante le feste e il grest estivo.',              '1900-01-01', 'Lecco', '23900', 45.8548, 9.3975, 1);

INSERT INTO profili_giovani (utente_id, anni, scuola_o_universita, anno_studio, raggio_km, mezzo_spostamento, disponibile, ore_settimana_max, punteggio_medio, n_lavori_completati) VALUES
(1, 17, 'ITIS Badoni Lecco',          '3° anno', 5, 'piedi,bici',       1, 12, 4.80, 7),
(2, 18, 'Liceo Manzoni Lecco',        '5° anno', 4, 'bici,monopattino', 1, 10, 4.90, 5),
(3, 16, 'ITIS Badoni Lecco',          '2° anno', 3, 'piedi,bici',       1,  8, 4.70, 3),
(4, 17, 'Liceo Scientifico Badoni',   '4° anno', 6, 'bici',             1, 15, 4.95, 9);

INSERT INTO profili_richiedenti (utente_id, tipo, nome_org) VALUES
(5, 'anziano',       NULL),
(6, 'privato',       NULL),
(7, 'ente_pubblico', 'Comune di Lecco'),
(8, 'associazione',  'Parrocchia San Nicola');

-- ============================================================
-- 17. DATI MOCK — annunci demo
-- ============================================================
INSERT INTO annunci (richiedente_id, categoria_id, titolo, descrizione, citta, cap, latitudine, longitudine, luogo_note, compenso_tipo, compenso_importo, data_inizio, ora_inizio, durata_ore, ricorrenza, n_giovani_cercati, stato, urgente) VALUES
(5, 2, 'Cerco babysitter per martedì pomeriggio',
   'Ho due nipotini di 4 e 6 anni, cercano qualcuno che li tenga d''occhio dalle 15 alle 18. Giocare, fare merenda e tenerli d''occhio.',
   'Lecco','23900',45.8570,9.3990,'Zona Pescarenico','orario',8.00,'2026-04-21','15:00',3,'settimanale',1,'attivo',0),

(6, 1, 'Ripetizioni di matematica per mio figlio (terza media)',
   'Mio figlio ha difficoltà con frazioni e geometria. Cerco uno studente delle superiori disponibile due pomeriggi a settimana.',
   'Lecco','23900',45.8555,9.3960,'Centro storico','orario',10.00,'2026-04-22','16:00',1.5,'settimanale',1,'attivo',0),

(7, 8, 'Volontari per evento "Lecco in Fiore" — 25 aprile',
   'Il Comune di Lecco cerca 5 giovani per accoglienza visitatori, distribuzione materiali e supporto logistico durante la manifestazione.',
   'Lecco','23900',45.8558,9.3966,'Piazza Manzoni','fisso',0.00,'2026-04-25','09:00',6,'una_tantum',5,'attivo',1),

(5, 3, 'Spesa al supermercato per venerdì',
   'Non riesco a camminare bene, ho bisogno di qualcuno che faccia la spesa per me con la lista che preparo io. Rimborso spese incluso.',
   'Lecco','23900',45.8570,9.3990,'Zona stazione','orario',7.00,'2026-04-19','10:00',2,'una_tantum',1,'attivo',1),

(8, 8, 'Aiuto durante il Grest estivo — giugno/luglio',
   'Cerchiamo ragazzi dai 16 anni in su per animare le attività del Grest 2026 presso la parrocchia. Esperienza formativa unica.',
   'Lecco','23900',45.8548,9.3975,'Via Parrocchia 3','volontariato',0.00,'2026-06-15','08:30',6,'settimanale',4,'attivo',0),

(6, 4, 'Aiuto con il nuovo smartphone a mia madre anziana',
   'Mia madre ha preso un nuovo telefono Android e non sa usarlo. Cerco un ragazzo paziente che le spieghi le basi in un paio d''ore.',
   'Lecco','23900',45.8540,9.3980,'Zona Caleotto','fisso',20.00,'2026-04-20','15:30',2,'una_tantum',1,'attivo',0);

-- ============================================================
-- 18. DATI MOCK — candidature e lavori
-- ============================================================
INSERT INTO candidature (annuncio_id, giovane_utente_id, messaggio, stato) VALUES
(1, 2, 'Ciao! Ho esperienza con bambini piccoli, sono affidabile e abito a pochi minuti. Sono disponibile il martedì!', 'accettata'),
(2, 1, 'Sono bravo in matematica, posso aiutare tuo figlio con piacere. Posso iniziare dalla prossima settimana.', 'accettata'),
(3, 4, 'Sarei molto felice di partecipare, amo gli eventi culturali e ho già fatto volontariato in passato.', 'inviata'),
(3, 1, 'Disponibile per il 25 aprile! Conosco bene Piazza Manzoni e sono bravo con la gente.', 'inviata'),
(4, 2, 'Posso fare la spesa venerdì mattina, abito vicino al supermercato Esselunga.', 'inviata'),
(6, 3, 'Mia nonna usa Android da anni, posso spiegare tutto con calma e pazienza.', 'vista');

INSERT INTO lavori (candidatura_id, annuncio_id, giovane_id, richiedente_id, data_inizio_eff, data_fine_eff, ore_effettive, stato) VALUES
(1, 1, 2, 5, '2026-04-15 15:00:00', '2026-04-15 18:00:00', 3.0, 'completato'),
(2, 2, 1, 6, '2026-04-16 16:00:00', '2026-04-16 17:30:00', 1.5, 'completato');

-- ============================================================
-- 19. DATI MOCK — recensioni
-- ============================================================
INSERT INTO recensioni (lavoro_id, autore_id, destinatario_id, voto, testo, puntualita, qualita, comunicazione, rispetto) VALUES
(1, 5, 2, 5, 'Sara è stata fantastica con i bambini! Puntuale, gentile e i nipotini la adorano. La richiamo sicuramente.', 5, 5, 5, 5),
(1, 2, 5, 5, 'La signora Verdi è gentilissima, mi ha accolta come una di casa. Esperienza bellissima.', 5, 5, 5, 5),
(2, 6, 1, 5, 'Matteo è preparatissimo e molto paziente. Mio figlio ha già capito le frazioni. Top!', 5, 5, 5, 5),
(2, 1, 6, 4, 'Famiglia molto disponibile, ambiente tranquillo. Raccomando.', 4, 4, 5, 5);

-- ============================================================
-- 20. DATI MOCK — messaggi
-- ============================================================
INSERT INTO conversazioni (annuncio_id, partecipante_1, partecipante_2) VALUES
(1, 5, 2),
(2, 6, 1);

INSERT INTO messaggi (conversazione_id, mittente_id, testo, letto) VALUES
(1, 5, 'Ciao Sara! Ho visto la tua candidatura. Sei disponibile martedì alle 15?', 1),
(1, 2, 'Ciao! Sì, sono liberissima. Devo portare qualcosa?', 1),
(1, 5, 'No, pensano a tutto io. Ti aspetto al civico 12 di via Pescarenico. Grazie!', 1),
(2, 6, 'Matteo, ottimo! Quando iniziamo? Mercoledì o giovedì pomeriggio?', 1),
(2, 1, 'Mercoledì perfetto! Sono disponibile dalle 16. Ci vediamo lì.', 1);

-- ============================================================
-- 21. VISTE UTILI
-- ============================================================

-- Annunci attivi con info categoria e richiedente
CREATE VIEW v_annunci_attivi AS
SELECT
    a.id, a.titolo, a.descrizione,
    a.citta, a.cap, a.latitudine, a.longitudine, a.luogo_note,
    a.compenso_tipo, a.compenso_importo,
    a.data_inizio, a.ora_inizio, a.durata_ore, a.ricorrenza,
    a.n_giovani_cercati, a.n_candidature, a.urgente,
    a.created_at,
    c.nome       AS categoria_nome,
    c.icona      AS categoria_icona,
    c.colore_hex AS categoria_colore,
    CONCAT(u.nome, ' ', u.cognome) AS richiedente_nome,
    pr.tipo      AS richiedente_tipo,
    pr.nome_org  AS richiedente_org,
    pr.punteggio_medio AS richiedente_voto
FROM annunci a
JOIN categorie c ON c.id = a.categoria_id
JOIN utenti u    ON u.id = a.richiedente_id
JOIN profili_richiedenti pr ON pr.utente_id = u.id
WHERE a.stato = 'attivo' AND a.visibile = 1;

-- Profilo pubblico giovane
CREATE VIEW v_profilo_giovane AS
SELECT
    u.id, u.nome, u.cognome, u.avatar_url, u.bio,
    u.citta, u.cap, u.latitudine, u.longitudine,
    pg.anni, pg.scuola_o_universita, pg.anno_studio,
    pg.raggio_km, pg.mezzo_spostamento,
    pg.disponibile, pg.ore_settimana_max,
    pg.compenso_min_ora, pg.punteggio_medio,
    pg.n_lavori_completati
FROM utenti u
JOIN profili_giovani pg ON pg.utente_id = u.id
WHERE u.attivo = 1;

-- Classifica giovani per punteggio
CREATE VIEW v_classifica_giovani AS
SELECT
    u.id, CONCAT(u.nome, ' ', u.cognome) AS nome_completo,
    u.avatar_url, u.citta,
    pg.punteggio_medio, pg.n_lavori_completati,
    pg.anni, pg.scuola_o_universita
FROM utenti u
JOIN profili_giovani pg ON pg.utente_id = u.id
WHERE u.attivo = 1
ORDER BY pg.punteggio_medio DESC, pg.n_lavori_completati DESC;

-- ============================================================
-- 22. STORED PROCEDURE — aggiorna punteggio medio giovane
-- ============================================================
DELIMITER $$

CREATE PROCEDURE aggiorna_punteggio_giovane(IN p_utente_id INT UNSIGNED)
BEGIN
    DECLARE media DECIMAL(3,2);
    DECLARE n_completati SMALLINT UNSIGNED;

    SELECT ROUND(AVG(r.voto), 2), COUNT(DISTINCT l.id)
    INTO media, n_completati
    FROM recensioni r
    JOIN lavori l ON l.id = r.lavoro_id
    WHERE r.destinatario_id = p_utente_id;

    UPDATE profili_giovani
    SET punteggio_medio     = COALESCE(media, 0.00),
        n_lavori_completati = COALESCE(n_completati, 0)
    WHERE utente_id = p_utente_id;
END$$

DELIMITER ;

-- ============================================================
-- 23. TRIGGER — aggiorna automaticamente il punteggio
-- ============================================================
DELIMITER $$

CREATE TRIGGER trg_after_recensione_insert
AFTER INSERT ON recensioni
FOR EACH ROW
BEGIN
    -- aggiorna punteggio destinatario
    CALL aggiorna_punteggio_giovane(NEW.destinatario_id);
    -- aggiorna counter candidature annuncio
    UPDATE annunci
    SET n_candidature = (
        SELECT COUNT(*) FROM candidature WHERE annuncio_id = annunci.id
    )
    WHERE id = (
        SELECT annuncio_id FROM lavori WHERE id = NEW.lavoro_id
    );
END$$

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- FINE SCHEMA — Helping Hood Database v1.0
-- ============================================================