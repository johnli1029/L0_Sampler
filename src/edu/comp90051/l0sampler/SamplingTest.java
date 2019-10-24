package edu.comp90051.l0sampler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SamplingTest {
  public static void printTestResult(Map<Integer, Integer> distroMap, Path outputPath) throws FileNotFoundException {
    try (
        PrintWriter output = new PrintWriter(outputPath.toFile())
    ) {
      for (Map.Entry<Integer, Integer> entry : distroMap.entrySet())
        output.println(entry.getKey() + "," + entry.getValue());
    }
  }

  public static Integer runExperiment(L0Sampler l0Sampler, Path data) throws IOException, FailToRetrieveException {
    Files.lines(data)
        .skip(1)
        .forEach(line -> {
          String[] record = line.split(",");
          l0Sampler.update(Integer.parseInt(record[0]), Integer.parseInt(record[1]));
        });

    return l0Sampler.output();
  }
}
