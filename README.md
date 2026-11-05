# spring-observability

A practice Spring Boot service for exploring the three pillars of observability —
**metrics**, **logs**, and **traces** — and wiring them into Grafana.

## Status

| Pillar  | Stack                                      | Status        |
|---------|---------------------------------------------|---------------|
| Metrics | Micrometer → Prometheus → Grafana          | Implemented   |
| Logs    | SLF4J/Logback → Loki (or Promtail) → Grafana | Not yet added |
| Traces  | Micrometer Tracing (OTel bridge) → Tempo/Zipkin → Grafana | Not yet added |

## Metrics stack (implemented)

**App → Prometheus → Grafana**

1. **App**: Spring Boot exposes metrics via `spring-boot-starter-actuator` +
   `micrometer-registry-prometheus`. `@Timed` methods are measured through the
   `TimedAspect` bean in `MeterConfig`, and custom `Counter`/`Gauge` metrics are
   registered directly against the `MeterRegistry` (see `OrderController`).
   Metrics are exposed at `/actuator/prometheus` on port `9001`
   (`src/main/resources/application.yaml`).
2. **Prometheus**: scrapes that endpoint on a 15s interval
   (`prometheus.yml`, job `spring-observability`,
   target `host.docker.internal:9001`).
3. **Grafana**: add Prometheus as a data source and build dashboards on top of
   the scraped series (`orders.placed`, `orders.failed`, `orders.in.progress`,
   `order.time`, plus standard JVM/HTTP metrics).

### Running locally

```bash
./mvnw spring-boot:run
```

Run Prometheus pointed at `prometheus.yml` (adjust the target if not running
Prometheus in Docker on macOS/Windows, where `host.docker.internal` resolves
to the host):

```bash
docker run --rm -p 9090:9090 \
  -v "$(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml" \
  prom/prometheus
```

Then run Grafana and point it at `http://host.docker.internal:9090` (or your
Prometheus container's address) as a Prometheus data source:

```bash
docker run --rm -p 3000:3000 grafana/grafana
```

Grafana will be at `http://localhost:3000` (default login `admin`/`admin`).

## Logs stack (planned)

Collect application logs (SLF4J/Logback, as already used in `UserController`)
and ship them to **Loki** via Promtail or a Docker logging driver, then add
Loki as a Grafana data source so logs can be correlated with the metrics
dashboards above (e.g. filtering logs by time range selected on a metrics
panel).

## Traces stack (planned)

Add `micrometer-tracing` with the OpenTelemetry bridge
(`micrometer-tracing-bridge-otel`) to generate spans for incoming requests and
outgoing calls, export them to **Tempo** (or Zipkin), and add that as a third
Grafana data source — enabling trace-to-logs and trace-to-metrics links from
a single Grafana instance.

## Project layout

- `controller/OrderController` — custom `Counter`/`Gauge` metrics + `@Timed`
- `controller/UserController` — example SLF4J logging
- `config/MeterConfig` — registers the `TimedAspect` so `@Timed` is active
- `prometheus.yml` — Prometheus scrape config for this app
