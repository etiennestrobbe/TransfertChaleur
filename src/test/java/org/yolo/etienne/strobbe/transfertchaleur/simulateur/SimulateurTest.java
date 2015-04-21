package org.yolo.etienne.strobbe.transfertchaleur.simulateur;

import junit.framework.TestCase;
import org.yolo.etienne.strobbe.transfertchaleur.tools.Constantes;

public class SimulateurTest extends TestCase {

    private Simulateur simulateur;

    public void setUp() throws Exception {
        super.setUp();
        simulateur = new Simulateur();

    }

    public void testUpdate() throws Exception {
        assertNotNull(simulateur);
        assertEquals(110.0, simulateur.update(0));
        assertEquals(20.0, simulateur.update(simulateur.sizeSimulation() - 1));
    }


    public void testSizeSimulation() throws Exception {
        assertNotNull(simulateur);
        assertEquals(Constantes.SIZE_ISOLANT + Constantes.SIZE_MUR, simulateur.sizeSimulation());
    }

}