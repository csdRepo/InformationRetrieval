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
import java.io.RandomAccessFile;
import java.util.StringTokenizer;

/**
 *
 * @author smyrgeorge
 */
public class RAFtest {
    public RAFtest() throws FileNotFoundException, IOException{
        int seek = 0;
        String file1 = "files/documentCollection/novels/test.txt";
        String str;
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "UTF8"));
        File file = new File("files/documentCollection/novels/test.txt");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        
        
        while ((str = in.readLine()) != null){
            StringTokenizer tok = new StringTokenizer(str, " ,.?-_;()![]\":'&*~`@#$%^�", true);
            int pos=seek;
            while (tok.hasMoreTokens()){
                raf.seek(pos);
                String str1=raf.readLine();
                System.out.println(str1);
                String token = tok.nextToken();
                pos=pos+token.length();
            }
            seek=seek+str.length()+1;
            //System.out.println(seek);
        }
        
        
    }
    
    
}
