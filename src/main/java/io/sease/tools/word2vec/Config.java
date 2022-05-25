package io.sease.tools.word2vec;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Config {
    private final String indexPath;
    private final String fieldName;
    private final String modelFilePath;
}
