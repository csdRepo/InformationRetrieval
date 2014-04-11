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
    private double avgl = 0;
    private final Map<Integer, DocInfo> docmap; 
    private final Map<String, VocInfo> vocab;
    private final HashSet<String> stopwords;
    
    public QueryValuate(String fpEN,String fpGR) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        this.vocab = new HashMap<>();
        this.docmap = new TreeMap<>();
        this.stopwords = new HashSet<>();
        
        this.initVocab();
        this.initDocMap();
        this.avgl=this.avgl/this.docmap.size();
        Stemmer.Initialize();
        this.initStopWords(fpEN);
        this.initStopWords(fpGR);
    }
    
    public void queryOKAPI (String query) throws IOException{
        Map<Double, Integer> sim = new TreeMap<>();
        for (Map.Entry<Integer, DocInfo> entry : this.docmap.entrySet()){
            double simDqi=processOKAPI(query, entry.getValue(), entry.getKey());
            sim.put(simDqi, entry.getKey());
            System.out.println(simDqi+" "+entry.getKey());
        }
    }
    
    private double processOKAPI (String query, DocInfo doc, int docid) throws IOException{
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        double sum = 0;
        
        StringTokenizer tok = new StringTokenizer(query, delimiter, true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            token=Stemmer.Stem(token);
            if(token.length()>1 && !this.stopwords.contains(token)){
                double IDFqi=this.IDFqi(token);
                //System.out.println(IDFqi);
                double fqiD=this.fqiD(token, docid);
                //System.out.println(fqiD);
                sum = sum +(IDFqi*((fqiD*3.0)/(fqiD+(3.0*(0.25+0.75*(doc.docLength/this.avgl))))));
            }
        }
        return sum;
    }

    private double IDFqi(String word){
        double IDFqi;
        if(this.vocab.containsKey(word)){
            IDFqi=this.docmap.size()-this.vocab.get(word).df+0.5;
            IDFqi=IDFqi/(this.vocab.get(word).df+0.5);
            //IDFqi=this.log2(IDFqi, 2.0);
        }
        else{
            IDFqi=this.docmap.size()+0.5;
            IDFqi=IDFqi/0.5;
            //IDFqi=this.log2(IDFqi, 2.0);
        }
        return IDFqi;
    }
    
    private int fqiD(String term, int docID) throws FileNotFoundException, IOException{
        String postLine;
        String postFile = "CollectionIndex/PostingFile.txt";
        RandomAccessFile rafPost = new RandomAccessFile(postFile, "r");

        rafPost.seek(this.vocab.get(term).pPost);
        for(int i=0; i<this.vocab.get(term).df;i++){
            postLine = rafPost.readLine();
            int doc = Integer.parseInt(postLine.substring(0, postLine.indexOf(" ")));
            if(doc==docID){
                return Integer.parseInt(postLine.substring(postLine.indexOf(" ")+1, postLine.lastIndexOf(" ")));
            }
        }
        return 0;
    }
    
    public void queryProcessor(String query) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        double weight = this.queryWeight(query);
        Map<Double,Integer> sim = simCounter(weight);
        for (Map.Entry<Double, Integer> entry : sim.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
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
        Map<Double, Integer> sim = new TreeMap<>();
        for (Map.Entry<Integer, DocInfo> entry : this.docmap.entrySet()){
            double value = entry.getValue().norm/Math.sqrt(entry.getValue().normPow*weight);
            if(sim.containsKey(value)) value = value + 0.000000000000001;
            sim.put(value, entry.getKey());
        }
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
            double normpow = Double.parseDouble(str.substring(str.lastIndexOf(" "),str.lastIndexOf("*")));
            double norm = Double.parseDouble(str.substring(str.lastIndexOf("_")+1, str.lastIndexOf(" ")));
            int length=Integer.parseInt(str.substring(str.lastIndexOf("*")+1));
            this.avgl=this.avgl+length;
            this.docmap.put(docid, new DocInfo(norm, normpow,length));
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
        private final int docLength;
        public DocInfo(double norm, double normPow, int docLength){
            this.norm=norm;
            this.normPow=normPow;
            this.docLength=docLength;
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
