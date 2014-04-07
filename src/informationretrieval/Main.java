/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author smyrgeorge
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FlIndexer fi = new FlIndexer("files/stopwordsEn.txt","files/stopwordsGr.txt");
        FlWriter fw = new FlWriter(fi);
        
        QueryValuate qv = new QueryValuate();
        qv.query("aborigin");
    }
    
}
