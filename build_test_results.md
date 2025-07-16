# Latte Plugin Build Test Results

## Environment

- **Java Version**: OpenJDK 23.0.2
- **Gradle Version**: 8.10
- **Operating System**: Mac OS X 15.5 aarch64

## Build Attempt Summary

I attempted to build the Latte Plugin following the instructions in the BUILD_AND_TEST.md guide, but encountered compatibility issues between Gradle 8.10 and the JetBrains Intellij plugin.

## Issues Encountered

1. **Gradle Version Incompatibility**:
   - The BUILD_AND_TEST.md guide recommends Gradle 7.6
   - The system has Gradle 8.10 installed
   - The JetBrains Intellij plugin is not compatible with Gradle 8.10

2. **Error Message**:
   ```
   class org.jetbrains.intellij.MemoizedProvider overrides final method org.gradle.api.internal.provider.AbstractMinimalProvider.toString()Ljava/lang/String;
   ```

3. **Attempted Solutions**:
   - Tried updating the JetBrains Intellij plugin from version 1.15.0 to 1.16.1
   - Tried downgrading the JetBrains Intellij plugin to version 1.13.3
   - Tried running simpler Gradle tasks (compileJava)
   - Temporarily modified build.gradle to comment out the JetBrains Intellij plugin

4. **Results**:
   - All attempts with the JetBrains Intellij plugin failed with the same error
   - Removing the plugin led to compilation errors due to missing dependencies

## Solution

The JetBrains Intellij plugin is not compatible with Gradle 8.10. To resolve this issue, you need to use Gradle 7.6 as specified in the BUILD_AND_TEST.md guide. Here are detailed instructions for setting up and using Gradle 7.6:

### Option 1: Install Gradle 7.6 using SDKMAN

[SDKMAN](https://sdkman.io/) is a tool for managing parallel versions of multiple Software Development Kits on most Unix-based systems.

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

5. **Add the Gradle wrapper to the project**:
   ```bash
   gradle wrapper
   ```

### Option 2: Use Docker

If you prefer using Docker, you can create a container with Gradle 7.6 and JDK 17:

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

### Option 3: Download and Use Gradle 7.6 Directly

1. **Download Gradle 7.6**:
   ```bash
   wget https://services.gradle.org/distributions/gradle-7.6-bin.zip
   ```

2. **Extract the archive**:
   ```bash
   unzip gradle-7.6-bin.zip
   ```

3. **Use the extracted Gradle to add the wrapper to the project**:
   ```bash
   ./gradle-7.6/bin/gradle wrapper
   ```

4. **Build the project using the wrapper**:
   ```bash
   ./gradlew buildPlugin
   ```

## Conclusion

The Latte Plugin requires Gradle 7.6 to build successfully due to compatibility issues between the JetBrains Intellij plugin and Gradle 8.10. By following one of the solutions above, you can set up the correct Gradle version and build the plugin without encountering the compatibility error.

Once you've added the Gradle wrapper to the project, anyone can build the plugin using the wrapper (`./gradlew buildPlugin`) without needing to install Gradle 7.6 separately.