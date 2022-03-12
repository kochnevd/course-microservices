package kda.learn.microservices.project.services.nlp;

import kda.learn.microservices.project.services.CommonFileUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // строки, отличающиеся слишком сильно, будем отбрасывать, предел определим через LOG2
        int maxDist = (int) (Math.log(preprocessedText.length()) / Math.log(2));

        return SYMPTOMS.entrySet().stream()
                .map(entry -> calcMinDist(preprocessedText, entry))
                .filter(entry -> entry.getValue() < maxDist)
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .limit(MAX_DISEASES_COUNT)
                .map(Map.Entry::getKey)
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

    private Map.Entry<String, Integer> calcMinDist(String text, Map.Entry<String, Set<String>> entry) {
        return entry.getValue()
                .stream()
                .map(symptom -> Map.entry(entry.getKey(), LevenshteinUtils.smartDistance(text, symptom)))
                .min(Map.Entry.comparingByValue())
                .get();
        // TODO: кроме подбора по целой фразе можно добавить подбор по отдельным словам
    }
}
