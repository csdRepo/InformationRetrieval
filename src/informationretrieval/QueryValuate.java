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
import mitos.stemmer.Stemmer;

/**
 *
 * @author smyrgeorge
 */
public class QueryValuate {
    private final Map<String, VocInfo> vocab;
    private final Map<String, Integer> docmaxTF;
    private final HashSet<String> stopwords;
    
    public QueryValuate(String fpEN,String fpGR) throws UnsupportedEncodingException, FileNotFoundException, IOException{
        this.vocab = new HashMap<>();
        this.docmaxTF = new HashMap<>();
        this.stopwords=new HashSet<>();
        this.initVocab();
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
    
    public void QueryPpocessor(String Query){
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        double idf;
        int tf;
        int maxtf = 0;
        Map<String,Double> queryMap =new HashMap<>();
        Map<String,Double> queryMapIDF =new HashMap<>();

        StringTokenizer tok = new StringTokenizer(Query, delimiter, true);
          //int posInseek = linepos;
        while (tok.hasMoreTokens()){
          String token = tok.nextToken();
          token=Stemmer.Stem(token);
          //posInseek=posInseek+token.length();
          if(token.length()>1 && !this.stopwords.contains(token)){
              if(queryMap.containsKey(token)){
                 tf=(int) (queryMap.get(token)+1);
                 if (tf>maxtf)maxtf=tf;
                 queryMap.put(token, (double)tf);
                 //queryMapIDF.put(token, vocab.get(token).idf);

              }
              else{
                  queryMap.put(token, 1.00);
                  if (1>maxtf)maxtf=1;
                  //queryMapIDF.put(token, vocab.get(token).idf);
              }   
          }

        }
        for (Map.Entry<String, Double> entry : queryMap.entrySet()){
            if(vocab.containsKey(entry.getKey())){
               double tfidf= (((double)entry.getValue())/maxtf)*vocab.get(entry.getKey()).idf;
               tfidf=log2(tfidf, 2);
               queryMap.put(entry.getKey(),Math.pow(tfidf, 2));
               System.out.println(entry.getKey()+" "+entry.getValue());
            }
            else{
              queryMap.put(entry.getKey(), 0.00);
              System.out.println(entry.getKey()+" "+entry.getValue());
            }
        }
    }
    
    private double log2(double a, double b ){
        return Math.log(a)/Math.log(b);
    }
    
    private void initStopWords(String fp) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fp), "UTF8"));
        String str;
        while ((str = in.readLine()) != null){
            this.stopwords.add(Stemmer.Stem(str));
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
