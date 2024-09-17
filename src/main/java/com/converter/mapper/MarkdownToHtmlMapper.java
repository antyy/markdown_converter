package com.converter.mapper;

import com.converter.model.MapperConfig;
import com.converter.model.Mapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class MarkdownToHtmlMapper {
    private List<ElementMapper> rootMappersList;
    private List<ElementMapper> childMappersList;

    record ElementMapper(Pattern pattern, String format, String prefix, String suffix) {
    }

    public MarkdownToHtmlMapper(MapperConfig config) {
        init(config);
    }

    private void init(MapperConfig config) {
        this.rootMappersList = config.getMappings()
                .stream()
                .filter(Mapping::getNode)
                .map(t -> new ElementMapper(Pattern.compile(t.getPattern()), t.getFormat(), t.getNodeStart(), t.getNodeEnd()))
                .toList();
        this.childMappersList = config.getMappings()
                .stream()
                .filter(t -> !t.getNode())
                .map(t -> new ElementMapper(Pattern.compile(t.getPattern()), t.getFormat(), t.getNodeStart(), t.getNodeEnd()))
                .toList();
    }

    public void convert(String src, String dest) {
        File source = new File(src);
        File destFile = new File(dest);

        validateSourceFile(source);
        validateDestinationFile(destFile);

        try (FileReader fileReader = new FileReader(source);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             FileWriter fileWriter = new FileWriter(destFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            ElementMapper paragraph = new ElementMapper(null, "", "<p>", "</p>");
            String line;
            String prevLine = null;
            String nodeEnd = "";

            while ((line = bufferedReader.readLine()) != null) {

                String result = line;
                if (line.isEmpty()) {
                    if (prevLine != null) {
                        prevLine = prevLine.concat(nodeEnd);
                        nodeEnd = "";
                    }
                    result = line;
                } else {
                    String nodePrefix = "";
                    boolean nodeFound = false;
                    for (ElementMapper mapper : rootMappersList) {
                        Matcher matcher = mapper.pattern.matcher(line);
                        if (matcher.find()) {
                            nodeFound = true;
                            result = String.format(mapper.format, matcher.group(1));
                            if (!nodeEnd.equals(mapper.suffix()) && prevLine != null) {
                                prevLine = prevLine.concat(nodeEnd);
                                if (mapper.prefix() != null) {
                                    nodePrefix = mapper.prefix();
                                }
                                nodeEnd = mapper.suffix() == null ? "" : mapper.suffix();
                            }
                            break;
                        }
                    }
                    result = mapInline(result);
                    if (!nodeFound) {
                        if (!nodeEnd.equals(paragraph.suffix())) {
                            if (prevLine != null) {
                                prevLine = prevLine.concat(nodeEnd);
                            }
                            nodePrefix = paragraph.prefix;
                            nodeEnd = paragraph.suffix();
                        }
                    }
                    result = nodePrefix.concat(result);
                }
                if (StringUtils.hasLength(prevLine)) {
                    bufferedWriter.write(prevLine);
                    bufferedWriter.newLine();
                }
                prevLine = result;
            }
            prevLine = prevLine.concat(nodeEnd);

            bufferedWriter.write(prevLine);
            bufferedWriter.newLine();
        } catch (Exception e) {
            log.error("Failed to map a file.", e);
            throw new RuntimeException("Failed to map file");
        }
    }

    private String mapInline(String line) {
        for (ElementMapper mapper : childMappersList) {
            Matcher matcher = mapper.pattern.matcher(line);
            int start = 0;
            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                String prefix = line.substring(start, matcher.start());
                start = matcher.end();
                int groupCount = matcher.groupCount();
                String[] groups = new String[groupCount];
                for (int i = 0; i < groupCount; i++) {
                    groups[i] = matcher.group(i + 1);
                }
                result.append(prefix)
                        .append(String.format(mapper.format, groups));
            }
            line = result.append(line.substring(start)).toString();
        }
        return line;
    }

    private void validateSourceFile(File src) {
        if (!src.exists() || !src.isFile()) {
            log.error("Source file {} does not exist or is not a file.", src);
            throw new RuntimeException("Source " + src + "file does not exist or is not a file.");
        }
    }

    private void validateDestinationFile(File dest) {
        if (!dest.exists() || !dest.isFile()) {
            try {
                Path filePath = dest.toPath();
                if (filePath.getParent() != null) {
                    filePath.getParent().toFile().mkdirs();
                }
                dest.createNewFile();
            } catch (Exception e) {
                log.error("Target file {} does not exist or is not a file.", dest, e);
                throw new RuntimeException("Target " + dest + " file does not exist or is not a file.");
            }
        }
    }
}
