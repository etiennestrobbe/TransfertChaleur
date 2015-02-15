package org.yolo.etienne.strobbe.transfertchaleur.modele;

import junit.framework.TestCase;

import java.util.Random;

public class CoucheTest extends TestCase {

    private Couche couche;
    private final int SIZE = 15;

    public void setUp() throws Exception {
        super.setUp();
        couche = new Couche(SIZE, Materiau.BRIQUE);

    }

    public void testSetTemperature() throws Exception {
        assertNotNull(couche);
        Random r = new Random();
        int id = r.nextInt(SIZE);
        assertEquals(couche.getTemperature(id), -1.0);
        r = new Random();
        double newTemp = r.nextDouble();
        couche.setTemperature(id, newTemp);
        assertEquals(couche.getTemperature(id), newTemp);

    }

    public void testLength() throws Exception {
        assertNotNull(couche);
        assertEquals(couche.length(), SIZE);
    }

    public void testGetMateriau() throws Exception {
        assertNotNull(couche);
        assertEquals(Materiau.BRIQUE, couche.getMateriau());
    }

    public void testToString() throws Exception {
        assertNotNull(couche);
        String expected = "-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0";
        assertEquals(new Couche(SIZE, Materiau.BRIQUE).toString(), expected);
    }
}