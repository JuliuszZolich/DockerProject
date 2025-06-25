### Dokumentacja Endpointów API

#### **POST /api/register**
Rejestruje nowego użytkownika w bazie danych Redis.

- **Nagłówki:** Brak
- **Body (JSON):**
  ```json
  {
    "name": "string",
    "surname": "string",
    "img_b64": "string|null",
    "birthdate": "yyyy-MM-dd",
    "phone": "string",
    "email": "string",
    "password": "string",
    "activityLevel": "int",
    "dietRating": "int",
    "mealsCount": "string",
    "jobType": "string",
    "dietPurpose": "string",
    "dieteticId": "string|null",
    "currentDietId": "string|null",
    "medicalData": {
      "height": "string",
      "weight": "string",
      "gender": "boolean",
      "diseases": ["string"],
      "allergies": ["string"],
      "journal": []
    }
  }
  ```
- **Odpowiedź (200 OK):**
  ```json
  "Dane zapisane dla klucza: {UUID}"
  ```

---

#### **POST /api/checkEmail**
Sprawdza, czy podany email jest już zajęty.

- **Nagłówki:** Brak
- **Body (JSON):**
  ```json
  "email@example.com"
  ```
- **Odpowiedź (200 OK):**
  ```json
  "Email dostępny"
  ```
- **Odpowiedź (409 Conflict):**
  ```json
  "Email już zajęty"
  ```

---

#### **POST /api/login**
Loguje użytkownika na podstawie podanych danych.

- **Nagłówki:** Brak
- **Body (JSON):**
  ```json
  {
    "email": "string",
    "password": "string"
  }
  ```
- **Odpowiedź (200 OK):**
  Zwraca dane użytkownika oraz nagłówek `User-Key`.
- **Odpowiedź (401 Unauthorized):**
  ```json
  "Niepoprawne dane logowania"
  ```
- **Odpowiedź (404 Not Found):**
  ```json
  "Nie znaleziono użytkownika"
  ```

---

#### **GET /api/update**
Aktualizuje dane użytkownika.

- **Nagłówki:**
    - `User-Key: {UUID}`
- **Body (JSON):**
  Dane użytkownika w formacie JSON (jak w `/api/register`).
- **Odpowiedź (200 OK):**
  ```json
  "Dane zaktualizowane dla klucza: {UUID}"
  ```
- **Odpowiedź (401 Unauthorized):**
  ```json
  "Niepoprawne dane"
  ```

---

#### **GET /api/logout**
Wylogowuje użytkownika.

- **Nagłówki:**
    - `Authorization: {token}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  ```json
  "Wylogowano pomyślnie"
  ```

---

#### **POST /api/verify**
Weryfikuje token resetowania hasła i email, a następnie generuje nowy token autoryzacyjny.

- **Nagłówki:** Brak
- **Body (JSON):**
  ```json
  {
    "token": "string",
    "email": "string"
  }
  ```
- **Odpowiedź (200 OK):**
  ```json
  "Token zweryfikowany"
  ```
  (W odpowiedzi serwer dodaje nagłówek `Authorization` z nowym tokenem.)
- **Odpowiedź (401 Unauthorized):**
  ```json
  "Niepoprawny token"
  ```
  lub
  ```json
  "Niepoprawne dane logowania"
  ```

---

#### **POST /api/resetPassword**
Resetuje hasło użytkownika po pomyślnej weryfikacji tokenu.

- **Nagłówki:**
  - `Authorization: {token}` (token uzyskany z endpointu `/api/verify`)
- **Body (JSON):**
  ```json
  "nowe_haslo_uzytkownika"
  ```
- **Odpowiedź (200 OK):**
  Zwraca zaktualizowane dane użytkownika (obiekt `User`).
- **Odpowiedź (401 Unauthorized):**
  ```json
  "Niepoprawny token"
  ```
  lub
  ```json
  "Niepoprawne dane logowania"
  ```

---

#### **POST /api/checkRecoveryEmail**
Sprawdza, czy podany email istnieje w systemie i inicjuje proces odzyskiwania hasła (wysyła kod).

- **Nagłówki:** Brak
- **Body (JSON):**
  ```json
  "email@example.com"
  ```
- **Odpowiedź (200 OK):**
  ```json
  "Kod został wysłany na email"
  ```
- **Odpowiedź (409 Conflict):**
  ```json
  "Email nie istnieje"
  ```
- **Odpowiedź (500 Internal Server Error):**
  ```json
  "Nie można wysłać kodu"
  ```

---

#### **GET /api/dietitians**
Pobiera listę wszystkich dietetyków.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Lista obiektów reprezentujących dietetyków.
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli lista jest pusta lub wystąpił błąd.

---

#### **GET /api/patients**
Pobiera listę pacjentów przypisanych do zalogowanego dietetyka.

- **Nagłówki:**
  - `Authorization: {userKey}` (klucz API dietetyka)
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Lista obiektów reprezentujących pacjentów.
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli lista jest pusta lub wystąpił błąd.

---

#### **POST /api/update**
Aktualizuje dane użytkownika. (Poprawka dla istniejącego wpisu w README - metoda to POST, nagłówek to Authorization)

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Dane użytkownika w formacie JSON (jak w `/api/register`).
- **Odpowiedź (200 OK):**
  ```json
  "Dane zaktualizowane dla klucza: {UUID}"
  ```
- **Odpowiedź (401 Unauthorized):**
  ```json
  "Niepoprawne dane"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```

---

#### **GET /api/update/dietetic/{type}/{id}**
Zarządza przypisaniem dietetyka do użytkownika lub pacjenta do dietetyka.

- **Ścieżka:**
  - `{type}`: typ operacji (`add` lub `remove`)
  - `{id}`: UUID użytkownika (pacjenta) lub dietetyka, zależnie od kontekstu i uprawnień.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedzi:**
  - **Dla `type="add"`:**
    - **200 OK:** `"Dietetyk dodany dla klucza: {userId}"`
    - **403 Forbidden:** `"Brak uprawnień do tej operacji"`
    - **401 Unauthorized:** `"Niepoprawne dane"`
  - **Dla `type="remove"`:**
    - **200 OK:** `"Dietetyk usunięty dla klucza: {patientId}"`
    - **403 Forbidden:** `"Brak uprawnień do tej operacji"`
    - **401 Unauthorized:** `"Niepoprawne dane"`
  - **Dla innego `type`:**
    - **400 Bad Request:** `"Niepoprawny typ operacji"`

---

#### **GET /api/meals**
Pobiera listę wszystkich posiłków.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Lista obiektów `Meal`.
  ```json
  [
    {
      "img_b64": "string|null",
      "title": "string",
      "description": "string",
      "ingredients": {
        "ingredient_uuid_1": "quantity_1",
        "ingredient_uuid_2": "quantity_2"
      }
    }
    // ... inne posiłki
  ]
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli lista jest pusta lub wystąpił błąd.

---

#### **GET /api/meal/{id}**
Pobiera dane konkretnego posiłku.

- **Ścieżka:**
  - `{id}`: UUID posiłku.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Obiekt `Meal`.
  ```json
  {
    "img_b64": "string|null",
    "title": "string",
    "description": "string",
    "ingredients": {
      "ingredient_uuid_1": "quantity_1",
      "ingredient_uuid_2": "quantity_2"
    }
  }
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli posiłek o podanym ID nie istnieje.

---

#### **POST /api/meal**
Dodaje nowy posiłek.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Obiekt `Meal`.
  ```json
  {
    "img_b64": "string|null",
    "title": "string",
    "description": "string",
    "ingredients": {
      "ingredient_uuid_1": "quantity_1",
      "ingredient_uuid_2": "quantity_2"
    }
  }
  ```
- **Odpowiedź (200 OK):**
  ```json
  "Dane zapisane dla klucza: {newUUID}"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```

---

#### **DELETE /api/meal/{id}**
Usuwa posiłek o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID posiłku.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  ```json
  "Dane usunięte dla klucza: {id}"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```

---

#### **GET /api/ingredient**
Pobiera listę wszystkich składników.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Lista obiektów `Ingredient`.
  ```json
  [
    {
      "img_b64": "string|null",
      "title": "string",
      "unit": "string",
      "categoryId": "int",
      "macros": {
        "calories": "double",
        "protein": "double",
        "carbs": "double",
        "sugar": "double",
        "fat": "double",
        "fiber": "double"
      }
    }
    // ... inne składniki
  ]
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli lista jest pusta lub wystąpił błąd.

---

#### **GET /api/ingredient/{id}**
Pobiera dane konkretnego składnika.

- **Ścieżka:**
  - `{id}`: UUID składnika.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Obiekt `Ingredient`.
  ```json
  {
    "img_b64": "string|null",
    "title": "string",
    "unit": "string",
    "categoryId": "int",
    "macros": {
      "calories": "double",
      "protein": "double",
      "carbs": "double",
      "sugar": "double",
      "fat": "double",
      "fiber": "double"
    }
  }
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli składnik o podanym ID nie istnieje.

---

#### **POST /api/ingredient**
Dodaje nowy składnik. Wartości odżywcze (`macros`) są pobierane automatycznie przez serwer.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Obiekt `Ingredient` (bez pola `macros`, które zostanie uzupełnione przez serwer).
  ```json
  {
    "img_b64": "string|null",
    "title": "string",
    "unit": "string",
    "categoryId": "int"
  }
  ```
- **Odpowiedź (200 OK):**
  ```json
  "Dane zapisane dla klucza: {newUUID}"
  ```
  (Odpowiedź będzie zawierać obiekt składnika z uzupełnionymi `macros`.)
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (500 Internal Server Error):**
  ```json
  "Nie można pobrać wartości odżywczych"
  ```

---

#### **DELETE /api/ingredient/{id}**
Usuwa składnik o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID składnika.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  ```json
  "Dane usunięte dla klucza: {id}"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
  
---

#### **GET /api/dietPlans**
Pobiera listę wszystkich planów dietetycznych.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Lista obiektów `DietPlan`.
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli lista jest pusta lub wystąpił błąd.

---

#### **GET /api/dietPlan/{id}**
Pobiera szczegóły planu diety o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID planu diety.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  Obiekt `DietPlan`.
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  Jeśli plan o podanym ID nie istnieje.

---

#### **POST /api/dietPlan**
Dodaje nowy plan diety.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Obiekt `DietPlan`.
- **Odpowiedź (200 OK):**
  ```json
  "{newUUID}"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```

---

#### **POST /api/update/dietPlan/{id}**
Aktualizuje plan diety o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID planu diety.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Obiekt `DietPlan`.
- **Odpowiedź (200 OK):**
  ```json
  "{id}"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```

---

#### **DELETE /api/delete/dietPlan/{id}**
Usuwa plan diety o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID planu diety.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  ```json
  "Dieta o ID {id} została usunięta"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```

---

#### **POST /api/update/meal/{id}**
Aktualizuje posiłek o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID posiłku.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Obiekt `Meal`.
- **Odpowiedź (200 OK):**
  ```json
  ""
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  ```json
  "Nie znaleziono posiłku o podanym ID"
  ```

---

#### **POST /api/update/ingredient/{id}**
Aktualizuje składnik o podanym ID.

- **Ścieżka:**
  - `{id}`: UUID składnika.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Obiekt `Ingredient`.
- **Odpowiedź (200 OK):**
  ```json
  ""
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  ```json
  "Nie znaleziono składnika o podanym ID"
  ```

---

#### **POST /api/journal**
Aktualizuje dziennik zdrowotny użytkownika.

- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body (JSON):**
  Tablica obiektów `Journal`.
- **Odpowiedź (200 OK):**
  ```json
  "Dziennik zaktualizowany"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  ```json
  "Nie znaleziono użytkownika"
  ```
- **Odpowiedź (500 Internal Server Error):**
  ```json
  "Nie można zaktualizować dziennika"
  ```

---

#### **GET /api/update/patient/{userid}/{dietId}**
Aktualizuje dietę pacjenta o podanym ID.

- **Ścieżka:**
  - `{userid}`: UUID pacjenta.
  - `{dietId}`: UUID planu diety.
- **Nagłówki:**
  - `Authorization: {userKey}`
- **Body:** Brak
- **Odpowiedź (200 OK):**
  ```json
  "Dieta pacjenta zaktualizowana"
  ```
- **Odpowiedź (403 Forbidden):**
  ```json
  "Brak uprawnień do tej operacji"
  ```
- **Odpowiedź (404 Not Found):**
  ```json
  "Nie znaleziono pacjenta o podanym ID"
  ```
  lub
  ```json
  "Nie znaleziono diety o podanym ID"
  ```

---
