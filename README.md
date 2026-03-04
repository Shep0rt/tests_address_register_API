# API Tests

Проект автотестов API Address Register Service (Java + JUnit5 + Rest Assured + Allure).

## Что нужно

- JDK 17
- URL стенда
- API key (`X-API-KEY`)

## Быстрый запуск (без очистки результатов прошлого запуска)

### 1. Smoke + web отчет

```powershell
.\gradlew.bat --continue smokeTest allureServe "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
```

### 2. Smoke + html отчет

```powershell
.\gradlew.bat --continue smokeTest allureReport "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
Start-Process ".\build\reports\allure-report\index.html"
```

### 3. Все тесты + web отчет

```powershell
.\gradlew.bat --continue test allureServe "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
```

### 4. Все тесты + html отчет

```powershell
.\gradlew.bat --continue test allureReport "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
Start-Process ".\build\reports\allure-report\index.html"
```

## Чистый запуск (с очисткой результатов прошлого запуска)

### 1. Smoke + web отчет

```powershell
.\gradlew.bat --continue clean smokeTest allureServe "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
```

### 2. Smoke + html отчет

```powershell
.\gradlew.bat --continue clean smokeTest allureReport "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
Start-Process ".\build\reports\allure-report\index.html"
```

### 3. Все тесты + web отчет

```powershell
.\gradlew.bat --continue clean test allureServe "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
```

### 4. Все тесты + html отчет

```powershell
.\gradlew.bat --continue clean test allureReport "-Dapi.base-url=http://<HOST>:<PORT>" "-Dapi.x-api-key=<API_KEY>"
Start-Process ".\build\reports\allure-report\index.html"
```

## Если тесты не запускаются

- Проверь `api.base-url` (с `http://` или `https://`)
- Проверь, что стенд доступен с твоей машины
- Проверь API key
