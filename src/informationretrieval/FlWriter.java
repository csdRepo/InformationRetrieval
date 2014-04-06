/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import com.google.common.collect.Multimap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

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
        LinkedList<Integer> tf;
        LinkedList<String> files;
        String token = null;
        int position=0;
        File file = new File("CollectionIndex/VocabularyFile.txt");
        File Postingfile = new File("CollectionIndex/PostingFile.txt");
//        if (!file.exists()) {
//                file.createNewFile();
//        }
        
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
        FileWriter fw_posting = new FileWriter(Postingfile.getAbsolutePath());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            try (BufferedWriter bw_posting = new BufferedWriter(fw_posting)){
                for (TermNode term : fi.terms) {
                    bw.write(term.getTerm()+" "+term.getDf()+" "+position+"\n");
                 
                    tf=term.getTfList();
                    files=term.getFileList();
                    int i=tf.size();
                    for (int j=0; j<i; j++){
                       
                        token=tf.get(j)+" "+term.multiMap.get(files.get(j))+"\n";
                        position= position + token.length();
                        
                        bw_posting.write(token);
                  
                    }
                  //  bw_posting();
                
              
                }
            }
        }
 
        System.out.println("Done creating VocabularyFile.txt");
    }
    private void writePosting(File file,TermNode term) throws IOException{
        

//        if (!file.exists()) {
//                file.createNewFile();
//        }
        
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
       
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
                if (file.isFile()) {
                    bw.write(i+" "+file.getCanonicalPath()+" ");
                    int dot = file.getAbsolutePath().lastIndexOf('.');
                    //int sep = file.getAbsolutePath().lastIndexOf('/');
                    bw.write(file.getAbsolutePath().substring(dot + 1)+"\n");
                    i++;
                    //System.out.println(file.getCanonicalPath());
                } 
            }
        }
         
        System.out.println("Done creating DocumentsFile.txt");
    }
}
