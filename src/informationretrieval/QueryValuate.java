/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import mitos.stemmer.Stemmer;

/**
 *
 * @author smyrgeorge
 */
public class QueryValuate {
    private final Map<String, VocInfo> vocab;
    private final Map<String, Integer> docmaxTF;
    
    public QueryValuate() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        this.vocab = new HashMap<>();
        this.docmaxTF = new HashMap<>();
        this.initVocab();
    }
    
    public void query(String word) throws FileNotFoundException, IOException{
        String postLine;
        String stWord=Stemmer.Stem(word).toLowerCase();
        String postFile = "CollectionIndex/PostingFile.txt";
        RandomAccessFile rafPost = new RandomAccessFile(postFile, "r");

        rafPost.seek(this.vocab.get(stWord).pPost);
        for(int i=0; i<this.vocab.get(stWord).idf;i++){
            postLine = rafPost.readLine();
            System.out.println(postLine);
            this.parsePost(postLine);
        }
    }
    private void parsePost(String postLine) throws FileNotFoundException, IOException{
        StringTokenizer tok = new StringTokenizer(postLine, " ,[]", true);
        int docID = 0,tf = 0;
        int i=0;
        
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            if(!" ,[]".contains(token)){
                if(i==0) docID=Integer.parseInt(token);
                else if(i==1) tf=Integer.parseInt(token);
                else this.openDoc(docID, Integer.parseInt(token));
                i++;
            } 
        }
    }
    private void openDoc(int docID, int pos) throws FileNotFoundException, IOException{
        String docs = "CollectionIndex/DocumentsFile.txt";
        RandomAccessFile rafDocs = new RandomAccessFile(docs, "r");
        String docsLine;
        
        while ((docsLine = rafDocs.readLine()) != null){
            if(docID==Integer.parseInt(docsLine.substring(0, docsLine.indexOf(' ')))){
                String docfile = docsLine.substring(docsLine.indexOf(" ")+1, docsLine.lastIndexOf(" "));
                System.out.println(docfile.substring(0, docfile.lastIndexOf(" ")));
                RandomAccessFile doc = new RandomAccessFile(docfile.substring(0, docfile.lastIndexOf(" ")), "r");
                doc.seek(pos);
                System.out.println(doc.readLine());
            }
            
        }
        
    }
    private void initVocab() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        String str;
        String file = "CollectionIndex/VocabularyFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        
        while ((str = in.readLine()) != null){
            String[] temp = str.split(" ");
            this.vocab.put(temp[0], new VocInfo(Double.parseDouble(temp[1]), Integer.parseInt(temp[2])));
        }
    }
    
    private class VocInfo{
        private final double idf;
        private final int pPost;
        public VocInfo(double idf, int pPost){
            this.idf=idf;
            this.pPost=pPost;
        }
    }
}
