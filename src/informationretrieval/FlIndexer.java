/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import mitos.stemmer.Stemmer;

/**
 *
 * @author smyrgeorge
 */
public class FlIndexer {
    private int maxintTF=0;
    private final Map<String, Integer> maxTF;
    private final HashSet<String> stopwords;
    public Map<String, TermNode> mapTerms;
    private final File folder;
    private final File[] listOfFiles;
    
    

    
    public FlIndexer(String fpEN, String fpGR, String path) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        this.folder= new File(path);
        this.listOfFiles =folder.listFiles();
        this.stopwords=new HashSet<>();
        this.maxTF = new TreeMap<>();
        this.mapTerms = new TreeMap<>();
        
        Stemmer.Initialize();
        this.initStopWordsEN(fpEN);
        this.initStopWordsGR(fpGR);
        
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getCanonicalPath());
                this.initIndex(file.getPath());
                this.maxTF.put(file.getPath(), this.maxintTF);
                this.maxintTF=0;
            } 
        }
        this.printLength();
    }
        
    private void initIndex(String file) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String delimiter = "\t\n\r\f!@#$%^&*;:'\".,0123456789()_-[]{}<>?|~`+-=/ \'\b«»§΄―—’‘–°· \\� ";
        String str;
        int linepos=0;
        while ((str = in.readLine()) != null){
            StringTokenizer tok = new StringTokenizer(str, delimiter, true);
            int posInseek = linepos;
            while (tok.hasMoreTokens()){
                String token = tok.nextToken();
                posInseek=posInseek+token.length();
                if(!delimiter.contains(token))
                    insertTerm(token.toLowerCase(), file, posInseek-token.length());
            }
            linepos=linepos+str.length()+2;
        }
    }
    
    private void insertTerm(String term, String file, int pos){
        term=Stemmer.Stem(term);
        if(this.stopwords.contains(term)) return;

        if(this.mapTerms.containsKey(term)){
            TermNode tm = this.mapTerms.get(term);
            if(!file.equals(tm.getLastfile())){
                tm.setDf();
                tm.setLastfile(file);
                tm.addPos(file, pos);
                if(tm.getLasttf()>this.maxintTF) this.maxintTF=tm.getLasttf();
                return;
            }
            tm.addPos(file, pos);
            tm.setSize();
            if(tm.getLasttf()>this.maxintTF) this.maxintTF=tm.getLasttf();
            return;
        }
        TermNode trm = new TermNode(term, file, pos);
        if(trm.getLasttf()>this.maxintTF) this.maxintTF=trm.getLasttf();
        this.mapTerms.put(term, trm);
    }
    
    private void initStopWordsEN(String fpEN) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fpEN), "UTF8"));
        String str;
        while ((str = in.readLine()) != null){
            this.stopwords.add(Stemmer.Stem(str));
        }
    }
    
    private void initStopWordsGR(String fpGR) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fpGR), "UTF8"));
        String str;
        while ((str = in.readLine()) != null){
            this.stopwords.add(Stemmer.Stem(str));
        }
    }
    
    public int getMaxTF(String file){
        return this.maxTF.get(file);
    }
    
    private void printTerms(){
        for (Map.Entry<String, TermNode> entry : this.mapTerms.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue().getSize()+" "+entry.getValue().getDf());
        }
    }
    
    private void printLength(){
        System.out.println("\nTotal Words: "+this.mapTerms.size());
    }    
}
