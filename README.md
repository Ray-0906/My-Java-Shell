# 🐚 JShell — POSIX-Compliant Unix Shell in Java

[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://www.oracle.com/java/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A lightweight, high-performance, POSIX-compliant Unix shell built from scratch in Java. **JShell** features a custom lexer/parser pipeline, hybrid process & thread-based execution engine for multi-stage pipelines, I/O redirection, autocompletion via Longest Common Prefix (LCP), and persistent session history.

---

## 🌟 Key Features

### 🛠️ Built-in Commands (In-Process Execution)
| Command | Description |
|---------|-------------|
| `echo` | Print text to standard output with quote stripping & escape evaluation |
| `cd` | Change directory (supports relative paths, absolute paths, and `~` home directory) |
| `pwd` | Print absolute path of current working directory |
| `type` | Inspect whether a command is a shell builtin or locate its executable in `PATH` |
| `history` | Display session history, last `N` entries, or file I/O (`-r`, `-w`, `-a`) |
| `exit` | Terminate shell session with custom exit status and auto-save history |

### 🔀 Hybrid Pipeline Engine
- **Native OS Pipelines**: Uses Java's `ProcessBuilder.startPipeline()` for all-external process chains (`cat file.txt | grep error | wc -l`).
- **Thread-Piped Builtins**: Hybrid pipelines involving shell builtins (`echo "data" | wc -w`) use an `ExecutionUnit` Strategy pattern backed by `PipedInputStream`/`PipedOutputStream` worker threads and non-blocking stream copy routines.
- **Upstream Lifecycle Management**: Long-running upstream stages (`tail -f log | head -n 5`) are automatically terminated via `SIGKILL` once downstream stages exit.

### 🔄 Standard I/O Redirection
- **Stdout Overwrite / Append**: `echo hello > out.txt`, `echo world >> out.txt`
- **Stderr Overwrite / Append**: `ls /invalid 2> err.log`, `ls /invalid 2>> err.log`
- Clean stream abstraction (`ShellIo`) decoupling command logic from underlying file/pipe streams.

### 🔤 Lexical Analysis & POSIX Quoting
- Finite State Machine (FSM) lexer handling:
  - Single quotes `'...'`: Everything literal, no escaping.
  - Double quotes `"..."`: Preserves whitespace, allows backslash escapes.
  - Backslash escaping `\`: Outside quotes and inside double quotes.

### ⌨️ Interactive Terminal & Tab Completion
- Powered by JLine 3 in raw terminal mode.
- **Context-Aware Autocompletion**:
  - **First Token**: Completes built-in commands & executables discovered across system `PATH`.
  - **Arguments**: Completes file and directory paths (including nested subdirectories `dir/sub/f<TAB>`).
- **Smart Completion Rules**:
  - Auto-completes up to the **Longest Common Prefix (LCP)** on partial matches.
  - Terminal bell (`\u0007`) on first `TAB` when no further prefix can be completed.
  - Formatted double-`TAB` candidate listing.

### 📜 Persistent History Engine
- **Interactive Navigation**: Up/Down arrow key history traversal.
- **Startup & Shutdown Persistence**: Loads from `$HISTFILE` on boot and flushes on `exit`.
- **Watermark Flushing**: `history -a <file>` uses an internal index watermark to incrementally append *only* newly typed commands since last flush.

---

## 🏗️ Architecture Overview

```
User Input ("cat file.txt | grep error > out.txt")
                       │
                       ▼
            ┌─────────────────────┐
            │   Shell REPL Loop   │ (JLine LineReader)
            └──────────┬──────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │      Tokenizer      │ Lexical Analysis (FSM)
            └──────────┬──────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │    CommandParser    │ Syntax Analysis & Pipeline Construction
            └──────────┬──────────┘
                       │
                       ▼
            ┌─────────────────────┐
            │   CommandExecutor   │ Router / Dispatcher
            └──────────┬──────────┘
                       │
             ┌─────────┴─────────┐
             ▼                   ▼
    ┌─────────────────┐ ┌──────────────────┐
    │ Single Command  │ │ PipelineExecutor │
    └────────┬────────┘ └────────┬─────────┘
             │                   │
      ┌──────┴──────┐     ┌──────┴─────────────┐
      ▼             ▼     ▼                    ▼
┌───────────┐ ┌──────────┐ ┌──────────────┐ ┌─────────────┐
│  Builtin  │ │ External │ │ ExternalUnit │ │ BuiltinUnit │
│ Executor  │ │ Executor │ │ (Process)    │ │ (Thread)    │
└───────────┘ └──────────┘ └──────────────┘ └─────────────┘
```

---

## 📂 Project Structure

```
src/main/java/
├── Main.java                        # Application entry point
├── Shell.java                       # Main REPL loop & JLine integration
│
├── Model/
│   └── Command.java                 # Command AST (arguments, redirection, pipeline)
│
├── Parser/
│   ├── Tokenizer.java               # POSIX state machine tokenizer
│   ├── CommandParser.java           # Command & pipeline parser
│   ├── BuiltCompleter.java          # JLine completion delegate router
│   └── completers/
│       ├── CommandCompleter.java    # Executable & builtin autocompleter
│       └── FileCompleter.java       # Path & nested directory autocompleter
│
├── Executors/
│   ├── CommandExecutor.java         # Central command router
│   ├── BuiltinExecutor.java         # In-process builtin dispatcher
│   ├── ExternalExecutor.java        # ProcessBuilder executor for external OS binaries
│   └── pipeline/
│       ├── ExecutionUnit.java       # Unified pipeline stage interface (Strategy Pattern)
│       ├── ExternalUnit.java        # Process-backed pipeline stage
│       ├── BuiltinUnit.java         # Thread & pipe-backed pipeline stage
│       └── PipelineExecutor.java    # Pipeline lifecycle orchestrator
│
├── Builtins/
│   ├── EchoCommand.java             # echo implementation
│   ├── CdCommand.java               # cd implementation
│   ├── PwdCommand.java              # pwd implementation
│   ├── TypeCommand.java             # type implementation
│   ├── ExitCommand.java             # exit implementation
│   └── historyCommand.java          # history implementation
│
├── IO/
│   └── ShellIo.java                 # Standard I/O stream abstraction wrapper
│
└── ShellContext/
    └── ShellContext.java            # Thread-safe global shell state (CWD, history)
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK**: Version 21 or higher
- **Build Tool**: Apache Maven (`mvn`)

### Building the Project

```bash
# Clone the repository
git clone https://github.com/your-username/My-Java-Shell.git
cd My-Java-Shell

# Build fat JAR with Maven
mvn package -Ddir=target
```

### Running the Shell

#### On Linux / macOS / Git Bash:
```bash
./my_shell.sh
```

#### On Windows (PowerShell):
```powershell
.\.myshell.ps1
```

#### Running Directly via Java:
```bash
java --enable-native-access=ALL-UNNAMED --enable-preview -jar target/codecrafters-shell.jar
```

#### With Custom History File:
```bash
HISTFILE=~/.my_shell_history java --enable-preview -jar target/codecrafters-shell.jar
```

---

## 🧪 Example Usage

```bash
my-shell> echo "Hello, World!"
Hello, World!

my-shell> pwd
/home/user/projects

my-shell> type echo
echo is a shell builtin

my-shell> type ls
ls is /usr/bin/ls

my-shell> cat file.txt | grep "ERROR" | wc -l
42

my-shell> echo "Log entry" >> app.log

my-shell> history 5
    1  echo "Hello, World!"
    2  pwd
    3  type echo
    4  type ls
    5  cat file.txt | grep "ERROR" | wc -l
```

---

## 📄 License

This project is open-source software licensed under the [MIT License](LICENSE).
