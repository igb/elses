package org.hccp.elses;

import org.junit.Test;

import java.util.List;

public class ScannerTest {

    @Test
    public void testScanTokens() {
        Scanner s = new Scanner("AXIOM: F\nF -> FF");
        List<Token> tokens = s.scanTokens();


    }
}
