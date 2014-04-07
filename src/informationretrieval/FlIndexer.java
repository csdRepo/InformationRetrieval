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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import mitos.stemmer.Stemmer;

/**
 *
 * @author smyrgeorge
 */
public class FlIndexer {
    
    private final String fpEN;
    private final String fpGR;
    private String[] stopwordsEN;
    private String[] stopwordsGR;
    public Map<String, TermNode> mapTerms;
    private final File folder = new File("files/documentCollection/all");
    private final File[] listOfFiles = folder.listFiles();
    
    

    
    public FlIndexer(String fp1, String fp2) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        this.stopwordsEN = null;
        this.stopwordsGR = null;
        this.mapTerms = new TreeMap<>();
        this.fpEN=fp1;
        this.fpGR=fp2;
        Stemmer.Initialize();
        this.initStopWordsEN();
        this.initStopWordsGR();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                System.out.println(file.getCanonicalPath());
                this.initIndex(file.getPath());
            } 
        }
        //this.printTerms();
        this.printLength();
    }
    
    private void initStopWordsEN() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.fpEN), "UTF8"));
        List<String> lines = new ArrayList<>();
        String str;
        while ((str = in.readLine()) != null){
            lines.add(str);
        }
        int enWords=lines.size();
        this.stopwordsEN = new String[enWords];
        for(int i=0; i<enWords;i++)
            this.stopwordsEN[i]=lines.get(i);
    }
    
    private void initStopWordsGR() throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(this.fpGR), "UTF8"));
        List<String> lines = new ArrayList<>();
        String str;
        while ((str = in.readLine()) != null){
            lines.add(str);
        }
        int grWords=lines.size();
        this.stopwordsGR = new String[grWords];
        for(int i=0; i<grWords;i++)
            this.stopwordsGR[i]=lines.get(i);
    }
    
    private void initIndex(String file) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String str;
        int linepos=0;
        while ((str = in.readLine()) != null){
            StringTokenizer tok = new StringTokenizer(str, " ,.?-_;()![]\":'&*~`@#$%^�=+", true);
            int posInseek = linepos;
            while (tok.hasMoreTokens()){
                String token = tok.nextToken();
                posInseek=posInseek+token.length();
                if(!" ".equals(token))
                    insertTerm(token.toLowerCase(), file,posInseek-token.length());
            }
            linepos=linepos+str.length()+1+1;
        }
    }
    
    private void insertTerm(String term, String file, int pos){
        try{  
            int test = Integer.parseInt(term);
            return;
        }
        catch(NumberFormatException nfe){
            //Do nothing
        }
        if(Character.isDigit(term.charAt(0))){
            return;
        }
        if(" ,.?-_;()![]\":'&*~`@#$%^�=+".contains(term)){
            return;
        }
        for (String stpwrdEN : this.stopwordsEN) {
            if(term.equals(stpwrdEN)){
                return;
            }
        }
        for (String stpwrdGR : this.stopwordsGR) {
            if(term.equals(stpwrdGR)){
                return;
            }
        }
        term=Stemmer.Stem(term);
        if(this.mapTerms.containsKey(term)){
            TermNode tm = this.mapTerms.get(term);
            if(tm.getTerm().equals(term)){
                if(!file.equals(tm.getLastfile())){
                    tm.setDf();
                    tm.setLastfile(file);
                    tm.addPos(file, pos);
                    return;
                }
                tm.addPos(file, pos);
                tm.setSize();
                return;
            }
        }
        TermNode trm = new TermNode(term, file, pos);
        this.mapTerms.put(term, trm);
    }
        
    private void printTerms(){
        for (Map.Entry<String, TermNode> entry : this.mapTerms.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue().getSize()+" "+entry.getValue().getDf());
        }
    }
    
    private void printLength(){
        System.out.println("\nTotal Words: "+this.mapTerms.size());
    }

    
    private class TermNodeComparator implements Comparator<TermNode> {
        @Override
        public int compare(TermNode o1, TermNode o2) {
            return o1.getTerm().compareTo(o2.getTerm());
        }
    }
    
}
