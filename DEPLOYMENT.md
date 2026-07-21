# Car Rental System — Deployment Guide

Full-stack app, made deployable end-to-end.

- **Backend:** Spring Boot 3.5.3, Java 21, Spring Security + JWT, **PostgreSQL**, Razorpay payments
- **Frontend:** Angular 15 + ng-zorro (Ant Design)
- **Database:** PostgreSQL

Two ways to run it:

1. **Local, one command** — `docker compose up` runs Postgres + backend + frontend together.
2. **Free public deploy** — **Neon** (Postgres) + **Render** (backend web service + static frontend). This gives you a public URL anyone can open, at no cost.

---

## 1. Run everything locally with Docker

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/). Nothing else needed.

### Steps
```bash
cd car-rental-system          # folder with docker-compose.yml
cp .env.example .env          # a ready .env is already included; edit if you like
docker compose up --build     # first run builds images (a few minutes)
```

Open:

| What | URL |
|------|-----|
| **App (use this)** | http://localhost:8081 |
| Backend API (direct) | http://localhost:8080 |
| PostgreSQL | localhost:5433 (user `postgres`) |

The frontend at **:8081** serves the Angular app and reverse-proxies `/api/*`
to the backend (same origin, no CORS).

**Log in** — an admin is auto-created on first boot:

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@test.com` | `Admin123` |

Customers self-register. Stop with `docker compose down` ( add `-v` to wipe the DB).

---

## 2. Free public deployment — Neon + Render

**Result:** a public frontend URL (always on) that talks to a backend on Render
(free, but sleeps after ~15 min idle — the first request then takes ~30–60s to
wake) backed by a Neon Postgres database (free, persistent).

You need three free accounts: **GitHub**, **Neon** (neon.tech), **Render** (render.com).

### Step A — Push this project to GitHub
From the project root:
```bash
git init
git add .
git commit -m "Deployable car rental system"
git branch -M main
git remote add origin https://github.com/<you>/car-rental-system.git
git push -u origin main
```
(Create the empty `car-rental-system` repo on github.com first.)

### Step B — Create the database on Neon
1. neon.tech → **New Project** (pick a region near you).
2. Copy the **connection string**. It looks like:
   `postgresql://alex:npg_xxx@ep-cool-name-123.us-east-2.aws.neon.tech/neondb?sslmode=require`
3. Split it into the three values you'll paste into Render:
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://ep-cool-name-123.us-east-2.aws.neon.tech/neondb?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME` = `alex`
   - `SPRING_DATASOURCE_PASSWORD` = `npg_xxx`

### Step C — Deploy the backend (Render Web Service)
1. render.com → **New → Web Service** → connect your GitHub repo.
2. Settings:
   - **Root Directory:** `backend`
   - **Runtime:** Docker (Render auto-detects the `Dockerfile`)
   - **Instance Type:** Free
3. **Environment variables:**
   ```
   SPRING_DATASOURCE_URL       = jdbc:postgresql://<neon-host>/<db>?sslmode=require
   SPRING_DATASOURCE_USERNAME  = <neon user>
   SPRING_DATASOURCE_PASSWORD  = <neon password>
   JWT_SECRET                  = <output of: openssl rand -base64 32>
   RAZORPAY_KEY                = rzp_test_OVYMnXur6tEFGV
   RAZORPAY_SECRET             = 3MqqRw03bNl2bkDRXyK75mT8
   ```
   (Render injects `PORT` automatically; the app already honors it.)
4. Deploy. When live, copy the backend URL, e.g. `https://carrental-backend.onrender.com`.

### Step D — Deploy the frontend (Render Static Site)
1. render.com → **New → Static Site** → same repo.
2. Settings:
   - **Root Directory:** `frontend`
   - **Build Command:** `npm install && npm run build:render`
   - **Publish Directory:** `dist/car-rental-angular`
3. **Environment variable:**
   ```
   API_URL = https://carrental-backend.onrender.com   (your Step C URL, no trailing slash)
   ```
   The build bakes this URL into the app; the backend's CORS filter allows the
   static site's origin, so calls just work.
4. **Add a rewrite rule** (Settings → Redirects/Rewrites) so Angular routing works:
   - Source `/*` → Destination `/index.html` → Action **Rewrite**
5. Deploy. The static site's URL is your **public app link** — share it.

> **Heads-up (free tier):** the *backend* sleeps when idle, so the very first
> load after a quiet period is slow while it wakes. The frontend and database
> stay up. Upgrading the Render backend to a paid instance removes the sleep.

---

## Environment variables reference

| Variable | Used by | Notes |
|----------|---------|-------|
| `SPRING_DATASOURCE_URL` | backend | `jdbc:postgresql://host/db?sslmode=require` |
| `SPRING_DATASOURCE_USERNAME` | backend | DB user |
| `SPRING_DATASOURCE_PASSWORD` | backend | DB password |
| `JWT_SECRET` | backend | **Base64**; rotate for production |
| `RAZORPAY_KEY` | backend + frontend | publishable key |
| `RAZORPAY_SECRET` | backend | keep server-side only |
| `PORT` | backend + frontend | injected by the host |
| `API_URL` | frontend build | backend URL baked into the static site |
| `BACKEND_ORIGIN` | frontend (Docker/nginx only) | proxy target for `/api` |
| `DB_NAME`, `DB_USER`, `DB_PASSWORD` | docker-compose | local only |

---

## What was changed to make this deployable
- Created the missing Angular `environment.ts` / `environment.prod.ts` (their
  absence was silently breaking the production build).
- Removed hardcoded `http://localhost:8080` from 4 services → configurable `apiUrl`.
- Externalized all secrets (DB, JWT signing key, Razorpay) to env vars.
- Fixed a 100× payment overcharge (amount was multiplied by 100 on both sides).
- Migrated from MySQL to PostgreSQL (driver, dialect, and the `longblob` column).
- Added Dockerfiles, nginx config, docker-compose, `set-env.js` for static
  hosting, and per-service deploy configs.

## Security notes (before going truly public)
- Generate a fresh `JWT_SECRET` (`openssl rand -base64 32`).
- Change the seeded admin password (`admin@test.com` / `Admin123`) after first login.
- Swap Razorpay test keys for live keys only when ready to take real payments
  (update `RAZORPAY_KEY` in env + `my-bookings.component.ts`, and `RAZORPAY_SECRET` in env).
