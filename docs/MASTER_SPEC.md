# Master Spec

## Beta 0.1 - Quick Capture

Obiettivo: creare uno scheletro Android nativo realmente compilabile e installabile.

La Beta 0.1 deve essere piccola, stabile e utilizzabile.

## Funzioni

### Quick Capture

L'utente preme "Cattura", inserisce un testo, sceglie facoltativamente il tipo e salva nella Inbox.

Tipi iniziali:

- NOTE;
- IDEA;
- TASK.

### Inbox

Ogni nuovo elemento entra in Inbox.

Stati:

- INBOX;
- CONFIRMED;
- COMPLETED;
- IGNORED.

Dalla Inbox l'utente puo:

- modificare;
- confermare;
- ignorare;
- trasformare in attivita.

### Attivita

Le attivita sono elementi di tipo TASK non ignorati.

L'utente puo:

- completare;
- riaprire;
- eliminare con conferma.

### Dashboard

Mostra:

- elementi da controllare;
- attivita aperte;
- attivita in scadenza;
- pulsante Cattura;
- accesso a Inbox;
- accesso ad Attivita.

## Tecnologia

- Kotlin;
- Jetpack Compose;
- Material 3;
- Room;
- Coroutines e Flow;
- MVVM;
- Repository Pattern;
- Navigation Compose;
- WorkManager predisposto;
- Gradle Kotlin DSL;
- GitHub Actions.

## Non implementare ora

- Gmail;
- PEC;
- WhatsApp automatico;
- cloud;
- AI remota;
- OCR avanzato;
- Google Maps;
- geofencing;
- registrazione chiamate.
