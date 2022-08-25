package io.sease.tools.word2vec;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FieldValuesSentenceIterator implements SentenceIterator {

    private final IndexReader reader;
    private final String fieldName;
    private String[] sentences = {};
    private int currentDocId = 0;
    private int sentenceId = 0;

    public FieldValuesSentenceIterator(Config config) throws IOException {
        Path path = Paths.get(config.getIndexPath());
        Directory directory = FSDirectory.open(path);
        reader = DirectoryReader.open(directory);
        fieldName = config.getFieldName();
    }

    @Override
    public String nextSentence() {
        try {
            while(sentenceId >= sentences.length && currentDocId < reader.numDocs()){
                IndexableField field = reader.document(currentDocId++).getField(fieldName);
                if (field != null) {
                    String text = field.stringValue();
                    sentences = new String[1];
                    sentences = text
                            .replaceAll("-", " ")
                            .replaceAll(":", " ")
                            .replaceAll("'", " ")
                            .replaceAll(" +", " ")
                            .replaceAll("\\(", " ")
                            .replaceAll("\\)", " ")
                            .replaceAll("\"", " ")
                            .replaceAll("°", " ")
                            .replaceAll(",", " ")
                            .replaceAll("\\.|;", " ")
                            .split("\\.|;");  // use character '.' and ';' to split sentences
//                    log.info("FULL TEXT: {}", text);
                    sentenceId = 0;
                }
            }

            if (sentenceId < sentences.length){
//                log.info("SENTENCE {}/{}: {}", sentenceId, sentences.length, sentences[sentenceId].trim());
                return sentences[sentenceId++].trim();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return sentenceId < sentences.length || currentDocId < reader.numDocs();
    }

    @Override
    public void reset() {
        currentDocId = 0;
    }

    @Override
    public void finish() {

    }

    @Override
    public SentencePreProcessor getPreProcessor() {
        return null;
    }

    @Override
    public void setPreProcessor(SentencePreProcessor preProcessor) {

    }

    public int getNumDocs(){
        return reader.numDocs();
    }
}