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
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import mitos.stemmer.Stemmer;

/**
 *
 * @author smyrgeorge
 */
public class QueryValuate {
    private final Map<Integer, DocInfo> docmap; 
    private final Map<String, VocInfo> vocab;
    private final HashSet<String> stopwords;
    
    public QueryValuate(String fpEN,String fpGR) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        this.vocab = new HashMap<>();
        this.docmap = new TreeMap<>();
        this.stopwords = new HashSet<>();
        
        this.initVocab();
        this.initDocMap();
        Stemmer.Initialize();
        this.initStopWords(fpEN);
        this.initStopWords(fpGR);
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
    
    public void queryPpocessor(String query) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        double weight = this.queryWeight(query);
        Map<Integer,Double> sim = simCounter(weight);
        //System.out.println(weight);
    }
    
    private double queryWeight(String query){
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        Map<String,Double> queryMap =new HashMap<>();
        double weight=0;
        int maxtf = 0;
        int tf;
        
        StringTokenizer tok = new StringTokenizer(query, delimiter, true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            token=Stemmer.Stem(token);
            if(token.length()>1 && !this.stopwords.contains(token)){
                if(queryMap.containsKey(token)){
                    tf=(int)(queryMap.get(token)+1);
                    if (tf>maxtf)maxtf=tf;
                    queryMap.put(token, (double)tf);
                }
                else{
                    queryMap.put(token, 1.00);
                    if (1>maxtf)maxtf=1;
                }   
            }
        }
        for (Map.Entry<String, Double> entry : queryMap.entrySet()){
            if(vocab.containsKey(entry.getKey())){
                double tfidf= (((double)entry.getValue())/maxtf)*vocab.get(entry.getKey()).idf;
                queryMap.put(entry.getKey(),Math.pow(tfidf, 2));
                weight=weight+queryMap.get(entry.getKey());
                System.out.println(entry.getKey()+" "+entry.getValue());
            }
            else{
                queryMap.put(entry.getKey(), 0.00);
                System.out.println(entry.getKey()+" "+entry.getValue());
            }
        }
        return weight;
    }
    
    private Map simCounter(Double weight) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        Map<Integer, Double> sim = new TreeMap<>();
        
        return sim;
    }
    
    private double log2(double a, double b ){
        return Math.log(a)/Math.log(b);
    }
    
    private void initVocab() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        String str;
        String file = "CollectionIndex/VocabularyFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        
        while ((str = in.readLine()) != null){
            String[] temp = str.split(" ");
            this.vocab.put(temp[0], new VocInfo(Integer.parseInt(temp[1]),
                    Double.parseDouble(temp[2]), Integer.parseInt(temp[3])));
        }
    }
    
    private void initDocMap() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        String file = "CollectionIndex/DocumentsFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        
        String str;
        while ((str = in.readLine()) != null){
            int docid = Integer.parseInt(str.substring(0, str.indexOf(" ")));
            double normpow = Double.parseDouble(str.substring(str.lastIndexOf(" ")));
            double norm = Double.parseDouble(str.substring(str.indexOf("_")+1, str.lastIndexOf(" ")));
            this.docmap.put(docid, new DocInfo(norm, normpow));
        }
    }
    
    private void initStopWords(String fp) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fp), "UTF8"));
        String str;
        while ((str = in.readLine()) != null){
            this.stopwords.add(Stemmer.Stem(str));
        }
    }
    
    private class DocInfo{
        private final double norm;
        private final double normPow;
        public DocInfo(double norm, double normPow){
            this.norm=norm;
            this.normPow=normPow;
        }
    }
    
    private class VocInfo{
        private final int df;
        private final double idf;
        private final int pPost;
        public VocInfo(int df, double idf, int pPost){
            this.df=df;
            this.idf=idf;
            this.pPost=pPost;
        }
    }
}
