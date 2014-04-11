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
    private Map<String, Integer> docMap;
    private Map<String, Double> docNorm;
    private Map<String, Double> docNormPow;
    
    
    public FlWriter(FlIndexer fi, String collectioPath) throws IOException{
        File dir = new File("CollectionIndex");
        dir.mkdir();
        
        this.initDocmap(collectioPath);
        this.writeVocabPost(fi);
        this.writeDocumentsFile(collectioPath);
    }
    
    private void writeVocabPost(FlIndexer fi) throws IOException{
        this.docNorm = new HashMap<>();
        this.docNormPow = new HashMap<>();
        
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
                    double idf = ((double)this.docMap.size())/entry.getValue().getDf();
                    idf=log2(idf, 2);
                    bw.write(entry.getValue().getTerm()+" "+entry.getValue().getDf()+" "+idf+" "+position+"\n");
                 
                    tf=entry.getValue().getTfList();
                    files=entry.getValue().getFileList();
                    int i=tf.size();
                    for (int j=0; j<i; j++){
                        double tfidf=idf*((double)tf.get(j)/fi.getMaxTF(files.get(j)));
                        double tfidfpow=Math.pow(tfidf, 2);

                        
                        if(this.docNorm.containsKey(files.get(j))){
                            this.docNorm.put(files.get(j), this.docNorm.get(files.get(j))+tfidf);
                            this.docNormPow.put(files.get(j), this.docNormPow.get(files.get(j))+tfidfpow);
                        }
                        else{
                            this.docNorm.put(files.get(j), tfidf);
                            this.docNormPow.put(files.get(j), tfidfpow);
                        }
                        
                        token=this.docMap.get(files.get(j))+" "+tf.get(j)+" "
                                +entry.getValue().multiMap.get(files.get(j))+"\n";
                        position= position + token.length();
                        
                        bw_posting.write(token);
                    }
                }
            }
        }
 
        System.out.println("Done creating VocabularyFile.txt");
        System.out.println("Done creating PostingFile.txt");
    }
    private void initDocmap(String collectionPath){
        File folder = new File(collectionPath);
        File[] listOfFiles = folder.listFiles();
        this.docMap = new HashMap<>();
        int i=0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                this.docMap.put(file.getPath(), i);
                i++;
            }
        }
    }

    private double log2(double a, double b ){
        return Math.log(a)/Math.log(b);
    }
    
    private void writeDocumentsFile(String collectionPath) throws IOException{
        File docfile = new File("CollectionIndex/DocumentsFile.txt");
        File folder = new File(collectionPath);
        File[] listOfFiles = folder.listFiles();

        FileWriter fw = new FileWriter(docfile.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            int i=0;
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    bw.write(i+" "+file.getPath()+" ");
                    int dot = file.getAbsolutePath().lastIndexOf('.');
                    bw.write(file.getAbsolutePath().substring(dot + 1)+"_"+
                            Double.toString(this.docNorm.get(file.getPath()))+
                            " "+this.docNormPow.get(file.getPath())+"\n");
                    i++;
                } 
            }
        }
        System.out.println("Done creating DocumentsFile.txt");
    }
}
