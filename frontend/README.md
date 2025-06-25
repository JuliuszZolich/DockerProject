# Dietetyk+ - Aplikacja do Zarządzania Dietą

## Spis Treści
1. [Opis Projektu](#opis-projektu)
2. [Kluczowe Funkcjonalności](#kluczowe-funkcjonalności)
    - [Dla Pacjentów (Użytkowników)](#dla-pacjentów-użytkowników)
    - [Dla Dietetyków](#dla-dietetyków)
    - [Funkcje Ogólne](#funkcje-ogólne)
3. [Stos Technologiczny](#stos-technologiczny)
4. [Struktura Projektu](#struktura-projektu)
5. [Uruchomienie Projektu](#uruchomienie-projektu)
6. [Szczegółowy Opis Plików](#szczegółowy-opis-plików)

## Opis Projektu

**Dietetyk+** to kompleksowa aplikacja internetowa stworzona w technologii React, która służy jako platforma do zarządzania dietami. Aplikacja jest przeznaczona dla dwóch głównych typów użytkowników: **pacjentów**, którzy chcą śledzić swoje diety i postępy, oraz **dietetyków**, którzy tworzą spersonalizowane plany żywieniowe i zarządzają swoimi podopiecznymi.

Projekt cechuje się rozbudowanym interfejsem użytkownika, logiką biznesową oddzieloną od warstwy prezentacji oraz komunikacją z backendowym API w celu pobierania i zapisywania danych.

## Kluczowe Funkcjonalności

Aplikacja oferuje szeroki wachlarz funkcji, dostosowanych do ról użytkowników.

### Dla Pacjentów (Użytkowników)

*   **Przeglądanie Planu Diety:** Dostęp do szczegółowego, 7-dniowego planu żywieniowego z podziałem na posiłki, przepisy i składniki.
*   **Generowanie Listy Zakupów:** Automatyczne tworzenie listy zakupów na podstawie planu diety, z podziałem na dni i kategorie produktów.
*   **Dziennik Postępów:** Możliwość wprowadzania i wizualizacji danych dotyczących wagi, poziomu glukozy, ciśnienia i pulsu na interaktywnych wykresach.
*   **Wyszukiwanie i Przypisywanie Dietetyka:** Użytkownicy mogą przeglądać profile dietetyków i przypisać się do wybranego specjalisty.
*   **Zarządzanie Ustawieniami Konta:** Edycja danych osobowych, zmiana hasła oraz avatara.

### Dla Dietetyków

*   **Zarządzanie Pacjentami:** Przeglądanie listy swoich pacjentów, dostęp do ich szczegółowych danych (medycznych, kontaktowych) oraz postępów.
*   **Kreator Dań:** Zaawansowane narzędzie do tworzenia nowych dań, definiowania ich składników, wartości makroodżywczych, przepisu oraz dodawania zdjęcia.
*   **Kreator Diet:** Możliwość tworzenia kompletnych, 7-dniowych planów dietetycznych poprzez komponowanie posiłków z wcześniej zdefiniowanych dań.
*   **Przypisywanie i Modyfikacja Diet:** Możliwość przypisania pacjentowi gotowej diety lub stworzenia i przypisania spersonalizowanego planu.
*   **Zarządzanie Profilem Dietetyka:** Możliwość edycji danych osobowych oraz dodania opisu widocznego dla pacjentów.

### Funkcje Ogólne

*   **System Autentykacji:** Pełny proces rejestracji (wielokrokowy), logowania i odzyskiwania hasła.
*   **Generowanie Plików PDF:** Zarówno plany dietetyczne, jak i listy zakupów mogą być eksportowane do formatu PDF.
*   **Dostęp Oparty na Rolach (RBAC):** Interfejs i dostępne funkcje dynamicznie dostosowują się w zależności od tego, czy zalogowany jest pacjent, czy dietetyk.
*   **Interaktywny Interfejs:** Aplikacja wykorzystuje animacje przy przewijaniu, modale, rozwijane listy i dynamiczne komponenty, aby poprawić doświadczenie użytkownika.
*   **Walidacja Formularzy:** Logika walidacji po stronie klienta zapewnia poprawność wprowadzanych danych w formularzach rejestracji, logowania i innych.

## Stos Technologiczny

*   **Framework:** [React.js](https://reactjs.org/)
*   **Bundler:** [Vite](https://vitejs.dev/)
*   **Routing:** [React Router DOM](https://reactrouter.com/)
*   **Wizualizacja Danych:** [Recharts](https://recharts.org/)
*   **Generowanie PDF:** [jsPDF](https://github.com/parallax/jsPDF) & [jsPDF-AutoTable](https://github.com/simonbengtsson/jsPDF-AutoTable)
*   **Obsługa Ciasteczek:** [react-cookie](https://www.npmjs.com/package/react-cookie)
*   **Styling:** CSS (bezpośrednio, bez preprocesorów)
*   **Linting:** [ESLint](https://eslint.org/)

## Struktura Projektu

Projekt ma logicznie zorganizowaną strukturę plików, która ułatwia nawigację i rozwój.

```
/
├── public/                  # Statyczne zasoby
├── src/
│   ├── assets/              # Komponenty UI, hooki i inne zasoby
│   │   ├── elements/        # Główne komponenty (np. nawigacja, formularze)
│   │   ├── hooks/           # Niestandardowe hooki (np. do obsługi obrazów)
│   │   └── ConnectionProvider.jsx # Kontekst do zarządzania stanem połączenia
│   ├── data/                # Statyczne dane, mocki, stałe (np. opcje do selectów)
│   ├── images/              # Obrazy i ikony używane w aplikacji
│   ├── pages/               # Główne komponenty renderowane jako strony
│   ├── scripts/             # Logika biznesowa, funkcje pomocnicze
│   │   ├── getData/         # Funkcje do pobierania danych z API (GET)
│   │   ├── sendData/        # Funkcje do wysyłania danych do API (POST, DELETE)
│   │   ├── validateData/    # Funkcje walidacyjne
│   │   └── ...              # Inne skrypty (np. generowanie PDF, obliczenia)
│   ├── style/               # Globalne i specyficzne dla komponentów pliki CSS
│   ├── main.jsx             # Punkt wejściowy aplikacji, konfiguracja routingu
│   └── ...
├── .eslintrc.config.js      # Konfiguracja ESLint
├── index.html               # Główny plik HTML
├── package.json             # Zależności i skrypty projektu
└── vite.config.js           # Konfiguracja Vite (m.in. proxy do backendu)
```

## Uruchomienie Projektu

Aby uruchomić projekt lokalnie, postępuj zgodnie z poniższymi krokami:

1.  **Sklonuj repozytorium:**
    ```bash
    git clone <adres-repozytorium>
    cd <nazwa-folderu-projektu>
    ```

2.  **Zainstaluj zależności:**
    ```bash
    npm install
    ```
    lub
    ```bash
    yarn install
    ```

3.  **Uruchom serwer backendowy:**
    Ta aplikacja frontendowa wymaga działającego serwera backendowego. Upewnij się, że serwer API jest uruchomiony i dostępny pod adresem `http://localhost:8081` (zgodnie z konfiguracją proxy w `vite.config.js`).

4.  **Uruchom serwer deweloperski Vite:**
    ```bash
    npm run dev
    ```
    Aplikacja będzie dostępna pod adresem `http://localhost:3000`.

## Szczegółowy Opis Plików

### Główne Komponenty i Strony (`src/pages`)
*   `HomePage.jsx`: Strona główna z dynamicznymi slajdami i przeglądem oferty.
*   `Register.jsx`: Wielokrokowy formularz rejestracyjny z walidacją na każdym etapie.
*   `Login.jsx`: Strona logowania.
*   `Creator.jsx`: Kluczowa strona dla dietetyków, zawierająca kreator dań i diet.
*   `DietPlanPage.jsx`: Strona dla pacjenta, wyświetlająca jego plan diety.
*   `ShoppingList.jsx`: Strona generująca listę zakupów na podstawie diety.
*   `ProgressJournal.jsx`: Strona z wykresami do śledzenia postępów pacjenta.
*   `DietitianPatientsPage.jsx`: Strona dla dietetyka do zarządzania pacjentami.

### Logika i Skrypty (`src/scripts`)
*   `getData/`: Zbiór asynchronicznych funkcji `fetch` do pobierania danych (np. `getPatientsData.js`, `getAllDiets.js`).
*   `sendData/`: Funkcje do wysyłania żądań `POST`, `DELETE` (np. `sendDietPlanData.js`, `sendMealData.js`).
*   `validateData/`: Moduły zawierające funkcje walidujące dane z formularzy (np. `validateRegisterUtils.js`, `validateUserSettingsUtils.js`).
*   `generatePDF.js`, `generateShoppingListPDF.js`: Funkcje wykorzystujące `jsPDF` do tworzenia plików PDF.
*   `countMacros.js`: Logika obliczająca sumaryczne wartości makroskładników na podstawie listy składników.
*   `shoppingListUtils.js`: Funkcja do agregowania składników z całego planu diety w celu stworzenia listy zakupów.

### Dane Statyczne (`src/data`)
*   `dietPlanDataUser.js`: Przykładowy, rozbudowany plan diety na 7 dni. Służy jako wzór struktury danych.
*   `ingredients.js`: Lista predefiniowanych składników z ich makroskładnikami.
*   `EmptyListsData.js`: "Puste" obiekty używane do inicjalizacji stanu w komponentach (np. `emptyDiet`, `emptyMeal`).
*   `SelectOptionsData.js`: Stałe używane do wypełniania list rozwijanych (np. kategorie posiłków, jednostki).

### Niestandardowe Hooki (`src/assets/hooks`)
*   `useImageUploader.jsx`: Abstrakcja logiki przesyłania obrazów (z obsługą przeciągnij i upuść).
*   `OnClickOutsideWindow.jsx`: Hook do wykrywania kliknięć poza określonym elementem (np. do zamykania modali).
*   `OnShowElement.jsx`: Wykorzystuje `IntersectionObserver` do animowania elementów, gdy pojawią się w widoku.