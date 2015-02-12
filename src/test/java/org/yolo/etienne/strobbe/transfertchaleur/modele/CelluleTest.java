package org.yolo.etienne.strobbe.transfertchaleur.modele;

import junit.framework.TestCase;

public class CelluleTest extends TestCase {

    private Cellule cellule;

    public void setUp() throws Exception {
        super.setUp();
        cellule = new Cellule(Materiau.BRIQUE);
    }

    public void testSetTemperature() throws Exception {
        assertNotNull(cellule);
        double newTemp = Math.random();
        cellule.setTemperature(newTemp);
        assertEquals(this.cellule.getTemperature(), newTemp);
    }

}