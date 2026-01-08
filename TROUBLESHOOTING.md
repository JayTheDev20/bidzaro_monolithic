# Troubleshooting Maven Compilation Issues

## Current Error
```
Fatal error compiling: java.lang.ExceptionInInitializerError: 
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Root Cause
This error occurs when there's a mismatch between:
1. The Java version being used
2. The Maven compiler plugin version
3. The tools.jar or internal compiler classes

## Solutions (Try in Order)

### Solution 1: Verify Java Version
```bash
java -version
javac -version
```

**Required:** Java 17 or higher

If you have multiple Java versions:
```bash
# Windows - Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

# Verify
java -version
```

### Solution 2: Use Maven Wrapper (Recommended)
The Maven Wrapper ensures consistent Maven version:
```bash
# Windows
.\mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

### Solution 3: Update Maven
If using system Maven, update to latest:
```bash
# Check current version
mvn -version

# Download latest from: https://maven.apache.org/download.cgi
# Extract and update PATH
```

### Solution 4: Clear Maven Cache
```bash
# Delete .m2 repository cache
# Windows: C:\Users\<username>\.m2\repository
# Linux/Mac: ~/.m2/repository

# Or use Maven to purge
mvn dependency:purge-local-repository
mvn clean install -U
```

### Solution 5: Use IntelliJ IDEA or Eclipse
IDEs have their own compilers that often work better:

**IntelliJ IDEA:**
1. Open project in IntelliJ
2. File → Project Structure → Project
3. Set SDK to Java 17
4. Build → Rebuild Project

**Eclipse:**
1. Import as Maven project
2. Right-click project → Maven → Update Project
3. Project → Clean

### Solution 6: Modify pom.xml (Already Done)
Ensure compiler plugin configuration:
```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <!-- Let Spring Boot parent manage version -->
            <configuration>
                <source>17</source>
                <target>17</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Solution 7: Use build.bat Script
We created a custom build script:
```bash
build.bat
```
This will:
- Check Java and Maven
- Clean build
- Compile with verbose logging
- Save errors to build.log

## Debugging Commands

### Get Full Error Stack
```bash
mvn clean compile -e -X > build.log 2>&1
```
Then check `build.log` for detailed errors.

### Check Effective POM
```bash
mvn help:effective-pom
```
This shows the final POM after parent inheritance.

### Dependency Tree
```bash
mvn dependency:tree
```
Check for conflicting dependencies.

## Alternative: Skip Compilation Issues

If you just want to see the API documentation without compiling:

1. **View Swagger YAML** - Already defined in code
2. **Use Postman Collection** - Can be generated from Swagger
3. **Read Source Code** - All endpoints documented in controllers

## Known Working Configuration

```xml
<!-- pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.1</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>
```

**Java Version:** OpenJDK 17 or Oracle JDK 17
**Maven Version:** 3.8.x or higher
**Spring Boot:** 3.2.1

## Still Not Working?

### Option A: Use Docker
```bash
docker build -t catering-platform .
docker run -p 8080:8080 catering-platform
```

### Option B: Use Pre-built JAR
If someone on your team has successfully built:
```bash
# Get the JAR file
java -jar catering-platform-1.0.0.jar
```

### Option C: Contact Support
Share the following information:
1. `java -version` output
2. `mvn -version` output
3. Contents of `build.log`
4. Operating System version

## Environment Variables to Check

```bash
# Windows
echo %JAVA_HOME%
echo %PATH%
echo %M2_HOME%

# Linux/Mac
echo $JAVA_HOME
echo $PATH
echo $M2_HOME
```

All should point to Java 17 installation.

## Quick Test

Try compiling a simple Java class:
```bash
# Create Test.java
echo "public class Test { public static void main(String[] args) { System.out.println(\"Java works!\"); } }" > Test.java

# Compile
javac Test.java

# Run
java Test

# Clean up
del Test.class Test.java
```

If this fails, the issue is with Java installation, not Maven.

