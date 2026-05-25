# MultiGraph Lab

MultiGraph Lab is a web application for **modeling networks of relationships** as an interactive graph.

You represent things as **entities** (nodes): people, organizations, concepts, events, and so on. You connect them with **directed links** (arrows): “works with”, “owns”, “influences”, etc. Each link belongs to a **relationship type** that controls its label, color, and line style.

The graph is drawn in the main area. You can explore it, edit it by hand, undo mistakes, and optionally build or enrich it with an external AI (ChatGPT, Claude, etc.) via copy and paste.

The database starts **empty**. You create your own graph from scratch, or import one from an AI response.

---

## Getting started

**Requirements:** JDK 25.

```bash
./gradlew run
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

Data is stored in an in-memory H2 database during development: **everything is reset when the server restarts**.

---

## The interface

The screen has two main parts:

| Area | Role |
|------|------|
| **Sidebar** (left) | Browse, create, edit, and AI tools |
| **Graph** (right) | Interactive visualization |

### Top bar

- **Language** — English, French, Spanish, Russian, or Portuguese
- **Theme** — light, dark, or automatic
- **Undo / Redo** — revert recent changes
- **Reorganize** — recalculate the automatic layout (node positions are otherwise kept when you edit the graph)

### Graph controls

- **Zoom + / − / fit** — bottom-right of the graph
- **Drag** a node to move it
- **Click** a node or link to select it
- **Pan** by dragging the background

On mobile, open the sidebar with **☰**, pin it with the pin icon if needed, or use the **+** button (bottom-left) to create an entity quickly.

---

## Sidebar tabs

### Browse

Find entities in a large graph:

1. Use the **search** field (name, entity type, or relationship type).
2. Narrow with the **entity type** and **relationship type** filters.
3. Click an entity in the list to select it on the graph.

Optional: check **Focus graph on selection** to center and zoom on the selected node. Uncheck it to highlight without moving the view.

Selecting from Browse does **not** switch to the Detail tab — you stay in Browse.

### Create

Three sub-tabs:

**Entity**

1. Open **Create → Entity**.
2. Enter a name and type (pick an existing type or choose “Other type…”).
3. Optionally expand **Advanced options** for a description and color.
4. Click **Create**.

The new node is selected and the app switches to **Detail** so you can review or adjust it. Returning to **Create → Entity** clears the form for the next entry.

**Link**

1. Open **Create → Link**.
2. Set **source** and **target** by typing entity names (with suggestions) or by clicking nodes on the graph.
3. Choose a relationship type (or create a new one).
4. Click **Create link**.

**Type**

Manage relationship types globally: internal name, display label, color, and solid/dashed line style.

### Detail

Shows the selected entity or link:

- Edit fields and click **Save**
- **Delete** removes the selection (and an entity’s links)
- Click a node or link on the graph to open Detail automatically

### AI

Work with an external AI in four steps:

1. **Copy AI prompt** — paste a document into your AI tool; it should return a JSON graph.
2. **Export for AI** — send the current graph to the AI to improve or extend it.
3. **Import from AI** — paste the JSON response; confirm to replace the current graph.
4. **Generate textual synthesis** — copy a prompt that asks the AI to write a text summary from the graph.

MultiGraph Lab does not call an AI API itself: you copy prompts and results manually.

---

## Common tasks

| Task | How |
|------|-----|
| Create an entity | Create → Entity, or mobile **+** FAB |
| Create a link | Create → Link |
| Edit something | Click it on the graph → Detail → Save |
| Delete something | Detail → Delete, or select and press **Del** |
| Undo / redo | Header buttons, or **Ctrl+Z** / **Ctrl+Y** |
| Rearrange layout | Reorganize button in the header |
| Change language | Language dropdown in the header |
| Show shortcuts | **?** or **Help (?)** in the sidebar footer |

Press **Esc** to deselect or cancel.

---

## Keyboard shortcuts

| Key | Action |
|-----|--------|
| `Ctrl+Z` | Undo |
| `Ctrl+Y` | Redo |
| `Del` | Delete selection |
| `Esc` | Deselect / cancel |
| `?` | Show help |

---

## Building from source

```bash
./gradlew test    # run tests
./gradlew build   # build the application JAR (ws-app.jar)
```

The UI uses Tailwind CSS; styles are rebuilt automatically during `./gradlew build` via the `buildCss` task.
