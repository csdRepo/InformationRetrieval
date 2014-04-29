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
import java.util.ArrayList;
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
    private final String colPath;
    private double avgl = 0;
    private final Map<Integer, DocInfo> docmap; 
    private final Map<String, VocInfo> vocab;
    private final HashSet<String> stopwords;
    
    public QueryValuate(String fpEN,String fpGR, String colPath) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        this.colPath = colPath;
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
    
    public Map queryOKAPI (String query) throws IOException{
        TreeMap<Double, Integer> sim = new TreeMap<>();
        Map<String,Double> map_weights=weights(query);
        for (Map.Entry<Integer, DocInfo> entry : this.docmap.entrySet()){
            double simDqi=processOKAPI(query, entry.getValue(), entry.getKey(), map_weights);
            if(sim.containsKey(simDqi)) simDqi = simDqi+0.00000000001;
            if(simDqi!=0.0)
                sim.put(simDqi, entry.getKey());
        }
        return sim.descendingMap();
    }
    
    private double processOKAPI (String query, DocInfo doc, int docid,Map<String,Double> weights_map) throws IOException{
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        double sum = 0;
        double weight;
        StringTokenizer tok = new StringTokenizer(query, delimiter, true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            token=Stemmer.Stem(token);
            if(token.length()>1 && !this.stopwords.contains(token)){
                if(weights_map.containsKey(token)){
                  //  System.out.println("to exw sou lew re");
                    weight=weights_map.get(token);
                    if (weight>1.0 || weight<0){
                        weight=1.0;
                    }
                   // System.out.println("to varos einai:"+weight);
                }
                else{
                weight=1.0;
                }
                
                double IDFqi=this.IDFqi(token)*weight;
                double fqiD=this.fqiD(token, docid);
                sum = sum +(IDFqi*((fqiD*3.0)/(fqiD+(3.0*(0.25+0.75*(doc.docLength/this.avgl))))));
            }
        }
        return sum;
    }
    
    private Map<String, Double> weights(String query)throws NumberFormatException{
        String delimiter = " ";
        double sum = 0;
       
        Map<String,Double> map_strings= new HashMap<>();
        
        StringTokenizer tok = new StringTokenizer(query, delimiter, true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            String[] parts=token.split(":");
            for (String part : parts) {
                System.out.println(part);
            }
            if (parts.length>2 || parts.length<2){
               // System.out.println("ela malaka");
               // map_strings.put(parts[0], 1.0);
            }                
            else{
            try{
                 double weight =Double.parseDouble(parts[1]);
               //  System.out.println("--------------"+weight);
             //    token=parts[0];
                 map_strings.put(Stemmer.Stem(parts[0]), weight);
                    
                }catch(NumberFormatException e){
                    System.out.println("error casting to double");
                }
            }
    
        }
        

//        for(Map.Entry<String,Double> entry: map_strings.entrySet()){
//            System.out.println(entry.getKey()+" "+entry.getValue());
//        }
        return map_strings;
    
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
        String postFile =this.colPath+"/PostingFile.txt";
        RandomAccessFile rafPost = new RandomAccessFile(postFile, "r");

        if(!this.vocab.containsKey(term)) return 0;
        rafPost.seek(this.vocab.get(term).pPost);
        for(int i=0; i<this.vocab.get(term).df;i++){
            postLine = rafPost.readLine();
            int doc = Integer.parseInt(postLine.substring(0, postLine.indexOf(" ")));
            if(doc==docID){
                return Integer.parseInt(postLine.substring(postLine.indexOf(" ")+1, postLine.indexOf("[")-1));
            }
        }
        return 0;
    }
    
    public Map queryVS(String query) throws FileNotFoundException, IOException {
        Map<String,Double> wiq = this.wiq(query);
        double sumWIQpow=0;
        Map<Integer, Double> sumWQ = this.sumWQ(wiq);
        TreeMap<Double, Integer> sim = new TreeMap<>();
             
        for(Map.Entry<String, Double> entry : wiq.entrySet()){
            sumWIQpow=sumWIQpow+entry.getValue()*entry.getValue();
        }
        
        for(Map.Entry<Integer, Double> entry : sumWQ.entrySet()){
            double tmsim = entry.getValue()/(Math.sqrt(this.docmap.get(entry.getKey()).norm*sumWIQpow));
            sim.put(tmsim, entry.getKey());
        }
        return sim.descendingMap();        
    }
    
    private Map sumWQ(Map<String,Double> wiq) throws FileNotFoundException, IOException{
        String file  =this.colPath+"/PostingFile.txt";
        Map<Integer,Double> sumWQ = new HashMap<>();
        for (Map.Entry<String,Double> entry : wiq.entrySet()){
            if(this.vocab.containsKey(entry.getKey())){
                int df = this.vocab.get(entry.getKey()).df;
                int post = this.vocab.get(entry.getKey()).pPost;
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(post);
                for(int i=0;i<df;i++){
                    String str = raf.readLine();
                    int docid = Integer.parseInt(str.substring(0, str.indexOf(" ")));
                    double wij = Double.parseDouble(str.substring(str.lastIndexOf(" ")));
                    if(sumWQ.containsKey(docid)){
                        sumWQ.put(docid,sumWQ.get(docid)+(wij*entry.getValue()));
                    }
                    else{
                        sumWQ.put(docid, wij*entry.getValue());
                    }
                } 
            }
        }
        return sumWQ;
    }
    
    private Map wiq(String query){
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        Map<String,Double> tf = new HashMap<>();
        double maxtf = 0;
        
        StringTokenizer tok = new StringTokenizer(query, delimiter, true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            token=Stemmer.Stem(token);
            if(token.length()>1 && !this.stopwords.contains(token)){
                if(tf.containsKey(token)){
                    tf.put(token, tf.get(token)+1.0);
                    if(tf.get(token)>maxtf) maxtf = tf.get(token);
                }
                else{
                    tf.put(token, 1.0);
                    if(tf.get(token)>maxtf) maxtf = tf.get(token);
                }
            }   
        }
        for (Map.Entry<String, Double> entry : tf.entrySet()){
            if(this.vocab.containsKey(entry.getKey())){
                entry.setValue(entry.getValue()/maxtf);
                entry.setValue(entry.getValue()*this.vocab.get(entry.getKey()).idf);
            }
            else entry.setValue(0.0);
            //System.out.println(entry.getKey()+" "+entry.getValue());
        }
        
        return tf;
    }
    
    private double log2(double a, double b ){
        return Math.log(a)/Math.log(b);
    }
    
    private void initVocab() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        String str;
        String file = this.colPath+"/VocabularyFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        
        while ((str = in.readLine()) != null){
            String[] temp = str.split(" ");
            this.vocab.put(temp[0], new VocInfo(Integer.parseInt(temp[1]),
                    Double.parseDouble(temp[2]), Integer.parseInt(temp[3])));
        }
    }
    
    public String getFilePath(int docID) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        String file = this.colPath+"/DocumentsFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String str;
        while ((str = in.readLine()) != null){
            int fdocid = Integer.parseInt(str.substring(0, str.indexOf(" ")));
            if(fdocid==docID) return str.substring(str.indexOf(" ")+1, str.lastIndexOf(" "));
        }
        return null;
    }
    
    public ArrayList getSnippet(int docID, String query, String docPath) throws FileNotFoundException, IOException{
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        ArrayList<String> snippet = new ArrayList<>();
        
        StringTokenizer tok = new StringTokenizer(query, delimiter, true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            token=Stemmer.Stem(token);
            if(token.length()>1 && !this.stopwords.contains(token)){
                if(this.vocab.containsKey(token)){
                    int pPost = this.vocab.get(token).pPost;
                    String file = this.colPath+"/PostingFile.txt";
                    RandomAccessFile raf = new RandomAccessFile(file, "r");
                    raf.seek(pPost);
                    String str;
                    for(int i=0;i<this.vocab.get(token).df;i++){
                        str = raf.readLine();
                        int docid = Integer.parseInt(str.substring(0, str.indexOf(" ")));
                        if(docid==docID){
                            String line;
                            RandomAccessFile rafdoc = new RandomAccessFile(docPath, "r");
                            //System.out.println(str);
                            str=str.substring(str.indexOf("[")+1, str.lastIndexOf("]"));
                            //System.out.println(str);
                            StringTokenizer tok1 = new StringTokenizer(str, "[] ,", true);
                            while(tok1.hasMoreTokens()){
                                String token1 = tok1.nextToken();
                                String del = "[] ,";
                                if(!del.contains(token1)){
                                    byte[] thebytearray;
                                    //thebytearray=rafdoc.readLine().getBytes("UTF-8");
                                    //String changed=new String(thebytearray, Charset.forName("UTF-8"));
                                    
                                    //System.out.println(token1);
                                    rafdoc.seek(Integer.parseInt(token1));
                                    snippet.add(rafdoc.readLine());
                                    //snippet.add(rafdoc.readLine());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return snippet;
    }
    
    private void initDocMap() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        String file = this.colPath+"/DocumentsFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        
        String str;
        while ((str = in.readLine()) != null){
            int docid = Integer.parseInt(str.substring(0, str.indexOf(" ")));
            double norm = Double.parseDouble(str.substring(str.lastIndexOf("_")+1,str.lastIndexOf("*")));
            //double norm = Double.parseDouble(str.substring(str.lastIndexOf("_")+1, str.lastIndexOf(" ")));
            int length=Integer.parseInt(str.substring(str.lastIndexOf("*")+1));
            this.avgl=this.avgl+length;
            this.docmap.put(docid, new DocInfo(norm, length));
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
        private final int docLength;
        public DocInfo(double norm, int docLength){
            this.norm=norm;
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
