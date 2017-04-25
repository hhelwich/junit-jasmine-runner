JUnit Jasmine Runner
====================

### Features
* Runs [Jasmine Tests](http://jasmine.github.io/2.0/introduction.html) with JUnit
* Integrates with Eclipse, IntelliJ, Maven etc.
* Uses Java Nashorn JavaScript interpreter
* Optional Browser Support (with [Envjs](https://github.com/thatcher/env-js))
* Supports Jasmine 2

### Requirements
* Java 8

Usage
-----

Add project dependency. E.g if you use maven:

```xml
<dependency>
  <groupId>de.helwich.junit</groupId>
  <artifactId>junit-jasmine-runner</artifactId>
  <version>0.1.3</version>
  <scope>test</scope>
</dependency>
```

Or download from [here](http://search.maven.org/#search|ga|1|g%3A%22de.helwich.junit%22%20AND%20a%3A%22junit-jasmine-runner%22).

Add Java class (below `src/test/java` for maven) which name ends with “Test”. The class specifies the JavaScript test and source files:

```java
import de.helwich.junit.*;
import org.junit.runner.RunWith;

@RunWith(JasmineTestRunner.class)
@JasmineTest(
    src =  { "Song", "Player" },
    test = { "SpecHelper", "PlayerSpec" },
    browser = false
)
public class RunJasmineTest {}
```

By default the tests are expected to be below `src/test/js` and the sources below `src/main/js`. The default file suffix is `.js`.

### Release

```
mvn clean deploy -P release
cd target
jar -cvf bundle.jar junit-jasmine-runner-*
```

* login [here](https://oss.sonatype.org/)
* Build Promotion > Staging Upload > Artifact Bundle > Select Bundle to Upload
* Build Promotion > Staging Repositories > Select > Release

### ToDo
* optionally emit HTML runner
