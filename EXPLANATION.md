# Load Balancer Manager — Project Explanation

A JavaFX desktop application that gives cloud networking teams a GUI to monitor
load balancers, backend target health, and listener routing rules. This document
explains how every part of the project works and how they fit together.

---

## 1. The Big Picture

The app follows a classic **layered architecture**. Each layer has one job and
talks only to the layer next to it:

```
┌─────────────────────────────────────────────────────────┐
│  App.java            → starts the program, opens window   │
│      │                                                     │
│      ▼ loads                                               │
│  dashboard.fxml      → describes WHAT the screen looks like│
│      │                                                     │
│      ▼ controlled by                                       │
│  DashboardController → fills the screen with data          │
│      │                                                     │
│      ▼ asks for data from                                  │
│  MockService         → produces the sample data            │
│      │                                                     │
│      ▼ returns lists of                                    │
│  Model classes       → hold one row of data each           │
│                                                            │
│  dark-theme.css      → colors/styles everything on screen  │
└─────────────────────────────────────────────────────────┘
```

The key idea: **layout (FXML)**, **styling (CSS)**, and **logic (Java)** are kept
in separate files. This is the standard JavaFX pattern and makes each piece easy
to change without breaking the others.

---

## 2. Startup Flow — What Happens When You Run It

When you run `mvn javafx:run`, this exact sequence happens:

1. **`main()` in `App.java`** calls `launch(args)`. This hands control to the
   JavaFX framework and tells it to boot up the UI system.

2. JavaFX creates the application and calls **`start(Stage stage)`**. A `Stage`
   is the actual OS window.

3. Inside `start()`:
   - `FXMLLoader` reads **`dashboard.fxml`** and builds all the UI objects
     (tables, labels, boxes) described in it — as real Java objects in memory.
   - While loading, the loader sees `fx:controller="...DashboardController"`, so
     it creates a `DashboardController` and **automatically calls its
     `initialize()` method**. This is where the tables get filled with data.
   - A `Scene` (the content of the window) is created at size 1200×800.
   - **`dark-theme.css`** is attached to the scene, so every styled element gets
     its dark colors.
   - The stage gets a title, a minimum size, and `stage.show()` makes it visible.

That's it — after `show()`, the window is on screen with data already loaded.

---

## 3. The Model Layer — Holding the Data

Files: `LoadBalancer.java`, `BackendTarget.java`, `ListenerRule.java`

Each model class represents **one row** in a table. For example, one
`LoadBalancer` object = one load balancer = one row.

The important detail is that these use **JavaFX Properties** instead of plain
fields. Compare:

```java
// Plain Java (NOT used here):
private String name;

// JavaFX Property (used here):
private final StringProperty name = new SimpleStringProperty();
```

**Why Properties?** They are "observable." When a Property's value changes, the
UI that is bound to it updates *automatically* — you never have to manually
refresh the table. This is called **reactive binding**. Even though our data is
static mock data, using Properties is the correct JavaFX foundation and is what
the table columns bind to.

Each field has three methods (the standard JavaFX pattern):
- `getName()` — read the value
- `setName(...)` — change the value
- `nameProperty()` — get the Property object itself (this is what tables bind to)

The three models:
- **`LoadBalancer`** — name, type, region, status, activeConnections, throughputMbps
- **`BackendTarget`** — loadBalancer, ipAddress, port, health, weight
- **`ListenerRule`** — loadBalancer, priority, protocol, listenerPort, pathPattern, targetGroup

---

## 4. The Service Layer — Producing the Data

File: `MockService.java`

This class is the app's data source. It has three methods, one per table:
- `getLoadBalancers()`
- `getBackendTargets()`
- `getListenerRules()`

Each method builds and returns an **`ObservableList`** — a special JavaFX list
that tables watch for changes. Inside each method the data is simply **hard-coded**:
made-up but realistic values (fake IPs like `10.0.1.21`, invented names like
`prod-web-alb`, plausible connection counts, etc.).

**Nothing is real** — there is no cloud account, no AWS/Azure API, no network
calls, no database. All values are typed directly into this file. This is by
design (the spec calls for a mock data service for demonstration).

The benefit of isolating data here: to make the app show *real* data later, you
would only replace this one class (e.g. with real AWS SDK calls) and nothing
else in the app would need to change.

---

## 5. The View Layer — Describing the Screen

File: `dashboard.fxml`

FXML is an XML file that describes the UI **declaratively** — it says *what*
elements exist and how they're arranged, without any logic. Reading it top to
bottom mirrors what you see on screen:

- **`ScrollPane`** (the root) — makes the whole page scroll if it's taller than
  the window.
- **`VBox`** — a vertical stack; everything is arranged top-to-bottom inside it.
- **Header** — a title label and a subtitle label.
- **`HBox` of 4 `summary-card` boxes** — the stat cards across the top (LB count,
  target count, connections, throughput). `HBox.hgrow="ALWAYS"` makes them share
  the width equally.
- **Three `TableView` sections**, each with a section label above it:
  - Load Balancers table
  - Backend Targets table
  - Listener Rules table

Each `TableView` and each `TableColumn` has an **`fx:id`** (e.g. `fx:id="lbTable"`,
`fx:id="lbNameCol"`). These ids are the connection points — the controller
grabs each element by its `fx:id` to work with it. Any label the controller needs
to update (like `fx:id="lbCountLabel"`) also has an id.

`CONSTRAINED_RESIZE_POLICY` on each table makes the columns automatically fill the
table's width. `styleClass="..."` attributes attach CSS style names (see §7).

---

## 6. The Controller Layer — Connecting Data to Screen

File: `DashboardController.java`

This is the "brain" that fills the empty UI (from the FXML) with data (from the
service). Here's how it works:

### 6a. Field injection with `@FXML`
Every UI element from the FXML that the controller needs is declared as a field
marked `@FXML`, with a matching name:

```java
@FXML private TableView<LoadBalancer> lbTable;      // matches fx:id="lbTable"
@FXML private TableColumn<LoadBalancer, String> lbNameCol; // matches fx:id="lbNameCol"
```

When the FXML loads, JavaFX **injects** the real objects into these fields
automatically. After loading, `lbTable` points at the actual table on screen.

### 6b. `initialize()` — runs once, automatically
JavaFX calls `initialize()` right after injection. It does four things:

1. Gets the three data lists from `MockService`.
2. Wires up each table (`wireLoadBalancerTable`, `wireTargetTable`, `wireRuleTable`).
3. Computes the summary statistics (`computeSummary`).

### 6c. Wiring a table — binding columns to model fields
For each column, the controller sets a **cell value factory** that tells the
column *which* field of the model to display:

```java
lbNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
```

`PropertyValueFactory<>("name")` looks for `nameProperty()` on each `LoadBalancer`
and shows its value in that column. For number columns it binds directly to the
Property:

```java
lbConnCol.setCellValueFactory(c -> c.getValue().activeConnectionsProperty());
```

Finally `lbTable.setItems(data)` hands the list to the table, and the table draws
one row per object.

### 6d. Status color-coding — a custom cell factory
The Status and Health columns aren't just text — they're colored. This is done
with `applyStatusColoring()`, which installs a custom `TableCell` that looks at
the value and adds a CSS class:

- `"Active"` / `"Healthy"` → `status-good` (green)
- `"Provisioning"` / `"Draining"` → `status-warn` (yellow)
- `"Degraded"` / `"Unhealthy"` → `status-bad` (red)

The actual colors live in the CSS file — the controller only decides *which*
class applies. `updateItem()` runs every time a cell is drawn/reused, and it
first clears old classes so recycled cells don't keep stale colors.

### 6e. Computing the summary cards
`computeSummary()` uses Java **streams** to calculate the aggregate numbers:

```java
long activeLbs      = lbs.stream().filter(l -> "Active".equals(l.getStatus())).count();
long healthyTargets = targets.stream().filter(t -> "Healthy".equals(t.getHealth())).count();
long totalConn      = lbs.stream().mapToInt(LoadBalancer::getActiveConnections).sum();
double totalTput    = lbs.stream().mapToDouble(LoadBalancer::getThroughputMbps).sum();
```

Then it writes each result into the matching label with `setText(...)`, using
formatting like `String.format("%,d", ...)` for thousands separators and
`"%.1f Mbps"` for throughput. These are computed once at startup.

---

## 7. The Styling Layer — Making It Look Good

File: `dark-theme.css`

JavaFX has its own CSS dialect (properties are prefixed with `-fx-`). The FXML
elements carry `styleClass` names, and this file defines what each name looks
like. Highlights:

- **Dark palette** — a GitHub-dark background (`#0d1117`), panels (`#161b22`),
  borders (`#30363d`) — chosen for an ops-center display that's easy on the eyes.
- **`.summary-card`** — rounded, bordered boxes; `.card-value` is the big blue
  number, `.card-caption` the small grey label.
- **`.data-table`** — styled headers, rounded corners, and **striped rows**
  (`.table-row-cell:odd` gets a slightly different shade) for readability.
- **`.status-good` / `.status-warn` / `.status-bad`** — the green/yellow/red text
  the controller assigns to status cells.
- **Scrollbars and selection** are restyled to match the dark theme instead of
  the default light OS look.

Because styling lives entirely here, you can recolor the whole app without
touching any Java or FXML.

---

## 8. The Build Layer — Compiling and Running

Files: `pom.xml`, `module-info.java`

### `pom.xml` (Maven)
Declares:
- **Java 21 / JavaFX 21** as the target.
- **Dependencies**: JavaFX Controls/FXML/Web, Jackson (JSON, available for future
  use), ControlsFX (extra UI controls).
- The **javafx-maven-plugin**, configured with the main class so `mvn javafx:run`
  knows what to launch.

Commands:
- `mvn compile` — compile the code
- `mvn javafx:run` — build and launch the app
- `mvn package` — bundle into a JAR

### `module-info.java` (Java Platform Module System)
Java 21 uses modules. This file declares:
- `requires` — the modules we depend on (javafx.controls, javafx.fxml, etc.).
- `opens ... to javafx.fxml` — lets JavaFX use reflection to inject `@FXML`
  fields and call the controller.
- `opens ...model to javafx.base` — lets `PropertyValueFactory` reflectively read
  the model Properties.
- `exports com.dcp.lbmanager` — makes the main package visible so the app can start.

Without the correct `opens`, FXML injection and table binding would fail at
runtime — these lines are what make the reflection-based wiring legal.

---

## 9. End-to-End: Following One Piece of Data

To tie it all together, here's the life of the "18,452 connections" value on
`prod-web-alb`:

1. **`MockService.getLoadBalancers()`** creates a `LoadBalancer` object with
   `activeConnections = 18452`, stored as a `SimpleIntegerProperty`.
2. That object goes into an `ObservableList`, returned to the controller.
3. **`initialize()`** passes the list to `wireLoadBalancerTable()`.
4. The `lbConnCol` column is bound to `activeConnectionsProperty()`, so the table
   reads `18452` from that object and draws it in the Connections column.
5. Separately, `computeSummary()` streams over all LBs, sums every
   `activeConnections`, and writes the total into the "Active Connections"
   summary card via `totalConnLabel.setText(...)`.
6. **`dark-theme.css`** styles the cell text and the summary card so both display
   in the dark theme.

One value, flowing cleanly from service → model → controller → view → styling.
That separation is the whole point of the architecture.
