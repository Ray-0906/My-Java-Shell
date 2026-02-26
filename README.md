# Build Your Own Shell — Java

[![progress-banner](https://backend.codecrafters.io/progress/shell/b35f6e8d-d4a1-4012-98be-ea5db8746208)](https://app.codecrafters.io/users/codecrafters-bot?r=2qF)

A fully functional POSIX-compliant shell built in Java as part of the [CodeCrafters "Build Your Own Shell" Challenge](https://app.codecrafters.io/courses/shell/overview). This shell supports built-in commands, external program execution, pipelines, I/O redirection, command history, and tab completion.

---

## Features

### Built-in Commands
| Command | Description |
|---------|-------------|
| `echo` | Print arguments to stdout |
| `cd` | Change current working directory (supports `~` for home) |
| `pwd` | Print current working directory |
| `type` | Display whether a command is a builtin or external |
| `history` | Display command history with optional `<n>` limit |
| `exit` | Exit the shell (saves history to `HISTFILE`) |

### Pipelines
- **Dual-command pipelines** — `cat file.txt | wc`
- **Multi-command pipelines** — `cat file | head -n 3 | wc`
- **Pipelines with built-ins** — `echo hello | wc`, `ls | type exit`
- **Long-running pipelines** — `tail -f file | head -n 5` (auto-terminates upstream)
- External-only pipelines use `ProcessBuilder.startPipeline()` for OS-native pipe handling
- Mixed pipelines (builtins + externals) use a thread-based `ExecutionUnit` abstraction

### I/O Redirection
- Stdout redirect: `echo hello > file.txt`
- Stdout append: `echo hello >> file.txt`
- Stderr redirect: `echo hello 2> error.txt`
- Stderr append: `echo hello 2>> error.txt`

### Command History
- `history` — show full history
- `history <n>` — show last n entries
- `history -r <file>` — read/append history from file into memory
- `history -w <file>` — write in-memory history to file
- `history -a <file>` — append only new commands since last flush
- **Startup loading** — reads history from `HISTFILE` environment variable on launch
- **Exit saving** — writes in-memory history to `HISTFILE` on `exit`
- **Up/Down arrow navigation** — powered by JLine

### Tab Completion
- **Command completion** — completes built-in commands and PATH executables
- **Partial completion** — completes to the longest common prefix when multiple matches exist
- **Double-tab listing** — shows all matches on second TAB press (with bell on first)
- **Filename completion** — completes filenames in current directory
- **Nested path completion** — completes filenames in subdirectories (e.g., `path/to/f<TAB>`)

---

## Project Structure

```
src/main/java/
├── Main.java                        # Entry point
├── Shell.java                       # Main REPL loop (JLine integration)
│
├── Model/
│   └── Command.java                 # Command model (args, redirects, pipeline)
│
├── Parser/
│   ├── Tokenizer.java               # Input tokenization (quotes, escaping)
│   ├── CommandParser.java           # Parse tokens into Command objects
│   ├── BuiltCompleter.java          # Tab completion (commands + filenames)
│   └── completers/
│       ├── CommandCompleter.java     # Command name completion
│       └── FileCompleter.java        # Filename completion
│
├── Executors/
│   ├── CommandExecutor.java         # Routes commands (single vs pipeline)
│   ├── BuiltinExecutor.java         # Dispatches to builtin handlers
│   ├── ExternalExecutor.java        # Runs external programs via ProcessBuilder
│   └── pipeline/
│       ├── ExecutionUnit.java        # Interface for pipeline stages
│       ├── ExternalUnit.java         # External command pipeline stage
│       ├── BuiltinUnit.java          # Builtin command pipeline stage
│       └── PipelineExecutor.java     # Orchestrates pipeline execution
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
│   └── ShellIo.java                 # Abstracted stdout/stderr streams
│
└── ShellContext/
    └── ShellContext.java             # Global state (cwd, history, builtins)
```

---

## Tech Stack

- **Language:** Java 25 (with preview features)
- **Build Tool:** Maven
- **Dependencies:** [JLine 3.25.1](https://github.com/jline/jline3) — terminal handling, line editing, tab completion

---

## How to Run

### Prerequisites
- Java 25+
- Maven (`mvn`)

### Build & Run

```sh
# Build the project
mvn -B package -Ddir=.

# Run the shell
./my_shell.sh
```

Or run directly:

```sh
java --enable-preview -jar codecrafters-shell.jar
```

### With History File

```sh
HISTFILE=~/.my_shell_history ./my_shell.sh
```

### Run CodeCrafters Tests

```sh
codecrafters submit
```

---

## Architecture

```
User Input
    │
    ▼
Shell (JLine LineReader)
    │
    ▼
Tokenizer ──► CommandParser ──► Command
                                    │
                        ┌───────────┴───────────┐
                        ▼                       ▼
                   Single Command           Pipeline
                        │                       │
                        ▼                       ▼
                CommandExecutor          PipelineExecutor
                   │         │            │            │
                   ▼         ▼            ▼            ▼
             Builtin    External    ExternalUnit  BuiltinUnit
             Executor   Executor   (ProcessBuilder) (Thread)
```

### Pipeline Design

| Scenario | Implementation |
|----------|---------------|
| All external (`cat \| wc`) | `ProcessBuilder.startPipeline()` — OS-native pipes |
| Mixed (`echo hi \| wc`) | `ExecutionUnit` abstraction with pipe threads |
| Long-running (`tail -f \| head`) | Last process waited first, upstream destroyed |

---

## Completed Stages

- Shell REPL with prompt
- Built-in commands: `echo`, `type`, `exit`, `pwd`, `cd`
- External command execution via PATH lookup
- Quoting (single quotes, double quotes, backslash escaping)
- Stdout/Stderr redirection (`>`, `>>`, `2>`, `2>>`)
- Dual-command pipelines
- Multi-command pipelines
- Pipelines with built-in commands
- Command completion (TAB)
- Partial completions (longest common prefix)
- Multiple completions (double-TAB listing)
- Filename completion
- Nested path filename completion
- History builtin (`history`, `history <n>`)
- History navigation (Up/Down arrow)
- History file: read (`-r`), write (`-w`), append (`-a`)
- History load on startup (`HISTFILE`)
- History save on exit

---

## License

This project was built as part of the [CodeCrafters](https://codecrafters.io) challenge.
