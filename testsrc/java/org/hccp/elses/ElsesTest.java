package org.hccp.elses;

import org.junit.Test;

import static org.junit.Assert.*;



public class ElsesTest {

    @Test
    public void testUnknownArg() throws Exception {
	
	Exception ex = assertThrows(IllegalArgumentException.class, () -> Elses.main(new String[] {"--baddarg", "source", "target"}));
	assertTrue(ex.getMessage().contains("baddarg"));
	
    }

    @Test
    public void testTooManyPositionals() throws Exception {
	
	Exception ex = assertThrows(IllegalArgumentException.class, () -> Elses.main(new String[] {"-i", "8", "source", "target", "yo!"}));
	assertTrue(ex.getMessage().contains("Too many"));
	
    }
}
