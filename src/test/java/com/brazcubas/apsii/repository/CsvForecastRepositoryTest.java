package com.brazcubas.apsii.repository;

import com.brazcubas.apsii.model.Forecast;
import com.brazcubas.apsii.model.PredictionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvForecastRepositoryTest {
    private static final String FIXTURE = "classpath:data-test/predictions-11-2.csv";

    @Mock
    private ModelRepository modelRepository;

    private CsvForecastRepository repository;

    @BeforeEach
    void setUp() {
        // Arrange (comum): o dublê devolve só model-a e model-b
        when(modelRepository.findAll()).thenReturn(List.of(model("model-a"), model("model-b")));
        repository = new CsvForecastRepository(new DefaultResourceLoader(), modelRepository, FIXTURE);

        // Act (comum): carrega a fixture pela via pública
        repository.load();
    }

    @Test
    void loadsOnlyValidRowsFromKnownModels() {
        List<Forecast> all = repository.findAll();

        assertEquals(List.of("mock-a-1", "mock-a-2", "mock-a-3", "mock-a-4", "mock-b-1"), ids(all));
        verify(modelRepository).findAll();
    }

    @Test
    void timelineKeepsOnlyValidRowsInChronologicalOrder() {
        List<Forecast> timeline = repository.findByModelAndProbe("model-a", 900001);

        assertEquals(List.of("mock-a-1", "mock-a-2", "mock-a-3", "mock-a-4"), ids(timeline));
    }

    @Test
    void blankConfidenceLoadsAsNull() {
        Forecast forecast = timelineForecast("mock-a-2");

        assertNull(forecast.modelConfidence());
    }

    @Test
    void acceptsConfidenceAtLowerBoundZero() {
        Forecast forecast = timelineForecast("mock-a-3");

        assertEquals(0.0, forecast.modelConfidence());
    }

    @Test
    void acceptsConfidenceAtUpperBoundOne() {
        Forecast forecast = timelineForecast("mock-a-4");

        assertEquals(1.0, forecast.modelConfidence());
    }

    @Test
    void discardsRowWithConfidenceAboveOne() {
        List<String> timelineIds = ids(repository.findByModelAndProbe("model-a", 900001));

        assertFalse(timelineIds.contains("mock-a-5"));
    }

    @Test
    void discardsRowWithNonNumericConfidence() {
        List<String> timelineIds = ids(repository.findByModelAndProbe("model-a", 900001));

        assertFalse(timelineIds.contains("mock-a-6"));
    }

    @Test
    void discardsRowWithNegativeConfidence() {
        List<String> timelineIds = ids(repository.findByModelAndProbe("model-a", 900001));

        assertFalse(timelineIds.contains("mock-a-7"));
    }

    private Forecast timelineForecast(String predictionId) {
        return repository.findByModelAndProbe("model-a", 900001).stream()
                .filter(forecast -> forecast.predictionId().equals(predictionId))
                .findFirst()
                .orElseThrow(() -> new AssertionError(predictionId + " deveria ter sido carregada"));
    }

    private static List<String> ids(List<Forecast> forecasts) {
        return forecasts.stream().map(Forecast::predictionId).toList();
    }

    private static PredictionModel model(String id) {
        return new PredictionModel(id, "Modelo " + id, "Descrição de exemplo", "Grupo", "Teste", "1.0", true, null);
    }
}