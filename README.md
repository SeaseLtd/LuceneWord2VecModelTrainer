# LuceneWord2VecModelTrainer

The LuceneWord2VecModelTrainer is a tool that is able to generate a Word2Vec model starting from a specific field of a Apache Lucene index.


## How to build
Create a fat-jar containing all dependencies
```
./gradlew shadowJar
```

## How to use
Execute the Training by invoking the following command.  
The required parameters are:
- `-p` path of the Lucene index where to read the index from
- `-f` field where to read the text to use for training
- `-o` output file name

Example
```
java -jar build/libs/LuceneWord2VecModelTrainer.jar -p <index_path> -f <field_name> -o <model_file>
```
