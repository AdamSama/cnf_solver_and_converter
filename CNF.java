import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CNF {
    private List<List<String>> content;
    private Logger logger;
    private String command = "python converter.py";

    private List<List<String>> fileReader(String fileName) throws IOException{
        List<List<String>> cont = new ArrayList<>();
        File file = new File(fileName);
        String st;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while((st = br.readLine()) != null) {
                List<String> list = new ArrayList<>(Arrays.asList(st.trim().split("\\s+")));
                cont.add(list);
            }
        } catch (FileNotFoundException f) {
            logger.log(Level.SEVERE, String.format("%s does not exist", fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cont;
    }
    CNF(String inputFile, Boolean convert) throws IOException {
        String fileName = inputFile;
        logger = Logger.getLogger(CNF.class.getName());
        if (Boolean.TRUE.equals(convert)) {
            // System.out.println("True");
            Process p = Runtime.getRuntime().exec(command + " " + inputFile);
            try(BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
    
                while ((line = input.readLine()) != null) {
                    System.out.println(line);
                }
            }
            fileName = "output.txt";
        }
        // if (Boolean.TRUE.equals(convert)) content = converter(fileName);
        content = fileReader(fileName);
    }
    public List<String> getAtoms() {
        List<String> atoms = new ArrayList<>();
        for (List<String> line : this.content) {
            for (String each : line) {
                if (each.charAt(0) == '!') {
                    if (!atoms.contains(each.substring(1, each.length())))
                        atoms.add(each.substring(1, each.length()));
                }   
                else {
                    if (!atoms.contains(each))
                        atoms.add(each);
                }
            } 
        }
        return atoms;
    }
    public List<List<String>>getter(){
        return this.content;
    }
    
}
