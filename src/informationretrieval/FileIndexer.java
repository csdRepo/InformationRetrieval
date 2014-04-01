/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

/**
 *
 * @author smyrgeorge
 */
public class FileIndexer {
    
    private final String fp1;
    private final String fp2;
    private final String[] stopwordsEN;
    private final String[] stopwordsGR;
    

    public FileIndexer(String fp1, String fp2){
        this.stopwordsEN = new String[256];
        this.stopwordsGR = new String[256];
        this.fp1=fp1;
        this.fp2=fp2;
    }
    
    private void initStopWordsEN(){
        
    }
    
    
    private void initStopWordsGR(){
        
    }
    
}
