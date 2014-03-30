import hhelwich.junit.JasmineTest;
import hhelwich.junit.JasmineTestRunner;

import org.junit.runner.RunWith;

@RunWith(JasmineTestRunner.class)
@JasmineTest(src = { "Song", "Player" }, test = { "SpecHelper", "PlayerSpec" })
public class RunJasmineTest {}
