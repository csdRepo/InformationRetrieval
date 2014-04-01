/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author smyrgeorge
 */
public class FileIndexer {
    
    int enWords;
    int grWords;
    private final String fpEN;
    private final String fpGR;
    private String[] stopwordsEN;
    private String[] stopwordsGR;
    

    public FileIndexer(String fp1, String fp2) throws FileNotFoundException{
        this.stopwordsEN = null;
        this.stopwordsGR = null;
        this.enWords=0;
        this.grWords=0;
        this.fpEN=fp1;
        this.fpGR=fp2;
        
        this.initStopWordsEN();
        this.initStopWordsGR();
    }
    
    private void initStopWordsEN() throws FileNotFoundException{
        Scanner sc = new Scanner(new File(this.fpEN));
        List<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        this.enWords=lines.size();
        this.stopwordsEN = new String[this.enWords];
        for(int i=0; i<this.enWords;i++)
            this.stopwordsEN[i]=lines.get(i);
    }
    
    
    private void initStopWordsGR() throws FileNotFoundException{
        Scanner sc = new Scanner(new File(this.fpGR));
        List<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        this.grWords=lines.size();
        this.stopwordsGR = new String[this.grWords];
        for(int i=0; i<this.grWords;i++)
            this.stopwordsGR[i]=lines.get(i);
    }
    
}
