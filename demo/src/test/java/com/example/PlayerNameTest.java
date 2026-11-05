package com.example;

import static org.junit.jupiter.api.Assertions.*;


import java.util.Scanner;
import org.junit.jupiter.api.Test;

class PlayerNameTest {
    private HumanPlayer human() { return new HumanPlayer('X', new Scanner("")); }
    private CpuPlayer cpu() { return new CpuPlayer('O'); }

    @Test void humanDefaultName() { assertEquals("Player #1", human().getName()); }
    @Test void cpuDefaultName() { assertEquals("CPU #1", cpu().getName()); }

    @Test void setNameWorks() {
        Player p = human();
        p.setName("Hans");
        assertEquals("Hans", p.getName());
    }

    @Test void nullNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> human().setName(null));
    }
    @Test void emptyNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> human().setName(""));
    }
    @Test void blankNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> human().setName("   "));
    }
}