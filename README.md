JUnit Jasmine Runner
====================

### Features
* Runs [Jasmine Tests](http://jasmine.github.io/2.0/introduction.html) with JUnit
* Integrates with Eclipse, IntelliJ, Maven etc.
* Uses Java Nashorn JavaScript interpreter
* Optional Browser Support (with [Envjs](http://www.envjs.com/))
* Supports Jasmine 2

### Requirements
* Java 8

Usage
-----

Add project dependency. E.g if you use maven:

```xml
<dependency>
  <groupId>hhelwich.junit</groupId>
  <artifactId>junit-jasmine-runner</artifactId>
  <version>0.1.0</version>
  <scope>test</scope>
</dependency>
```

Add Java class (below `src/test/java` for maven) which name ends with “Test”. The class specifies the JavaScript test and source files:

```java
import hhelwich.junit.*;
import org.junit.runner.RunWith;

@RunWith(JasmineTestRunner.class)
@JasmineTest(
    src =  { "Song", "Player" }, 
    test = { "SpecHelper", "PlayerSpec" }
)
public class RunJasmineTest {}
```

By default the tests are expected to be below `src/test/javascript` and the sources below `src/main/javascript`. The default file suffix is `.js`.


### ToDo 
* deploy to maven central
* optionally emit HTML runner
