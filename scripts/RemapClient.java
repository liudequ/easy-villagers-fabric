package net.fengye.easyvillagers.build;

import java.io.*;
import java.nio.file.*;
import net.fabricmc.loader.impl.lib.tinyremapper.*;
import net.fabricmc.loader.impl.lib.tinyremapper.api.*;

public class RemapClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: java RemapClient <inputJar> <outputJar> <mappingsFile>");
            System.exit(1);
        }

        Path inputJar = Paths.get(args[0]);
        Path outputJar = Paths.get(args[1]);
        Path mappingsFile = Paths.get(args[2]);

        System.out.println("Reading mappings from " + mappingsFile);
        IMappingProvider mappingProvider = acceptor -> {
            try (BufferedReader reader = Files.newBufferedReader(mappingsFile)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    String[] parts = line.split("\t");
                    if (parts.length < 3) continue;
                    switch (parts[0]) {
                        case "CLASS":
                            acceptor.acceptClass(parts[1], parts[2]);
                            break;
                        case "FIELD":
                            if (parts.length >= 5) {
                                acceptor.acceptField(new IMappingProvider.Member(parts[1], parts[3], parts[2]), parts[4]);
                            }
                            break;
                        case "METHOD":
                            if (parts.length >= 5) {
                                acceptor.acceptMethod(new IMappingProvider.Member(parts[1], parts[3], parts[2]), parts[4]);
                            }
                            break;
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };

        TrLogger logger = (level, msg) -> {
            if (level == TrLogger.Level.ERROR || level == TrLogger.Level.WARN) {
                System.err.println("[" + level + "] " + msg);
            }
        };

        System.out.println("Building TinyRemapper...");
        TinyRemapper remapper = TinyRemapper.newRemapper(logger)
                .withMappings(mappingProvider)
                .renameInvalidLocals(true)
                .rebuildSourceFilenames(true)
                .build();

        Files.deleteIfExists(outputJar);
        try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(outputJar).build()) {
            outputConsumer.addNonClassFiles(inputJar, NonClassCopyMode.UNCHANGED, remapper);
            InputTag tag = remapper.createInputTag();
            remapper.readInputsAsync(tag, inputJar).join();
            remapper.apply(outputConsumer, tag);
        } finally {
            remapper.finish();
        }

        System.out.println("Done! Output jar created at: " + outputJar);
    }
}
