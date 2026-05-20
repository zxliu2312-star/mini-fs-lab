import filesystem.FileSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        FileSystem fileSystem = new FileSystem();
        StringBuilder output = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            handleCommand(fileSystem, line, output);
        }

        if (output.length() > 0) {
            System.out.print(output);
        }
    }

    private static void handleCommand(FileSystem fileSystem, String line, StringBuilder output) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) {
            return;
        }

        switch (parts[0]) {
            case "MKDIR":
                if (parts.length == 2) {
                    fileSystem.mkdir(parts[1]);
                }
                break;
            case "TOUCH":
                if (parts.length == 3) {
                    try {
                        long size = Long.parseLong(parts[2]);
                        fileSystem.touch(parts[1], size);
                    } catch (NumberFormatException ignored) {
                    }
                }
                break;
            case "LS":
                if (parts.length == 2) {
                    appendLines(output, fileSystem.ls(parts[1]));
                }
                break;
            case "INFO":
                if (parts.length == 2) {
                    output.append(fileSystem.info(parts[1])).append(System.lineSeparator());
                }
                break;
            default:
                break;
        }
    }

    private static void appendLines(StringBuilder output, List<String> lines) {
        for (String item : lines) {
            output.append(item).append(System.lineSeparator());
        }
    }
}
