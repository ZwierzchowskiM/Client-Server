package org.zwierzchowski.marcin.utils;

import org.jooq.codegen.GenerationTool;

import java.nio.file.Files;
import java.nio.file.Path;

public class DbGenerationTool {

  public static void main(String[] args) throws Exception {
    GenerationTool.generate(
        Files.readString(
            Path.of(
                "C:\\Users\\marci\\IdeaProjects\\ZR\\client-server\\client-server\\src\\main\\resources\\jooq-config.xml")));
    }
}
