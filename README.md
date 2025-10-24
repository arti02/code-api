# coding

### struktura
- 3-warstwowa
- Controller - przyjmowanie zapytań i wypluwanie odpowiedzi
- Service - logika biznesowa
- Repository - połączenie z bazą danych
- każde entity będzie miało swoje 3-warstwowe flow
- Nauczyciel (id, imie, nazwisko, lista jezykow)
- Kursant (id, imie, nazwisko, jezyk)
- Lekcja (id, Kursant, Nauczyciel, termin)
- testy jednostkowe( service)
- N+1 problem
- race condition
- DTOs rone przyjmowanie i opdowiedzi(command)
- mappery
- return ResponseEntity

### funkcjonalności
- wylistowanie nauczycieli
- wylistowanie kursantów
- wylistowanie lekcji
- dodawanie nauczyciela (dodając nauczyciela, chcemy mieć możliwość wybrania kilku języków jednocześnie)
- dodawanie kursanta (wybór nauczyciela z listy dostępnych - nie pozwalany na przypisanie nauczyciela, który nie uczy danego języka) 
- dodawanie lekcji (nie pozwalamy na zaplanowanie lekcji w przeszłości && nie pozwalamy na zaplanowanie lekcji w terminie, który będzie się pokrywał z inna lekcją danego nauczyciela)
- usuwanie nauczyciela (soft delete)
- usuwanie kursanta (soft delete) 
- usuwanie lekcji (nie usuwamy lekcji, która już się zaczęła) 
- zmiana terminu lekcji (nie pozwalamy na przypisanie terminu, który jest niedostępny dla nauczyciela, ani nie pozwalamy na zaplanowanie jej w przeszłości)
- zmiana nauczyciela dla kursanta (walidujemy/sprawdzamy język)
- ezyki enum
- versions for optimistic locking
- active not active for records
- java version 23 of projekt  problem z Lombockiem
- baza danych docker
