import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
    File question = new File("src/Exercises.txt");
    System.out.println(question.createNewFile());
}
}
