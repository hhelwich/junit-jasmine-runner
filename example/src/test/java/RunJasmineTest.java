import org.junit.runner.RunWith;

import de.helwich.junit.JasmineTest;
import de.helwich.junit.JasmineTestRunner;

@RunWith(JasmineTestRunner.class)
@JasmineTest(src = { "Song", "Player" }, test = { "SpecHelper", "PlayerSpec", "introduction" })
public class RunJasmineTest {}
