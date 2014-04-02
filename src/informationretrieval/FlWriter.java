/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author smyrgeorge
 */
public class FlWriter {
    //private final String fldpath;
    private final FlIndexer fi;
    
    public FlWriter(FlIndexer fi) throws IOException{
        //this.fldpath=fldPath;
        this.fi=fi;
        File dir = new File("CollectionIndex");
        dir.mkdir();
        
        this.writeVocabulary(fi);
    }
    
    private void writeVocabulary(FlIndexer fi) throws IOException{
        File file = new File("CollectionIndex/VocabularyFile.txt");
 
        String content = "This is the content to write into file";

//        if (!file.exists()) {
//                file.createNewFile();
//        }
 
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            for (TermNode term : fi.terms) {
                bw.write(term.getTerm()+" "+term.getDf()+"\n");
            }
        }
 
        System.out.println("Done creating VocabularyFile.txt");
    }
}
