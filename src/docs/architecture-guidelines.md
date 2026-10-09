# Guía de Arquitectura – Prototipo App Web de Turismo

**Stack:** Spring Boot · Arquitectura Hexagonal (Ports & Adapters)
**Estado:** Prototipo previo al proyecto final

---

## 1. Principios generales

- La arquitectura hexagonal trabaja únicamente con tres capas:
  - `domain`
  - `application`
  - `infrastructure`
- **Todo el software se escribe en inglés.** Nada de español ni spanglish (clases, métodos, variables, paquetes, mensajes). Esto evita problemas con la `ñ` y da un aspecto más profesional.

---

## 2. Estructura de carpetas

```
domain/
├── exceptions/
├── model/
│   └── <entity>/        (una subcarpeta por entidad)
└── ports/
    ├── repository/
    └── usecases/
application/
infrastructure/
```

> Las capas `application` e `infrastructure` quedan pendientes de detallar.

### 2.1 `domain/exceptions`

Excepciones personalizadas.

- Deben heredar de `RuntimeException`.
- Usar nombres claros y descriptivos, **aunque sean largos**.
  - Ejemplo: `UserEmailAlreadyRegisteredException`

### 2.2 `domain/model`

Clases y Enums del dominio.

- Se sugiere separar en **subcarpetas por entidad**.
- **Relaciones entre clases:** se hacen mediante **IDs (`UUID`)**, no mediante referencias a otras clases.
- Incluir **validaciones de datos** dentro del modelo.
- Incluir **métodos de comportamiento** que reduzcan la lógica en los casos de uso, por ejemplo:
  - Cambios de estado.
  - Modificación de atributos propios que requieran validaciones previas.

### 2.3 `domain/ports`

#### `repository`
Interfaces limpias con los métodos que deben implementar los adapters de persistencia.

#### `usecases`
Interfaces limpias con el/los método(s) que debe implementar cada caso de uso.

- **Convención de nombres** (cualquiera es válida):
  - `INombreUseCase`
  - `NombreUseCasePort`
- Cada caso de uso respeta el principio de responsabilidad única (ver nota abajo).
- Lo ideal es que tengan **un solo método llamado `handle`**.

---

## 3. Resumen rápido (checklist)

- [ ] Todo el código en inglés.
- [ ] Solo `domain`, `application` e `infrastructure`.
- [ ] Excepciones extienden `RuntimeException` con nombres claros.
- [ ] Modelo con subcarpeta por entidad.
- [ ] Relaciones entre entidades por `UUID`, no por objeto.
- [ ] Validaciones y métodos de comportamiento dentro del modelo.
- [ ] Puertos de casos de uso nombrados `INombreUseCase` o `NombreUseCasePort`.
- [ ] Un único método `handle` por caso de uso.

---

## Nota

El mensaje original menciona el "principio Singleton, de los SOLID". Singleton es un patrón de diseño y no forma parte de SOLID. Por el contexto (un caso de uso = un método `handle`), el principio aplicable es el de **Responsabilidad Única (Single Responsibility, la "S" de SOLID)**. Conviene confirmarlo con el equipo.
