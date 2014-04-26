/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.NavigableMap;

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
//        GUInterface.main(args);
        FlIndexer fi = new FlIndexer("files/stopwordsEn.txt","files/stopwordsGr.txt","files/documentCollection/all");
        FlWriter fw = new FlWriter(fi, "files/documentCollection/all");
        
       QueryValuate qv = new QueryValuate("files/stopwordsEn.txt","files/stopwordsGr.txt","CollectionIndex/");
       
       NavigableMap<Double,Integer> simOKAPI = (NavigableMap) qv.queryOKAPI("plays piano");
       System.out.println("OKAPI:");
       for(NavigableMap.Entry<Double,Integer> entry: simOKAPI.entrySet()){
           System.out.println(entry.getValue()+" "+entry.getKey());
           System.out.println(qv.getFilePath(entry.getValue()));
           ArrayList<String> snippet = qv.getSnippet(entry.getValue(), "plays piano", qv.getFilePath(entry.getValue()));
            for (String snpt : snippet) {
                System.out.print(snpt+"... ");
            }
       }
       
       
       NavigableMap<Double,Integer> simVS = (NavigableMap) qv.queryVS("plays piano");
       System.out.println("VS:");
       for(NavigableMap.Entry<Double,Integer> entry: simVS.entrySet()){
           System.out.println(entry.getValue()+" "+entry.getKey());
           System.out.println(qv.getFilePath(entry.getValue()));
       }
       
    }
}
