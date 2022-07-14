# Koncert szervező/nyilvántartó applikáció

Az applikáció célja, hogy összegyüjtsük a koncertet adó helyiségeket és előadókat a könnyebb program szervezés 
érdekében. Az alkalmazás események kezelésére és meghírdetésére is alkalmas, amely gyors és informatív elérést 
biztosít a közönség számára, bevonzva ezzel az érdeklődő zenét kedvelő embereket.


## Az alkalmazás főbb egységei
### Music venue:
Zenés produkcióknak helyet adó helyszínek: Név, telefonszám, cím, befogadóképesség, hely típus, események.
### Performer:
Előadók, zenekarok: Név, email, telefonszám, műfaj, partner támogatottsági szint, események (kapcsoló táblával).
### Event:
Események, koncertek. Egy esemény mindig kell tartalmazzon egy headlinert, hogy meg lehessen hírdetni a 
közönség számára: Név, dátum, meghírdetve, ár, eladott jegyek száma, helyszín, fellépők (kapcsoló táblával), 
eseményre jegyet váltók.
### Participant:
Eseményre jegyet váltók: Név, email, esemény.
### PerformersAtEvents:
Kapcsoló tábla a több-többes kapcsolat feloldására. Egy elődadónak több eseménye is lehet, illetve egy eseménynek 
is lehet több előadója: Esemény, előadó, headliner, regisztrálás időpontja.
### Enumok:
- GenreType: Előadó műfaja (pl.: Rock, Punk, Jazz stb.)
- PartnerLevel: Előadó partner együttműkődési szintje. Öt szintre leoszvta, előadók hírességük 
függvényében változhat. Befolyásolja az esemény árát.
- VenueType: Helyszínek típusa (pl.: Klub, Aréna, stadion stb.)
minnél jobb a több-többes kapcsolat feloldására. Egy elődadónak több eseménye is lehet, illetve egy eseménynek
is lehet több előadója: Cím, leírás, napi ára, pontos lokáció, és az utazási ügynökség.


## Endpointok
### Event
- mentés: Esemény mentése
- listázás: Összes jövőbeli esemény kilistázása
- listázás: Esemény ID alapján kilistázása
- mentés: Előadó hozzáadása eseményhez
- törlés: Előadó kitörlése eseményről, ha headliner az előadó csak törölni vagy módosítani lehet a dátumát
- módosítás: Esemény dátumának megváltoztatása
- törlés: Esemény törlése

### MusicVenue
- mentés: Helyszín mentése
- listázás: Összes helyszín kilistázása
- listázás: Helyszín ID alapján kilistázása
- törlés: Esemény törlése, amennyiben, meghírdetett jövőbeli eseményeivel

### Participant
- mentés: Eseményen résztvevő mentése
- listázás: Adott eseményre jegyet váltók listázása

### Performer
- mentés: Előadó mentése
- listázás: Összes előadó kilistázása
- listázás: Előadó ID alapján kilistázása
- törlés: Előadó törlése, jövőbeli eseményekről törlése, ha headliner jövőbeli esemény törlése is


### Az alkalmazás és adatbázis konténerben való futtatásához készült segédlet: 
 [docker-cheat-sheet](docker-cheat-sheet.md)

### Egyéb segédlet:
[UML diagram](uml.png)

[Adatbázis diagram](database_diagram.PNG)



------------------------------------------------------------------------------------------------------------------------

# Vizsga projekt

A feladatod egy backend API projekt elkészítése, általad választott témában.  
A témákhoz összeszedtünk néhány ötletet, kérlek írd be magad ahhoz a témához, amit te választanál. Érdemes mindenkinek egyedi alkalmazást készíteni, próbáljatok meg osztozkodni a témákon.  
Nem csak ezek közül a témák közül lehet választani, ha saját ötleted van, akkor nyugodtan írd hozzá a listához.

[témaötletek](https://docs.google.com/document/d/1ct21ZzbqeV0_Zvw_0k_dwjtEQVKa7aLqE49pB1Uq1eI/edit?usp=sharing)

## Követelmények

* Maven projekt
* Spring Boot alkalmazás
* REST API, Swagger, OpenAPI dokumentáció
* SQL backend (pl. MySQL, MariaDB)
* Flyway sémamigráció, SQL táblalétrehozás, adatbetöltés
* Hibakezelés
* Spring Data JPA repository
* Integrációs tesztek
* Konténerizált alkalmazás

## Feladat nagysága

* Legalább két 1-n kapcsolatban lévő tábla
* Legalább két SQL migráció
* Legalább két entitás
* Legalább két controller
* Minden bemenő paraméter validálása
* Legalább egy property beolvasása
* Minden HTTP metódusra legalább egy végpont (`GET`, `POST`, `PUT`, `DELETE`)
* Legalább 60%-os tesztlefedettség, amely tartalmaz egység és integrációs teszteket is
* Egy `Dockerfile`
