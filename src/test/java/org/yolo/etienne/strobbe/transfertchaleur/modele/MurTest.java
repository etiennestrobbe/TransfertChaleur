package org.yolo.etienne.strobbe.transfertchaleur.modele;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.yolo.etienne.strobbe.transfertchaleur.tools.BadIndexException;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

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

    public void testGetMateriau() throws Exception {
        assertNotNull(mur);
        assertEquals(mur.getMateriau(0), Materiau.BRIQUE);
        assertEquals(mur.getMateriau(5), Materiau.LAINE_DE_VERRE);

        try {
            assertEquals(mur.getMateriau(-1), Materiau.DEFAULT);
        } catch (BadIndexException e) {
            System.err.println(e.getLocalizedMessage());
            Assert.assertTrue(true);
        }
        try {
            assertEquals(mur.getMateriau(2000), Materiau.DEFAULT);
        } catch (BadIndexException e) {
            System.err.println(e.getLocalizedMessage());
            Assert.assertTrue(true);
        }
    }

    public void testGetTemp() throws Exception {
        assertNotNull(mur);
        mur.setInit();
        assertEquals(mur.getTemp(0), 110.0);
        assertEquals(mur.getTemp(1), 20.0);
        mur.setTemp(6, 25.0);
        assertEquals(mur.getTemp(6), 25.0);
    }

    public void testSize() throws Exception {
        assertNotNull(mur);
        assertEquals(Constantes.SIZE_ISOLANT + Constantes.SIZE_MUR, mur.size());
    }
}