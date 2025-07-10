import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestTSVParsing {
    
    public static void main(String[] args) {
        String tsvContent = "barcode\tquantity\ncycle1\t30\npiz1\t25\na1b2c3d4e5\t100\n";
        
        System.out.println("Testing TSV content:");
        System.out.println(tsvContent);
        
        // Test if the content looks correct
        String[] lines = tsvContent.split("\n");
        System.out.println("\nParsed lines:");
        for (int i = 0; i < lines.length; i++) {
            System.out.println("Line " + (i + 1) + ": [" + lines[i] + "]");
        }
        
        // Test tab splitting
        if (lines.length > 1) {
            String[] headers = lines[0].split("\t");
            System.out.println("\nHeaders:");
            for (String header : headers) {
                System.out.println("Header: [" + header + "]");
            }
            
            if (lines.length > 1) {
                String[] firstRow = lines[1].split("\t");
                System.out.println("\nFirst data row:");
                for (String field : firstRow) {
                    System.out.println("Field: [" + field + "]");
                }
            }
        }
    }
} 