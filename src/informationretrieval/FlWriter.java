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
    private Map<String, Integer> docmap;
    private Map<String, Double> docnorm;
    private Map<String, Double> docnormpow;
    
    
    public FlWriter(FlIndexer fi, String collectioPath) throws IOException{
        File dir = new File("CollectionIndex");
        dir.mkdir();
        
        this.initDocmap(collectioPath);
        this.writeVocabulary(fi);
        this.writeDocumentsFile(collectioPath, fi);
    }
    
    private static double log2(double a, double b ){
        return Math.log(a) / Math.log(b);
    }
    
    private void writeVocabulary(FlIndexer fi) throws IOException{
        this.docnorm = new HashMap<>();
        this.docnormpow = new HashMap<>();
        
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
                    double idf = ((double)this.docmap.size())/entry.getValue().getDf();
                    //double idf = ((double)entry.getValue().getDf())/this.docmap.size();
                    idf=log2(idf, 2);
                    bw.write(entry.getValue().getTerm()+" "+entry.getValue().getDf()+" "+Double.toString(idf)+" "+position+"\n");
                 
                    tf=entry.getValue().getTfList();
                    files=entry.getValue().getFileList();
                    int i=tf.size();
                    for (int j=0; j<i; j++){
                        double tfidf=idf*((double)tf.get(j)/fi.getMaxTF(files.get(j)));
                        double tfidfpow=Math.pow(tfidf, 2);
                       // System.out.println(tfidf);
                        //System.out.println(files.get(j));
                        if(this.docnorm.containsKey(files.get(j))){
                            this.docnorm.put(files.get(j), this.docnorm.get(files.get(j))+tfidf);
                            this.docnormpow.put(files.get(j), this.docnormpow.get(files.get(j))+tfidfpow);
                        }
                        else{
                            this.docnorm.put(files.get(j), tfidf);
                            this.docnormpow.put(files.get(j), tfidfpow);
                        }
                        
                        token=this.docmap.get(files.get(j))+" "+tf.get(j)+" "+Double.toString(tfidf)+" "+entry.getValue().multiMap.get(files.get(j))+"\n";
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
        this.docmap = new HashMap<>();
        int i=0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                this.docmap.put(file.getPath(), i);
                i++;
            }
        }
        
    }

    
    private void writeDocumentsFile(String collectionPath, FlIndexer fi) throws IOException{
        File docfile = new File("CollectionIndex/DocumentsFile.txt");
        File folder = new File(collectionPath);
        File[] listOfFiles = folder.listFiles();
        //this.docmap = new HashMap<>();

        FileWriter fw = new FileWriter(docfile.getAbsoluteFile());
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            int i=0;
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    bw.write(i+" "+file.getPath()+" ");
//                    this.docmap.put(file.getPath(), i);
                    int dot = file.getAbsolutePath().lastIndexOf('.');
                    bw.write(file.getAbsolutePath().substring(dot + 1)+" "+Double.toString(this.docnorm.get(file.getPath()))+" "+this.docnormpow.get(file.getPath())+"\n");
                    //bw.write(file.getAbsolutePath().substring(dot + 1)+" "+fi.getMaxTF(file.getPath())+"\n");
                    i++;
                } 
            }
        }
        System.out.println("Done creating DocumentsFile.txt");
    }
}
