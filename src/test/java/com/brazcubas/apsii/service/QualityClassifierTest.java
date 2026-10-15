package com.brazcubas.apsii.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QualityClassifierTest {
    private final QualityClassifier classifier = new QualityClassifier();

    @Test
    void classifiesGoodConnection() {
        assertEquals("GOOD", classifier.classify(30.0, 0.0));
    }

    @Test
    void classifiesModeratePacketLoss() {
        assertEquals("MODERATE", classifier.classify(40.0, 1.5));
    }

    @Test
    void worseMetricDeterminesQuality() {
        assertEquals("UNSTABLE", classifier.classify(20.0, 5.0));
    }

    // ---------- Novos testes (Exercício 11.1) ----------

    // Borda: o limiar de 60 ms é inclusivo (a regra usa >=)
    @Test
    void classifiesRttExactlyAtModerateThresholdAsModerate() {
        // Arrange
        double rtt = 60.0;
        double packetLoss = 0.0;

        // Act
        String quality = classifier.classify(rtt, packetLoss);

        // Assert
        assertEquals("MODERATE", quality);
    }

    // Borda: logo abaixo do limiar ainda é GOOD
    @Test
    void classifiesRttJustBelowModerateThresholdAsGood() {
        // Arrange
        double rtt = 59.9;
        double packetLoss = 0.0;

        // Act
        String quality = classifier.classify(rtt, packetLoss);

        // Assert
        assertEquals("GOOD", quality);
    }

    // Caminho triste: RTT negativo é recusado
    @Test
    void rejectsNegativeRtt() {
        // Arrange
        double rtt = -1.0;
        double packetLoss = 0.0;

        // Act + Assert
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> classifier.classify(rtt, packetLoss));
        assertEquals("Prediction metrics must be finite and non-negative", error.getMessage());
    }

    // Caminho triste: métrica não finita (NaN) é recusada
    @Test
    void rejectsNonFinitePacketLoss() {
        // Arrange
        double rtt = 30.0;
        double packetLoss = Double.NaN;

        // Act + Assert
        assertThrows(IllegalArgumentException.class,
                () -> classifier.classify(rtt, packetLoss));
    }
}