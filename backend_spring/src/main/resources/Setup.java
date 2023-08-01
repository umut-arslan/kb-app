import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class Setup {

    private static String root = "../../../../";
    public static void main(String[] args) throws IOException {
        // rename packages
        renamePackage(new File(root + "backend_spring/src/main/java/com/kb/template"),
                new File(root + "backend_spring/src/main/java/com/kb/" + args[0]));
        renamePackage(new File(root + "backend_spring/src/test/java/com/kb/template"),
                new File(root + "backend_spring/src/test/java/com/kb/" + args[0]));

        // rename kb-app occurances to given arg
        String[] files = {"frontend_vue/pom.xml", "Dockerfile",
                ".github/workflows/ci-cd_job.yml", "pom.xml",
                "k8s/deploy.yml", "k8s/cert-manager.yml",
                "k8s/ingress.yml", "docker-compose.yml",
                "backend_spring/pom.xml", "backend_spring/src/main/resources/application-dev.properties",
                "backend_spring/src/main/resources/application-test.properties",
                ".github/workflows/ci_job.yml"};

        for (int i = 0; i < files.length; i++) {
            Path path = Paths.get(root + files[i]);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path), charset);
            content = content.replaceAll("kb-app", args[0]);
            content = content.replaceAll("kb-app", args[0]);
            Files.write(path, content.getBytes(charset));
        }
    }
    private static void renamePackage(File srcFile, File destFile){
        srcFile.renameTo(destFile);
        srcFile.delete();
    }

}