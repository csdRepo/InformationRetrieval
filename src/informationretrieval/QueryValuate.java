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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author smyrgeorge
 */
public class QueryValuate {
    private final Map<String, VocNode> vocab;
    
    public QueryValuate() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        this.vocab = new HashMap<>();
        this.initVocab();
    }
    
    private void initVocab() throws UnsupportedEncodingException, FileNotFoundException, IOException{
        String str;
        String file = "CollectionIndex/VocabularyFile.txt";
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        
        while ((str = in.readLine()) != null){
            String[] temp = str.split(" ");
            this.vocab.put(temp[0], new VocNode(Integer.parseInt(temp[1]), Integer.parseInt(temp[2])));
        }
        
    }
    
    private class VocNode{
        private final int df;
        private final int pPost;
        public VocNode(int df, int pPost){
            this.df=df;
            this.pPost=pPost;
        }
        
        public int getDF(){
            return this.df;
        }
        
        public int getpPost(){
            return this.pPost;
        }
    }
}
