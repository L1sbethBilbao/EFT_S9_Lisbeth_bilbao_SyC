# EFT Semana 9 — Unidos por los Animales

Aplicación web en capas (frontend Spring MVC + backend REST + MySQL). Ver `docker-compose.yml` en esta carpeta para levantar el stack completo (`docker compose up -d --build`).

## Credenciales demo (solo revisión docente)

Los siguientes usuarios se crean al iniciar el backend con base de datos vacía (`DataInitializer`). **Comparten la misma contraseña** configurada en el servidor.

| Usuario | Rol principal        |
|---------|----------------------|
| `maria` | Coordinador          |
| `pedro` | Veterinario          |
| `luis`  | Gestor               |

**Contraseña por defecto:** `Duoc2026!`

En despliegue se puede sobrescribir sin cambiar código usando la variable de entorno del backend:

`APP_SEED_USER_PASSWORD`

(misma propiedad `app.seed.user-password` en `backendS9/src/main/resources/application.properties`).

---

*Estas credenciales no se muestran en la página de login por motivos de seguridad y para evitar exposición en escaneos automáticos sobre la interfaz web.*
