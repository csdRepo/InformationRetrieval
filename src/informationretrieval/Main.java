/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.io.FileNotFoundException;

/**
 *
 * @author smyrgeorge
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        FileIndexer fi = new FileIndexer("files/stopwordsEn.txt","files/stopwordsGr.txt");
    }
    
}
