# Dietetyk Plus – Full Stack App (React + Spring Boot + Python + Redis)

Projekt składa się z aplikacji frontendowej w React, backendu w Spring Boot, serwera API w Pythonie oraz bazy danych Redis. Wszystkie komponenty uruchamiane są jako kontenery Dockera, z wykorzystaniem NGINX jako reverse proxy / load balancera.

---

## 📦 Struktura projektu

```

dietetyk-plus/
├── frontend/            # React (Vite) App
├── backend/             # Spring Boot App (Java)
├── ai/       # Serwer API w Pythonie (FastAPI)
├── nginx/
│   └── nginx.conf       # Konfiguracja NGINX
├── docker-compose.yml   # Docker Compose config
└── README.md

```

---

## ⚙️ Wymagane zmiany przed uruchomieniem

### 🔑 1. Python – dodaj klucz API w `main.py`
##Klucz można pozyskać ze strony [AIStudio](https://aistudio.google.com/apikey)
W pliku:

```

ai/main.py

````

Dodaj tutaj:

```python
API_KEY = "DODAĆ KLUCZ TUTAJ"
````

---

### 🔑 2. Spring Boot – dodaj konfigurację w `application.properties`

W pliku:

```
backend/src/main/resources/application.properties
```

Dodaj wymagany klucz [courier]([https://www.courier.com):

```properties
spring.application.name=Dietetyk_Plus
server.port=8081
spring.data.redis.host=localhost
spring.data.redis.port=6379
courier.api_key=DODAĆ_TUTAJ_KLUCZ
```

---

## 🚀 Uruchomienie projektu

1. Zbuduj i uruchom wszystkie kontenery:

```bash
docker-compose up --build
```

2. Aplikacja React będzie dostępna pod adresem:

```
http://localhost:80
```

---

## ♻️ Reset / przebudowa

Aby usunąć wszystkie kontenery i dane:

```bash
docker-compose down -v --remove-orphans
```

Aby wymusić przebudowanie:

```bash
docker-compose up --build
```

---

## 📌 Dostępność usług (tylko wewnętrznie)

| Usługa      | Port wewnętrzny | Opis                    | Dostęp z zewnątrz |
| ----------- | --------------- | ----------------------- | ----------------- |
| React       | 3000            | Frontend (Vite)         | ❌ (przez NGINX)   |
| Spring Boot | 8081            | Backend                 | ❌                 |
| Python API  | 4000            | Serwer API              | ❌                 |
| Redis       | 6379            | Baza danych (key-value) | ❌                 |
| NGINX       | 80              | Load balancer + proxy   | ✅                 |

---

## 🐳 Docker Compose – skrót działania

* `frontend`: Vite React App, serwowana przez NGINX
* `spring_server`: Spring Boot API
* `python_server`: API w Pythonie (FastAPI / Flask)
* `redis`: baza danych Redis
* `nginx`: reverse proxy, który udostępnia frontend na zewnątrz

---

## 🛠️ Przykładowe polecenia developerskie

Uruchomienie tylko jednego kontenera do debugowania:

```bash
docker-compose up frontend
docker-compose up spring_server
docker-compose up python_server
```

Wejście do środka kontenera:

```bash
docker exec -it spring_server sh
```

---

## 📫 Kontakt / rozwój

Możesz rozwijać projekt dodając autoryzację, system użytkowników, zapisy do bazy danych i połączenia z zewnętrznymi usługami.

---
