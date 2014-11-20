package indexer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WordNormalizerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNormalizeWord() {
		WordNormalizer wordNormalizer = new WordNormalizer();
		assertEquals("tete", wordNormalizer.normalizeWord("têtes"));
		assertEquals("tetetet", wordNormalizer.normalizeWord("tétetetes"));
		assertEquals("tete", wordNormalizer.normalizeWord("TETE"));
		assertEquals("totete", wordNormalizer.normalizeWord("tötétè"));
		assertEquals("tuti", wordNormalizer.normalizeWord("tùTï"));
	}

}
