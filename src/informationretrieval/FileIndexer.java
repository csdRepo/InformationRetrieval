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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author smyrgeorge
 */
public class FileIndexer {
    
    private final String fpEN;
    private final String fpGR;
    private String[] stopwordsEN;
    private String[] stopwordsGR;
    private final ArrayList<TermNode> terms;
    

    public FileIndexer(String fp1, String fp2) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        this.stopwordsEN = null;
        this.stopwordsGR = null;
        this.terms = new ArrayList<>();
        this.fpEN=fp1;
        this.fpGR=fp2;
        
        this.initStopWordsEN();
        this.initStopWordsGR();
        this.initIndex("files/documentCollection/novels/BLUEBELL.txt");
        
        this.sortTerms();
        
        this.printTerms();
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
        while ((str = in.readLine()) != null){
            StringTokenizer tok = new StringTokenizer(str, " ,.?-_;()![]\":'", true);
            while (tok.hasMoreTokens()){
                String token = tok.nextToken();
                if(!" ".equals(token))
                    insertTerm(token.toLowerCase());
            }
        }
    }
    
    private void insertTerm(String term){
        if(" ,.?-_;()![]\":'".contains(term)){
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
        for (TermNode tm : this.terms) {
            if(tm.getTerm().equals(term)){
                tm.setSize();
                return;
            }
        }
        TermNode trm = new TermNode(term);
        this.terms.add(trm);
    }
    
    private void sortTerms(){
        Collections.sort(this.terms, new TermNodeComparator());
    }
    
    
    private void printTerms(){
        for (TermNode term : this.terms) {
            System.out.println(term.getTerm()+": "+term.getSize());
        }
    }
    
    
    private class TermNodeComparator implements Comparator<TermNode> {
        @Override
        public int compare(TermNode o1, TermNode o2) {
            return o1.getTerm().compareTo(o2.getTerm());
        }
    }
    
}
