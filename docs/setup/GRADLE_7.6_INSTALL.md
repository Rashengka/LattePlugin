# Installing and Using Gradle 7.6 for the Latte Plugin

This guide provides detailed instructions for installing and using Gradle 7.6, which is required for building the Latte Plugin.

## Why Gradle 7.6 is Required

The Latte Plugin uses the JetBrains Intellij plugin for Gradle, which is **not compatible** with Gradle 8.x. Attempting to build the plugin with Gradle 8.x results in the following error:

```
class org.jetbrains.intellij.MemoizedProvider overrides final method org.gradle.api.internal.provider.AbstractMinimalProvider.toString()Ljava/lang/String;
```

Therefore, Gradle 7.6 is required to successfully build the plugin.

## Java Version Compatibility

Gradle 7.6 is compatible with:
- JDK 8 through JDK 19
- **Not compatible** with JDK 20 or newer

If you're using a newer JDK (like JDK 20+), you'll need to use one of the following approaches:
- Install a compatible JDK (8-19) alongside your current JDK
- Use Docker with a compatible JDK (recommended if you don't want to install multiple JDKs)

For detailed instructions on installing and managing Java versions, including how to install JDK 17 and manage multiple Java installations, see [JAVA_INSTALL.md](JAVA_INSTALL.md).

## Installation Methods

### Method 1: Using SDKMAN (Recommended for Unix-based Systems)

[SDKMAN](https://sdkman.io/) is a tool for managing parallel versions of multiple Software Development Kits on Unix-based systems (Linux, macOS, etc.).

1. **Install SDKMAN** (if not already installed):
   ```bash
   curl -s "https://get.sdkman.io" | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```

2. **Install Gradle 7.6**:
   ```bash
   sdk install gradle 7.6
   ```

3. **Use Gradle 7.6 for the current session**:
   ```bash
   sdk use gradle 7.6
   ```

4. **Verify the installation**:
   ```bash
   gradle --version
   ```
   You should see output indicating Gradle 7.6.

5. **Add the Gradle wrapper to the project** (recommended):
   ```bash
   gradle wrapper
   ```
   This creates the Gradle wrapper files (`gradlew`, `gradlew.bat`, and the `gradle/wrapper` directory) that will use Gradle 7.6.

### Method 2: Direct Download

1. **Download Gradle 7.6**:
   ```bash
   wget https://services.gradle.org/distributions/gradle-7.6-bin.zip
   ```
   Or download from the [Gradle releases page](https://gradle.org/releases/).

2. **Extract the archive**:
   ```bash
   unzip gradle-7.6-bin.zip
   ```

3. **Add to PATH** (optional):
   ```bash
   export PATH=$PATH:/path/to/gradle-7.6/bin
   ```

4. **Use the extracted Gradle to add the wrapper to the project**:
   ```bash
   /path/to/gradle-7.6/bin/gradle wrapper
   ```

### Method 3: Using Docker

If you have Docker installed, you can use a Docker container with Gradle 7.6 and JDK 17:

1. **Create a Dockerfile**:
   ```dockerfile
   FROM gradle:7.6-jdk17
   WORKDIR /app
   COPY . .
   CMD ["gradle", "buildPlugin"]
   ```

2. **Build and run the Docker container**:
   ```bash
   docker build -t latte-plugin-builder .
   docker run -v $(pwd):/app latte-plugin-builder
   ```

## Building the Plugin

Once you have Gradle 7.6 installed and configured:

1. **Using the Gradle wrapper** (recommended if you've added it):
   ```bash
   ./gradlew buildPlugin
   ```

2. **Using Gradle directly**:
   ```bash
   gradle buildPlugin
   ```

The built plugin will be available at `build/distributions/LattePlugin-1.0-SNAPSHOT.zip`.

## Troubleshooting

### Java Version Issues

If you see an error like:
```
Unsupported class file major version XX
```

This indicates that your Java version is not compatible with Gradle 7.6. Check your Java version with:
```bash
java -version
```

If you're using JDK 20 or newer, you'll need to:
- Install a compatible JDK (8-19)
- Configure Gradle to use the compatible JDK
- Or use the Docker approach described above

For detailed instructions on installing compatible Java versions, managing multiple Java installations, and configuring Java for Gradle, see [JAVA_INSTALL.md](JAVA_INSTALL.md).

### Multiple Gradle Versions

If you need to maintain multiple Gradle versions:

1. **With SDKMAN**:
   ```bash
   # List installed Gradle versions
   sdk list gradle
   
   # Switch between versions
   sdk use gradle 7.6
   sdk use gradle 8.x
   ```

2. **Without SDKMAN**:
   Use the full path to the Gradle version you want to use:
   ```bash
   /path/to/gradle-7.6/bin/gradle buildPlugin
   ```

## Conclusion

Using Gradle 7.6 is essential for successfully building the Latte Plugin due to compatibility requirements with the JetBrains Intellij plugin. By following the instructions in this guide, you can set up the correct Gradle version and build the plugin without encountering compatibility errors.