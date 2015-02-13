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

    public void testGetMateriau() throws Exception {
        assertEquals(new Cellule(Materiau.BRIQUE).getMateriau(), Materiau.BRIQUE);
        assertEquals(new Cellule(Materiau.LAINE_DE_VERRE).getMateriau(), Materiau.LAINE_DE_VERRE);
        assertEquals(new Cellule(Materiau.GRANITE).getMateriau(), Materiau.GRANITE);
    }

    public void testToString() throws Exception {
        assertNotNull(cellule);
        assertEquals(cellule.toString(), "-1.0");
    }

}