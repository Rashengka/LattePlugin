# Building and Testing the Latte Plugin for JetBrains IDEs

This guide provides comprehensive instructions for building and testing the Latte Plugin for JetBrains IDEs from source code.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Setting Up the Development Environment](#setting-up-the-development-environment)
3. [Building the Plugin](#building-the-plugin)
4. [Running and Debugging the Plugin](#running-and-debugging-the-plugin)
5. [Running Tests](#running-tests)
6. [Creating a Distribution Package](#creating-a-distribution-package)
7. [Installing the Plugin](#installing-the-plugin)
8. [Troubleshooting](#troubleshooting)

## Prerequisites

Before you begin, ensure you have the following installed:

- **JDK 17** or later (the plugin is compiled with Java 17 compatibility)
- **Gradle 7.6** (required for building the plugin - **IMPORTANT**: The JetBrains Intellij plugin is NOT compatible with Gradle 8.x)
- **IntelliJ IDEA** (Community or Ultimate edition, version 2023.1.5 or compatible)

> **Note**: If you have Gradle 8.x installed, see the [GRADLE_7.6_INSTALL.md](GRADLE_7.6_INSTALL.md) file for detailed instructions on how to install and use Gradle 7.6 alongside your existing Gradle installation.

### Checking Version Requirements

You can verify that your environment meets the required Java and Gradle versions by running the provided checker script:

```bash
# Make the script executable (if not already)
chmod +x check_versions.sh

# Run the checker script
./check_versions.sh
```

The script will:
- Check if your Java version is compatible (JDK 8-19, with JDK 17 recommended)
- Check if your Gradle version is exactly 7.6
- Verify the presence of Gradle wrapper files
- Provide appropriate guidance if any requirements are not met

This is a quick way to ensure your development environment is properly set up before proceeding with building the plugin.

## Setting Up the Development Environment

### Installing JDK 17

1. **Download JDK 17**:
   - OpenJDK: [Adoptium](https://adoptium.net/)
   - Oracle JDK: [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

2. **Install JDK 17**:
   - **Windows**: Run the installer and follow the instructions
   - **macOS**: Run the installer package or use Homebrew: `brew install --cask temurin17`
   - **Linux**: Use your package manager (e.g., `sudo apt install openjdk-17-jdk` for Ubuntu)

3. **Verify installation**:
   ```
   java -version
   ```
   You should see output indicating Java 17.

For detailed instructions on installing and managing Java versions, including how to handle multiple Java installations and troubleshooting common issues, see [JAVA_INSTALL.md](JAVA_INSTALL.md).

### Installing Gradle

1. **Download Gradle**:
   - Visit [Gradle Releases](https://gradle.org/releases/) and download version 7.6

2. **Install Gradle**:
   - **Windows**: Extract the ZIP file and add the `bin` directory to your PATH
   - **macOS/Linux**: Extract and add to PATH or use a package manager:
     - macOS: `brew install gradle@7.6` or use SDKMAN (recommended)
     - Linux: Use SDKMAN (recommended) or download directly

3. **Verify installation**:
   ```
   gradle --version
   ```
   You should see output indicating Gradle 7.6.

For detailed instructions on installing and managing Gradle 7.6, including using SDKMAN and Docker-based solutions, see [GRADLE_7.6_INSTALL.md](GRADLE_7.6_INSTALL.md).

### Installing IntelliJ IDEA

1. **Download IntelliJ IDEA**:
   - [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (Community Edition is sufficient)

2. **Install IntelliJ IDEA**:
   - Run the installer and follow the instructions

3. **Install required plugins**:
   - Open IntelliJ IDEA
   - Go to Settings/Preferences > Plugins
   - Install the "Gradle" and "Plugin DevKit" plugins if not already installed

## Building the Plugin

### Cloning the Repository

1. Clone the repository:
   ```
   git clone https://github.com/Rashengka/LattePlugin.git
   cd LattePlugin
   ```

### Adding Gradle Wrapper (Strongly Recommended)

The project currently doesn't include the Gradle wrapper. Adding the wrapper ensures that everyone building the project uses the same Gradle version (7.6), regardless of what version they have installed on their system.

#### If you have Gradle 7.6 installed:

```bash
gradle wrapper
```

#### If you have Gradle 8.x installed:

The JetBrains Intellij plugin is not compatible with Gradle 8.x, so you'll need to use one of the following methods to add the wrapper:

1. **Use SDKMAN to install and use Gradle 7.6** (recommended for Unix-based systems):
   ```bash
   # Install SDKMAN if not already installed
   curl -s "https://get.sdkman.io" | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   
   # Install and use Gradle 7.6
   sdk install gradle 7.6
   sdk use gradle 7.6
   
   # Add the wrapper
   gradle wrapper
   ```

2. **Download and use Gradle 7.6 directly**:
   ```bash
   # Download and extract Gradle 7.6
   wget https://services.gradle.org/distributions/gradle-7.6-bin.zip
   unzip gradle-7.6-bin.zip
   
   # Use the extracted Gradle to add the wrapper
   ./gradle-7.6/bin/gradle wrapper
   ```

3. **Use Docker** (see [build_test_results.md](build_test_results.md) for detailed instructions)

Once added, the wrapper will create the necessary files (`gradlew`, `gradlew.bat`, and the `gradle/wrapper` directory).

### Building with Gradle

1. **Using Gradle directly**:
   ```
   gradle buildPlugin
   ```

2. **Using Gradle wrapper** (if you added it):
   ```
   ./gradlew buildPlugin
   ```

The built plugin will be available at `build/distributions/LattePlugin-1.0-SNAPSHOT.zip`.

## Running and Debugging the Plugin

### Opening the Project in IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select "Open" and navigate to the LattePlugin directory
3. Choose "Open as Project"
4. When prompted, select "Open as a Gradle Project"

### Running the Plugin

1. In IntelliJ IDEA, go to the Gradle tool window (View > Tool Windows > Gradle)
2. Navigate to Tasks > intellij > runIde
3. Double-click on `runIde` to start a new instance of IntelliJ IDEA with the plugin installed

### Debugging the Plugin

1. In the Gradle tool window, right-click on `runIde`
2. Select "Debug"
3. Set breakpoints in your code as needed
4. When the new IntelliJ IDEA instance starts, you can debug the plugin

## Running Tests

The plugin uses JUnit 4 for testing (as of July 2025, the project was converted from JUnit 5 to JUnit 4 for better compatibility with the IntelliJ Platform). You can run the tests using Gradle:

1. **Using Gradle directly**:
   ```
   gradle test
   ```

2. **Using Gradle wrapper** (if you added it):
   ```
   ./gradlew test
   ```

3. **Running tests from IntelliJ IDEA**:
   - Open the Gradle tool window
   - Navigate to Tasks > verification > test
   - Double-click on `test` to run all tests

### Viewing Test Results

Test results are available in the `build/reports/tests/test/index.html` file. Open this file in a browser to view detailed test results.

For the full, disciplined workflow to run, analyze, and fix tests iteratively (treating "0 tests" as failure and timeouts as errors), follow the Iterační testovací cyklus (ITC): see ../testing/ITERACNI_TESTOVACI_CYKLUS.md.

## Creating a Distribution Package

To create a distribution package that can be installed in IntelliJ IDEA:

1. **Using Gradle directly**:
   ```
   gradle buildPlugin
   ```

2. **Using Gradle wrapper** (if you added it):
   ```
   ./gradlew buildPlugin
   ```

The distribution package will be created at `build/distributions/LattePlugin-1.0-SNAPSHOT.zip`.

## Installing the Plugin

### Installing from a ZIP File

1. Open IntelliJ IDEA
2. Go to Settings/Preferences > Plugins
3. Click the gear icon and select "Install Plugin from Disk..."
4. Navigate to `build/distributions/LattePlugin-1.0-SNAPSHOT.zip` and select it
5. Click "OK" and restart IntelliJ IDEA when prompted

### Installing from the JetBrains Marketplace

Once the plugin is published to the JetBrains Marketplace:

1. Open IntelliJ IDEA
2. Go to Settings/Preferences > Plugins
3. Click "Marketplace" and search for "Latte Template"
4. Click "Install" and restart IntelliJ IDEA when prompted

## Troubleshooting

### Common Issues

#### Gradle Build Fails

- **Issue**: `Could not find method buildPlugin()`
  - **Solution**: Ensure you're using Gradle 7.6 and that the JetBrains Gradle plugin is properly applied in `build.gradle`

- **Issue**: `class org.jetbrains.intellij.MemoizedProvider overrides final method org.gradle.api.internal.provider.AbstractMinimalProvider.toString()Ljava/lang/String;`
  - **Solution**: This error occurs when using Gradle 8.x with the JetBrains Intellij plugin. The plugin is not compatible with Gradle 8.x. You must use Gradle 7.6 as specified in the prerequisites. See the [Adding Gradle Wrapper](#adding-gradle-wrapper-strongly-recommended) section for detailed instructions on how to use Gradle 7.6 even if you have Gradle 8.x installed.

- **Issue**: Java compilation errors
  - **Solution**: Ensure you're using JDK 17 or later

#### Plugin Doesn't Load in IntelliJ IDEA

- **Issue**: Plugin is not compatible with your IntelliJ IDEA version
  - **Solution**: Check the `build.gradle` file for the `intellij` block and ensure the `version` matches your IntelliJ IDEA version

- **Issue**: Required plugins are missing
  - **Solution**: Ensure the HTML plugin is installed in your IntelliJ IDEA instance

#### Tests Fail

- **Issue**: Tests cannot find dependencies
  - **Solution**: Ensure all test dependencies are properly declared in `build.gradle`

### Getting Help

If you encounter issues not covered here, please:

1. Check the [GitHub Issues](https://github.com/Rashengka/LattePlugin/issues) for similar problems
2. Create a new issue with detailed information about your problem