package kda.learn.microservices.project.services.nlp;

import kda.learn.microservices.project.services.CommonFileUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NlpSymptomsServiceImpl implements NlpSymptomsService {

    private final static String SYMPTOMS_FILE = "data/symptoms.lst";
    private final static String STOP_WORDS_FILE = "data/stopwords.lst";
    private final static Map<String, Set<String>> SYMPTOMS = loadSymptomsData();
    private final static Set<String> STOP_WORDS = loadStopWords();

    private static final long MAX_DISEASES_COUNT = 3;

    private static Map<String, Set<String>> loadSymptomsData() {
        try {
            String data = CommonFileUtils.loadDataFile(SYMPTOMS_FILE);

            Map<String, Set<String>> res = new HashMap<>();
            for (var line : data.split("\n")) {
                var parts = line.split(":");
                if (parts.length != 2) throw new RuntimeException("Некорректный формат строки --> " + line);
                var code = parts[0];
                var symptomsStrings = parts[1].split(",");
                var symptoms = Arrays.stream(symptomsStrings)
                        .map(s -> s.trim().toLowerCase())
                        .collect(Collectors.toSet());
                res.put(code, symptoms);
            }
            return res;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки данных.", e);
        }
    }

    private static Set<String> loadStopWords() {
        try {
            String data = CommonFileUtils.loadDataFile(STOP_WORDS_FILE);
            return Arrays
                    .stream(data.split("\n"))
                    .map(s -> s.trim().toLowerCase())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки данных.", e);
        }
    }

    @Override
    public List<String> guessDisease(String text) {

        var preprocessedText = preprocess(text);

        return SYMPTOMS.entrySet().stream()
                .map(entry -> calcMinDist(preprocessedText, entry))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(distanceEntry -> distanceEntry.distance))
                .limit(MAX_DISEASES_COUNT)
                .map(distanceEntry -> distanceEntry.text)
                .collect(Collectors.toList());
    }

    private String preprocess(String text) {
        // Все в нижний регистр
        var res = text.toLowerCase();

        // Удалить стоп-слова
        for (String stopword : STOP_WORDS) {
            res = res.replaceAll("\\b" + stopword + "\\b", "");
        }

        // Удалить лишние разделители
        res = res.replaceAll("[.,\\-\\s]{2,}", " ").trim();

        return res;
    }

    private DistanceEntry calcMinDist(String text, Map.Entry<String, Set<String>> entry) {
        System.out.println();
        return entry.getValue()
                .stream()
                .map(symptom -> new DistanceEntry(entry.getKey(), LevenshteinUtils.smartDistance(text, symptom)))
                .filter(distanceEntry -> distanceEntry.distance != null)
                .min(Comparator.comparing(distanceEntry -> distanceEntry.distance))
                .orElse(null);
        // TODO: кроме подбора по целой фразе можно добавить подбор по отдельным словам
    }

    private static class DistanceEntry {
        private String text;
        private Integer distance;

        public DistanceEntry(String text, Integer distance) {
            this.text = text;
            this.distance = distance;
        }
    }
}
