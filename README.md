# Operativo

Operativo e un assistente operativo personale offline-first.

La Beta 0.1 si concentra su una base offline-first: inserimento rapido, Inbox di controllo, To do list, Calendario e Archivio separati.

```text
L'app propone.
L'utente controlla.
L'utente decide.
```

## Beta 0.1 - Base operativa

Funzioni incluse:

- Home minimale;
- pulsante "Inserimento vocale" con dettatura Android, se disponibile sul dispositivo;
- pulsante "Inserimento manuale";
- scelta esplicita della destinazione: To do, Calendario o Archivio;
- check list "Controllo rapido";
- sezioni operative separate dalla Home tramite navigazione inferiore;
- inserimento rapido di testo;
- destinazioni iniziali: Nota, To do, Calendario, Archivio;
- salvataggio nella Inbox con destinazione proposta visibile;
- modifica degli elementi in Inbox;
- conferma esplicita verso To do, Calendario o Archivio;
- ignoramento degli elementi non utili;
- spostamento controllato verso To do, Calendario o Archivio;
- schermata To do list;
- completamento e riapertura delle cose da fare;
- eliminazione dalla To do list con conferma;
- schermata Calendario;
- schermata Archivio con cartelle previste;
- persistenza locale con Room;
- navigazione inferiore: Home, Calendario, To do, Archivio;
- funzionamento offline per le funzioni essenziali.

## Struttura

```text
app/
  src/main/java/com/jurdekkers/operativo/
    data/local/
    data/repository/
    domain/model/
    ui/
    worker/
docs/
.github/workflows/android-build.yml
```

## Requisiti

- JDK 17;
- Android SDK;
- Android 6.0 o superiore per l'installazione.

## Build locale

```bash
./gradlew assembleDebug
```

Su Windows:

```powershell
.\gradlew.bat assembleDebug
```

L'APK viene creato in:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions

Il workflow:

```text
.github/workflows/android-build.yml
```

genera l'APK debug e lo pubblica come artifact:

```text
operativo-beta-debug-apk
```

## Scaricare l'APK da Actions

1. Apri il repository su GitHub.
2. Vai in **Actions**.
3. Apri l'ultima run verde di **Build Android APK**.
4. Scarica l'artifact **operativo-beta-debug-apk**.
5. Estrai lo zip.
6. Installa `app-debug.apk` sul telefono.

Se Android segnala "App non installata" durante un aggiornamento, disinstalla prima la versione precedente.

## Limiti attuali

Non sono ancora implementati:

- dettatura vocale;
- Gmail;
- PEC;
- WhatsApp automatico;
- cloud;
- AI remota;
- OCR;
- GPS operativo;
- allegati reali;
- geofencing;
- calendario esterno.

## Roadmap sintetica

- Beta 0.1: Home, Inbox, To do, Calendario, Archivio, Room, APK debug.
- Beta 0.2: voce, date, ricorrenze, notifiche locali.
- Beta 0.3: foto, documenti, condivisione da altre app, GPS.
- Beta 0.4: appunti vocali, audio, trascrizione.
- Beta 0.5: entita, eventi, pratiche, timeline.

La documentazione completa e in `docs/`.
