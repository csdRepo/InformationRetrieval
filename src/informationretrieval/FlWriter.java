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
        this.writeDocumentsFile();
    }
    
    private void writeVocabulary(FlIndexer fi) throws IOException{
        File file = new File("CollectionIndex/VocabularyFile.txt");

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
    
    private void writeDocumentsFile() throws IOException{
        File docfile = new File("CollectionIndex/DocumentsFile.txt");
        File folder = new File("files/documentCollection/novels");
        File[] listOfFiles = folder.listFiles();

        FileWriter fw = new FileWriter(docfile.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            int i=0;
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    bw.write(i+" "+file.getCanonicalPath()+" "+file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.'))+"\n");
                    //System.out.println(file.getCanonicalPath());
                } 
            }
        }
         
        System.out.println("Done creating DocumentsFile.txt");
    }
}
