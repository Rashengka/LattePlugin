# Installing and Managing Java for the Latte Plugin

This guide provides detailed instructions for installing and managing Java versions required for building and running the Latte Plugin.

## Table of Contents

1. [Java Version Requirements](#java-version-requirements)
2. [Checking Your Current Java Version](#checking-your-current-java-version)
3. [Installing JDK 17](#installing-jdk-17)
   - [Windows](#windows)
   - [macOS](#macos)
   - [Linux](#linux)
4. [Managing Multiple Java Versions](#managing-multiple-java-versions)
   - [Using SDKMAN (macOS/Linux)](#using-sdkman-macoslinux)
   - [Using jEnv (macOS/Linux)](#using-jenv-macoslinux)
   - [Using Jabba (Cross-platform)](#using-jabba-cross-platform)
   - [Windows-specific Tools](#windows-specific-tools)
5. [Configuring Java for Gradle](#configuring-java-for-gradle)
6. [Troubleshooting](#troubleshooting)
7. [References](#references)

## Java Version Requirements

The Latte Plugin has the following Java requirements:

- **Recommended**: JDK 17 (LTS)
- **Compatible**: JDK 8 through JDK 19
- **Not Compatible**: JDK 20 or newer

These requirements are based on:
1. The plugin is compiled with Java 17 compatibility
2. Gradle 7.6 (required for building the plugin) is compatible with JDK 8-19, but not with JDK 20+

## Checking Your Current Java Version

To check your current Java version, run:

```bash
java -version
```

You should see output similar to:

```
openjdk version "17.0.6" 2023-01-17
OpenJDK Runtime Environment (build 17.0.6+10)
OpenJDK 64-Bit Server VM (build 17.0.6+10, mixed mode, sharing)
```

If you see a version number of 20 or higher (e.g., 20, 21, 23), you'll need to install and switch to a compatible Java version.

## Installing JDK 17

### Windows

1. **Download JDK 17**:
   - [Eclipse Temurin (OpenJDK)](https://adoptium.net/temurin/releases/?version=17)
   - [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (requires Oracle account)

2. **Install JDK 17**:
   - Run the installer and follow the instructions
   - The installer will set up the necessary environment variables

3. **Verify installation**:
   ```
   java -version
   ```

### macOS

#### Using Homebrew

1. **Install Homebrew** (if not already installed):
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```

2. **Install JDK 17**:
   ```bash
   brew install --cask temurin17
   ```

3. **Verify installation**:
   ```bash
   java -version
   ```

#### Using the Installer

1. **Download JDK 17**:
   - [Eclipse Temurin (OpenJDK)](https://adoptium.net/temurin/releases/?version=17)
   - [Oracle JDK](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) (requires Oracle account)

2. **Install JDK 17**:
   - Open the downloaded .pkg file
   - Follow the installation instructions

3. **Verify installation**:
   ```bash
   java -version
   ```

### Linux

#### Debian/Ubuntu

```bash
# Update package list
sudo apt update

# Install JDK 17
sudo apt install openjdk-17-jdk

# Verify installation
java -version
```

#### Fedora/RHEL/CentOS

```bash
# Install JDK 17
sudo dnf install java-17-openjdk-devel

# Verify installation
java -version
```

#### Arch Linux

```bash
# Install JDK 17
sudo pacman -S jdk17-openjdk

# Verify installation
java -version
```

## Managing Multiple Java Versions

If you need to maintain multiple Java versions on your system, here are several tools that can help.

### Using SDKMAN (macOS/Linux)

[SDKMAN](https://sdkman.io/) is a tool for managing parallel versions of multiple Software Development Kits on Unix-based systems.

1. **Install SDKMAN**:
   ```bash
   curl -s "https://get.sdkman.io" | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```

2. **Install JDK 17**:
   ```bash
   sdk install java 17.0.6-tem
   ```

3. **List installed Java versions**:
   ```bash
   sdk list java
   ```

4. **Switch Java version**:
   ```bash
   # For the current terminal session
   sdk use java 17.0.6-tem
   
   # Set as default
   sdk default java 17.0.6-tem
   ```

5. **Verify current version**:
   ```bash
   java -version
   ```

### Using jEnv (macOS/Linux)

[jEnv](https://www.jenv.be/) is a command-line tool to help you manage parallel Java versions.

1. **Install jEnv**:
   
   **macOS (with Homebrew)**:
   ```bash
   brew install jenv
   ```
   
   **Linux/macOS (manual)**:
   ```bash
   git clone https://github.com/jenv/jenv.git ~/.jenv
   ```

2. **Add jEnv to your shell**:
   
   **Bash**:
   ```bash
   echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile
   echo 'eval "$(jenv init -)"' >> ~/.bash_profile
   source ~/.bash_profile
   ```
   
   **Zsh**:
   ```bash
   echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
   echo 'eval "$(jenv init -)"' >> ~/.zshrc
   source ~/.zshrc
   ```

3. **Add Java versions to jEnv**:
   ```bash
   # Find your JDK installations
   /usr/libexec/java_home -V  # macOS
   
   # Add a JDK to jEnv
   jenv add /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home/
   ```

4. **List managed versions**:
   ```bash
   jenv versions
   ```

5. **Switch Java version**:
   ```bash
   # For the current directory
   jenv local 17.0
   
   # Globally
   jenv global 17.0
   ```

6. **Verify current version**:
   ```bash
   java -version
   ```

### Using Jabba (Cross-platform)

[Jabba](https://github.com/shyiko/jabba) is a cross-platform Java version manager.

1. **Install Jabba**:
   
   **macOS/Linux**:
   ```bash
   curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
   ```
   
   **Windows (PowerShell)**:
   ```powershell
   [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
   Invoke-Expression (
     Invoke-WebRequest https://github.com/shyiko/jabba/raw/master/install.ps1 -UseBasicParsing
   ).Content
   ```

2. **Install JDK 17**:
   ```bash
   jabba install openjdk@17.0.6
   ```

3. **List installed versions**:
   ```bash
   jabba ls
   ```

4. **Switch Java version**:
   ```bash
   jabba use openjdk@17.0.6
   ```

5. **Verify current version**:
   ```bash
   java -version
   ```

### Windows-specific Tools

#### Using Windows Environment Variables

1. **Set JAVA_HOME**:
   - Right-click on "This PC" or "My Computer" and select "Properties"
   - Click on "Advanced system settings"
   - Click on "Environment Variables"
   - Under "System variables", click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: Path to your JDK (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot`)
   - Click "OK"

2. **Update PATH**:
   - Under "System variables", find and select "Path"
   - Click "Edit"
   - Click "New"
   - Add `%JAVA_HOME%\bin`
   - Click "OK" on all dialogs

3. **Verify in a new command prompt**:
   ```
   java -version
   ```

#### Using jEnv for Windows

[jEnv-for-Windows](https://github.com/FelixSelter/JEnv-for-Windows) is a port of jEnv for Windows.

1. **Install with PowerShell**:
   ```powershell
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/FelixSelter/JEnv-for-Windows/master/install.ps1'))
   ```

2. **Add Java versions**:
   ```
   jenv add "C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot"
   ```

3. **Switch Java version**:
   ```
   jenv use 17.0
   ```

## Configuring Java for Gradle

If you have multiple Java versions installed, you can specify which one Gradle should use:

### Using JAVA_HOME Environment Variable

Set the `JAVA_HOME` environment variable to point to your JDK 17 installation:

**macOS/Linux**:
```bash
export JAVA_HOME=/path/to/jdk-17
```

**Windows**:
```
set JAVA_HOME=C:\path\to\jdk-17
```

### Using Gradle Properties

Create or edit the `gradle.properties` file in your project root or in your Gradle user home directory (`~/.gradle/`):

```properties
org.gradle.java.home=/path/to/jdk-17
```

## Troubleshooting

### Java Version Mismatch

**Issue**: Gradle reports a different Java version than what you expect.

**Solution**:
1. Check if Gradle is using a different JDK:
   ```bash
   gradle --version
   ```
2. Set the `JAVA_HOME` environment variable as described above.

### Unsupported Class File Major Version

**Issue**: Error message like "Unsupported class file major version 64" when building with Gradle 7.6.

**Solution**:
This error occurs when you're using a Java version that's too new for Gradle 7.6. Switch to JDK 17 or another compatible version (8-19).

### Multiple Java Installations Conflict

**Issue**: Commands use a different Java version than expected.

**Solution**:
1. Check your PATH to see which Java binary is being found first:
   ```bash
   which java  # macOS/Linux
   where java  # Windows
   ```
2. Use a version manager like SDKMAN, jEnv, or Jabba to explicitly select the version you want.

### IntelliJ IDEA Using Wrong JDK

**Issue**: IntelliJ IDEA is using a different JDK than the one you want.

**Solution**:
1. Go to File > Project Structure
2. Under Project Settings > Project, select the correct JDK
3. Also check under Platform Settings > SDKs to ensure the correct JDK is configured

## References

- [Eclipse Temurin (OpenJDK) Downloads](https://adoptium.net/)
- [Oracle JDK Downloads](https://www.oracle.com/java/technologies/downloads/)
- [SDKMAN Documentation](https://sdkman.io/usage)
- [jEnv Documentation](https://www.jenv.be/)
- [Jabba Documentation](https://github.com/shyiko/jabba)
- [Gradle Java Compatibility](https://docs.gradle.org/7.6/userguide/compatibility.html)