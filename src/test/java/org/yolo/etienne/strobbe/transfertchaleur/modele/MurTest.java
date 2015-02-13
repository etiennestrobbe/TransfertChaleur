package org.yolo.etienne.strobbe.transfertchaleur.modele;

import junit.framework.TestCase;

public class MurTest extends TestCase {

    private Mur mur;
    private final String MUR_NOT_INIT = "-1.0,-1.0,-1.0,-1.0,-1.0 - -1.0,-1.0,-1.0\n";
    private final String MUR_INIT = "110.0,20.0,20.0,20.0,20.0 - 20.0,20.0,20.0\n";

    public void setUp() throws Exception {
        super.setUp();
        mur = new Mur();

    }

    public void testSetInit() throws Exception {
        assertNotNull(mur);
        assertEquals(mur.toString(), MUR_NOT_INIT);
        mur.setInit();
        assertEquals(mur.toString(), MUR_INIT);

    }

    public void testToString() throws Exception {
        assertNotNull(mur);
        assertEquals(mur.toString(), MUR_NOT_INIT);
    }

    public void testGetTemp() throws Exception {
        assertNotNull(mur);
        //mur
    }
}