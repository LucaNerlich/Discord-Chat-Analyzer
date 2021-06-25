package analyzer;

import com.google.gson.Gson;

import java.awt.print.Book;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Discord Chat Analyzer");
        try {
            // create Gson instance
            Gson gson = new Gson();
            
            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("logs/test.json"));
            
            // convert JSON string to Book object
            Book book = gson.fromJson(reader, Book.class);
            
            // print book
            System.out.println(book);
            
            // close reader
            reader.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Ending Discord Chat Analyzer");
    }
}
