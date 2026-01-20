# Copilot CLI Terminal - JetBrains Plugin

<p align="center">
  <img src="src/main/resources/META-INF/pluginIcon.svg" width="80" height="80" alt="Copilot CLI Logo">
</p>

Run **GitHub Copilot CLI** (`copilot`) seamlessly inside IntelliJ-based IDEs. No more switching to external terminals.

![JetBrains Plugin](https://img.shields.io/badge/JetBrains-Plugin-blue?logo=jetbrains)
![License](https://img.shields.io/badge/License-MIT-green)

## ✨ Features

- **One-click focus & restart** — Use `Ctrl+Shift+G` or Tools → Focus / Restart; hold Shift to rebuild the session
- **Auto-start** — Automatically runs if `copilot` is detected on PATH or common install locations
- **Clean UI** — Dedicated right-side ToolWindow; bottom terminal stays hidden
- **Smart Detection** — Checks common paths (`/opt/homebrew/bin`, `/usr/local/bin`, etc.)
- **Smart Hints** — Balloon notification with install instructions if CLI is not found

## 🚀 Installation

### Prerequisites

1. Install [GitHub Copilot CLI](https://docs.github.com/en/copilot/github-copilot-in-the-cli):
   
   **macOS/Linux:**
   ```bash
   curl -fsSL https://gh.io/copilot-install | bash
   ```
   
   **Windows:**
   ```bash
   winget install GitHub.Copilot
   ```
   
   **Or via npm:**
   ```bash
   npm install -g @github/copilot
   ```

2. Ensure `copilot` is available in your PATH

### Plugin Installation

#### From Source
1. Clone this repository
2. Build with Gradle:
   ```bash
   ./gradlew buildPlugin
   ```
3. Install the plugin from `build/distributions/copilot-cli-jetbrains-*.zip`

#### From JetBrains Marketplace
*(Coming soon)*

## 🛠 Usage

1. Open any project in IntelliJ IDEA, WebStorm, PyCharm, etc.
2. The **Copilot CLI** tool window appears on the right side
3. Use `Ctrl+Shift+G` (Windows/Linux) or `Cmd+Shift+G` (macOS) to focus
4. Hold `Shift` while pressing the shortcut to restart the session

## 📋 Requirements

- IntelliJ IDEA 2023.2+ (or any JetBrains IDE based on IntelliJ Platform 232+)
- Java 17+ (for building only)
- GitHub Copilot CLI installed

## 🏗 Building

```bash
# Build the plugin
./gradlew buildPlugin

# Run in a sandbox IDE for testing
./gradlew runIde
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

---

*Disclaimer: This plugin is an independent project and is not officially affiliated with or endorsed by GitHub.*
