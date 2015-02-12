package org.yolo.etienne.strobbe.transfertchaleur.modele;

import junit.framework.TestCase;

public class MateriauTest extends TestCase {

    private Materiau materiau;

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testGetLambda() throws Exception {
        assertEquals(Materiau.LAINE_DE_VERRE.getLambda(), 0.04);
        assertEquals(Materiau.BRIQUE.getLambda(), 0.84);
        assertEquals(Materiau.GRANITE.getLambda(), 2.2);

    }

    public void testGetRho() throws Exception {
        assertEquals(Materiau.LAINE_DE_VERRE.getRho(), 30);
        assertEquals(Materiau.BRIQUE.getRho(), 1400);
        assertEquals(Materiau.GRANITE.getRho(), 2700);

    }

    public void testGetC() throws Exception {
        assertEquals(Materiau.LAINE_DE_VERRE.getC(), 900);
        assertEquals(Materiau.BRIQUE.getC(), 840);
        assertEquals(Materiau.GRANITE.getC(), 790);

    }
}