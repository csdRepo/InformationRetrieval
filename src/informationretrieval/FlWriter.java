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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author smyrgeorge
 */
public class FlWriter {
    private final FlIndexer fi;
    private Map<String, Integer> docmap;
    
    
    public FlWriter(FlIndexer fi) throws IOException{
        this.fi=fi;
        File dir = new File("CollectionIndex");
        dir.mkdir();
        
        this.writeDocumentsFile();
        this.writeVocabulary(fi);
    }
    
    private void writeVocabulary(FlIndexer fi) throws IOException{
        LinkedList<Integer> tf;
        LinkedList<String> files;
        String token;
        int position=0;
        File file = new File("CollectionIndex/VocabularyFile.txt");
        File Postingfile = new File("CollectionIndex/PostingFile.txt");
        
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
        FileWriter fw_posting = new FileWriter(Postingfile.getAbsolutePath());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            try (BufferedWriter bw_posting = new BufferedWriter(fw_posting)){
                for (Map.Entry<String, TermNode> entry : fi.mapTerms.entrySet()){
                    bw.write(entry.getValue().getTerm()+" "+entry.getValue().getDf()+" "+position+"\n");
                 
                    tf=entry.getValue().getTfList();
                    files=entry.getValue().getFileList();
                    int i=tf.size();
                    for (int j=0; j<i; j++){
                       
                        token=this.docmap.get(files.get(j))+" "+tf.get(j)+" "+entry.getValue().multiMap.get(files.get(j))+"\n";
                        position= position + token.length();
                        
                        bw_posting.write(token);
                    }
                }
            }
        }
 
        System.out.println("Done creating VocabularyFile.txt");
        System.out.println("Done creating PostingFile.txt");
    }

    
    private void writeDocumentsFile() throws IOException{
        File docfile = new File("CollectionIndex/DocumentsFile.txt");
        File folder = new File("files/documentCollection/all");
        File[] listOfFiles = folder.listFiles();
        this.docmap = new HashMap<>();

        FileWriter fw = new FileWriter(docfile.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            int i=0;
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    bw.write(i+" "+file.getPath()+" ");
                    this.docmap.put(file.getPath(), i);
                    int dot = file.getAbsolutePath().lastIndexOf('.');
                    bw.write(file.getAbsolutePath().substring(dot + 1)+"\n");
                    i++;
                } 
            }
        }
         
        System.out.println("Done creating DocumentsFile.txt");
    }
}
