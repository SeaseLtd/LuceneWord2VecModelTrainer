package io.sease.tools.word2vec;

import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Slf4j
public class CreateSynonymsFileFromTrainedModel {

    private static List<String> WORD_SYNONYMS_LIST = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        log.info("Read a word2vec trained model from file");
        long startTime = System.currentTimeMillis();
        Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel("wiki-ita-w2v-model.zip");
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Model loaded in {} ms", elapsedTime);

        log.info("Extract synonyms for each word in the vocabulary");
        int i = 0;
        for(String word : word2Vec.getVocab().words()){
            String synonymFileRow = extractNearestWords(word2Vec, word, 0.8);
            WORD_SYNONYMS_LIST.add(synonymFileRow);
            log.info("Extracted synonyms for word {}", i++);
        }

        log.info("Write synonyms to a .txt file");
        FileWriter writer = new FileWriter("synonyms.txt");
        for(String synonymRow: WORD_SYNONYMS_LIST) {
            if (!synonymRow.isEmpty())
                writer.write(synonymRow + System.lineSeparator());
        }
        writer.close();
    }

    private static String extractNearestWords(Word2Vec vec, String word, double accuracy) {
        assert accuracy > 0;
        assert accuracy <= 1;
        List<String> synonymList = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        Collection<String> lst = vec.wordsNearest(word, 5);
        for (String synonym : lst) {
            double similarity = vec.similarity(word, synonym);
            if (similarity >= accuracy)
                synonymList.add(synonym);
        }
        if (!synonymList.isEmpty())
            synonymList.add(0, word);
        String synonymListCommaSeparated = String.join(",", synonymList);

        long estimatedTime = System.currentTimeMillis() - startTime;
        log.info("Synonyms for {} extracted in {} ms", word, estimatedTime);
        return synonymListCommaSeparated;
    }
}