# Designrichtlinien

## Vision

- Registerentwicklung im Zeitalter der KI.
- KI verändert nicht die Architektur – sie verändert die Softwareentwicklung.
- KI ist kein Ersatz für Softwarearchitekten, sondern ein digitaler Kollege.
- Die eigentliche Innovation ist nicht KI, sondern industrialisierte Softwareentwicklung.

## Randbedingungen

### Architektur

- Architektur definiert den Lösungsraum.
- Ein klar definierter Lösungsraum ermöglicht reproduzierbare Softwareentwicklung.
- Architektur reduziert Komplexität durch bewusste Einschränkung technologischer Entscheidungen.
- Ein Register ist ein Self-Contained System.
- Das Gateway ist der einzige zulässige Zugriffspfad zum Register.
- Security ist Bestandteil der Architektur und keine nachträgliche Ergänzung.

### Single Source of Truth

- Das Domänenmodell ist die Single Source of Truth.
- Das Domänenmodell beschreibt die fachliche Konsistenz
- Das Domänenmodell beschreibt das Schema in der Datenbank.
- Aus dem Domänenmodell entstehen alle technischen Artefakte.
- Ein Domänenmodell – viele Artefakte.
- Das Domänenmodell wird durch JPA und Liquibase implementiert.

### CQRS

- CQRS trennt Änderungen von Abfragen.
- CRUD-Endpunkte für Commands.
- GET-Endpunkte für einfache Queries.
- GraphQL-Operationen für komplexe Queries.

### Agenten

- Agenten implementieren keine Architekturentscheidungen.
- Agenten führen Spezifikationen reproduzierbar aus.
- Agenten sind digitale Kollegen, keine autonomen Entwickler.
- Der Mensch entscheidet – der Agent setzt um.
- Der Agent rät nicht, sondern arbeitet nach Spezifikation.
- Fehlt eine Spezifikation, wird nicht implementiert.

### Skills

- Skills sind wiederverwendbare Engineering-Prozesse.
- Skills standardisieren die Zusammenarbeit zwischen Mensch und KI.
- Jeder Skill prüft Vorbedingungen, implementiert Änderungen, erzeugt Tests und aktualisiert die Dokumentation.

### Spezifikationen

- Spezifikationen definieren die Umsetzung.
- Spezifikationen ersetzen keine Architektur, sondern konkretisieren sie.
- Qualität entsteht durch präzise Spezifikationen.
- Tests und Dokumentation sind Bestandteil der Spezifikation.
- Neue Lösungen entstehen zunächst experimentell und werden anschließend als Spezifikation dokumentiert.

### Qualität

- Qualität entsteht nicht zufällig.
- Qualität entsteht durch reproduzierbare Prozesse.
- Qualität ist das Ergebnis von Architektur, Spezifikationen und Tests.
- Die Testpyramide ist Bestandteil jeder Änderung.
- Dokumentation ist Teil des Entwicklungsprozesses, nicht dessen Ergebnis.

### Monorepo

- Code, Tests, Dokumentation, Spezifikationen und Skills werden gemeinsam versioniert.
- Das Monorepo bildet die gemeinsame Wissensbasis für Menschen und Agenten.

## Architekturentwurf

Die Software realisiert ein Register, eine Datensenke für sensible und besonders schützenswerte Daten.
Die Daten im Backend (Spring Boot mit Spring Data JPA, Spring Data REST, Spring GraphQL.m Spring Security und QueryDSL) mit relationaler Datenbank (Postgresql) sollen durch einen Anti-Corruption-Layer (Quarkus, OpenAPI) geschützt eigenen und fremden digitalen Services zur Verfügung gestellt werden.
Digitale Service sind beliebige Anwendungen, die je nach Schnittstellenvertrag rollenbasierten Zugriff auf die Daten im Register bekommen.
Die Zugriffspfade sind durch die OpenAPI-Spezifikation festgelegt.

```
┌──────────────────────────────┐
| Digitale Services            |
└──────────────────────────────┘
              │
          OIDC,HTTPS
              │
┌──────────────────────────────┐
│ Gateway                      │
│ • OpenAPI                    │
│ • Validierung                │
│ • Adaption                   │
│ • Caching                    │
└──────────────────────────────┘
               │
             mTLS
               │
┌──────────────────────────────┐
| Self-Contained System        | 
│ • Datenschutz                │
│ • Sicherheit                 │
│ • Konsistenz                 │
|   ┌──────────────────────┐   |
|   │ Backend              │   |
|   └──────────────────────┘   |
|              │               |
|            mTLS              |
|              │               |
|   ┌──────────────────────┐   |
|   │ Datenbank            │   |
|   └──────────────────────┘   |
└──────────────────────────────┘
```

### Register-Backend

Das Register-Backend ist für die fachliche Konsistenz, Persistenz und effiziente Bereitstellung der Registerdaten verantwortlich.
Sicherheitsaspekte der externen Kommunikation, Stabilisierung der Schnittstellen und Integrationsaufgaben werden vom Gateway übernommen.
Das Register-Backend ist darauf optimiert,

- möglichst wenig handgeschriebenen Code zu benötigen,
- eine hohe Entwicklungsproduktivität zu erreichen,
- wiederkehrende Implementierungen zu automatisieren,
- horizontale Skalierung zu ermöglichen,
- große Datenbestände effizient zu verwalten,
- komplexe Leseabfragen performant auszuführen.

Das Register-Backend folgt dem Prinzip Command Query Responsibility Segregation (CQRS).
Mutationen erfolgen ausschließlich über REST-Endpunkte.
Einfache Leseoperationen verwenden GET-Endpunkte, konplexe Leseoperation verwenden GraphQL.
GraphQL ermöglicht

- entityübergreifende Abfragen,
- flexible Selektion benötigter Attribute,
- effiziente Navigation über Beziehungen,
- Optimierungen für das N+1-Problem.

Das Register-Backend basiert auf JPA und einem relationalen Datenbankschema.
Das Datenbankschema wird mit Liquibase migriert.
das JPA-Domänenmodell ist die zentrale Quelle der Wahrheit:

JPA-Modell
     │
     ├────────────► Datenbankschema mit Liquibase
     │
     ├────────────► Repositories mit Spring Data JPA
     │
     ├────────────► REST-Endpunkte mit Spring Data REST
     │
     ├────────────► QueryDSL für dynamische Filter
     │
     └────────────► GraphQL mit Spring GraphQL

Dadurch reduziert sich die Menge handgeschriebenen Codes erheblich.

### Register-Gateway

Das Register-Gateway bildet den stabilen Vertrag gegenüber allen digitalen Services.
Dadurch können wir das Backend unabhängig weiterentwickeln – vom relationalen Modell bis zu den internen REST- und GraphQL-Endpunkten.
Gleichzeitig ist das Gateway vollständig zustandslos und horizontal skalierbar.
Caching im Gateway reduziert Last auf Backend und Datenbank und verbessert die Antwortzeiten.

Das Register-Gateway ist der einzige zulässige Zugriffspfad für digitale Services.

### Digitale Services

Das digitale Service implementiert register-übergreifende Funktionalitäten, soll aber nicht weiter detailliert werden.
Fokus liegt auf dem Register.

### Entwicklungsprozess

### Monorepo

Die Software liegt in einem Git-Monorepo.
Die Struktur ist bereits festgelegt.
Siehe https://github.com/robertbruckbauer/petclinic.

```
.
├── .agents/skills     # Agent skills
├── app/gateway        # Source for gateway server
├── app/deploy         # Source for deployments
├── app/migrate        # Source for database migrations
├── app/backend        # Source for backend server with a database
├── doc/concept        # Specifications
├── doc/manual         # Manuals
├── lib/backend-api    # Source for the JPA data model of the backend server
├── lib/backend-data   # Source for the REST and GraphQL implementation of the backend server
├── AGENTS.md          # Agent settings
├── PROMPT.adoc        # Prompt engineering help
├── README.adoc        # Build management help
└── VERSION            # Version of this application
```

### Agenten

> Der Agent soll nicht erraten, wie etwas implementiert wird.
Er soll eine freigegebene Spezifikation reproduzierbar anwenden.

Die Softare soll KI-gestützt entwickelt werden.

```
Prompt
  |
  v
Skill auswählen
  |
  v
Vorbedingungen prüfen
  |-- nicht erfüllt --> Abbruch und Klarstellung anfordern
  v
Spezifikation laden
  |-- nicht gefunden --> Abbruch und Experiment anfordern
  v
Implementierung erstellen
  |
  v
Tests gemäß Testpyramide erstellen
  |
  v
Dokumentation aktualisieren
  |
  v
Tests ausführen
  |-- nicht erfolgreich -> Abbruch und Fehlerbehebung vorschlagen
  v
Pull-Request erstellen
```

Der Agent wird durch eine zentrale AGENTS.md und durch Skills gesteuert.
Die Skills sind für den Agente konkrete Checklisten mit Vorbedingungen.
In den einzelnen Aufgaben der Checkliste werden sehr spezifische Spezifikationen als Vorgaben für die Implementierung referenziert.
Die Spezifikationen beschreiben eine konkrete Änderung, z.B. das Anlegen einer neuen Entität mit Primärschlüssel, Versionierung für optimistische Locking oder das Hinzufügen eines Feldes mit einem bestimmten Typ (boolean, numerisch, Aufzählung, String, LocalDate, LocalTime, Duration) oder einer Relation (ElementCollection, OneToOne, OneToMany, ManyToOne, uni- oder bidirektional, ManyToMany).Ziel dieser Skills ist es, die Aufgaben sehr klar und kleinteilig zu machen, um so wenig Kontext wie möglich für ihre Ausführung durch den Agenten zu verbrauchen.

### Spezifikationen

Spezifikationen sollen nicht nur die eigentliche Implementierung festlegen, sondern auch Tests in eine ausbalanzierten Testpyramide und die Struktur der technische Dokumentation vorgeben.

Spezifikationen definieren eine ausbalancierte Testpyramide.

```
End-to-End-Tests
  Playwright, wenige Tests, basierend auf den wichtigsten Use Cases
API-Tests
  Playwright, alle Endpunkte
Slice-Tests
  Spring Boot Test, alle Endpunkte, alle Datenbanktabellen
Unit-Tests
  JUnit, Mockito, alle JPA-Entitäten, alle Methoden, alle Felder
```

Spezifikationen sind selbst ist Teil der technischen Dokumentation und wird ergänzt um eine Dokumentation der REST-Endpunkte und GraphQL-Operationen.

Spezifikationen sind in Asciidoc.
Die Vorteile von Asciidoc gegenüber Markdown werden später geklärt.

Spezifikationen sind notwendig für die Wiederholbarkeit der Ausführung (jedes Feld eines bestimmten Typs wird immer gleich implementiert), die hohe Qualität der Ergebnisse und die Reduzierung von Halluzinationen.
Fehlt für einen Typ oder eine Relation eine Spezifikation, dann muss der Agent abbrechen.
Die neue Spezifikation muss experimentell implementiert werden, daraus leiten sich Regeln und Beispiele für die Implementierung, die Einordnung in die Testpyramide und die technische Dokumentation ab.
