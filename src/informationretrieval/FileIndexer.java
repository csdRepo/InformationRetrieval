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
import java.util.List;

/**
 *
 * @author smyrgeorge
 */
public class FileIndexer {
    
    private final String fpEN;
    private final String fpGR;
    private String[] stopwordsEN;
    private String[] stopwordsGR;
    

    public FileIndexer(String fp1, String fp2) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        this.stopwordsEN = null;
        this.stopwordsGR = null;
        this.fpEN=fp1;
        this.fpGR=fp2;
        
        this.initStopWordsEN();
        this.initStopWordsGR();
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
    
}
