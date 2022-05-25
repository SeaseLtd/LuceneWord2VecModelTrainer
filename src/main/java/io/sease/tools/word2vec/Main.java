package io.sease.tools.word2vec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;

import java.io.IOException;


@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        Config config = parseConfiguration(args);

        SentenceIterator iter = new FieldValuesSentenceIterator(config);
        while (iter.hasNext()) {
            log.info(iter.nextSentence());
        }

        iter.reset();

        Word2Vec vec = new Word2Vec.Builder()
                .layerSize(100)
                .windowSize(5)
                .iterate(iter)
                .build();
        vec.fit();

        WordVectorSerializer.writeWord2VecModel(vec, config.getModelFilePath());
    }

    private static Config parseConfiguration(String[] args){

        final Options options = new Options();
        options.addOption(Option.builder()
                        .option("p")
                        .longOpt("path")
                        .hasArg(true)
                        .required(true)
                        .desc("Path of the index folder")
                .build());
        options.addOption(Option.builder()
                        .option("f")
                        .longOpt("field")
                        .hasArg(true)
                        .required(true)
                        .desc("FieldName where the text has to be read from")
                .build());
        options.addOption(Option.builder()
                        .option("o")
                        .longOpt("output")
                        .hasArg(true)
                        .required(true)
                        .desc("Generates model name")
                .build());
        options.addOption(Option.builder()
                        .option("h")
                        .longOpt("help")
                        .hasArg(false)
                        .desc("Help")
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")){
                helper.printHelp("Usage:", options);
                System.exit(0);
            }

            String indexPath = cmd.getOptionValue("path");
            String field = cmd.getOptionValue("field");
            String modelFile = cmd.getOptionValue("output");

            log.info("indexPath = {}", indexPath);
            log.info("field = {}", field);
            log.info("modelFile = {}", modelFile);

            return Config.builder()
                    .indexPath(indexPath)
                    .fieldName(field)
                    .modelFilePath(modelFile)
                    .build();
        } catch (ParseException e) {
//            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }
        return null;
    }

}