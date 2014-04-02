/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package informationretrieval;

import java.util.LinkedList;

/**
 *
 * @author smyrgeorge
 */
public class TermNode {
    
    private final String term;
    private int size;
    private int df;
    private final LinkedList<Integer> tf;
    private final LinkedList<String> file;
    
    
    public TermNode(String term, String lf){
        this.term=term;
        this.size=1;
        this.df=1;
        this.tf=new LinkedList<>();
        this.file=new LinkedList<>();
        this.file.addFirst(lf);
        this.tf.addFirst(1);
    }
    
    public void setSize(){
        this.size++;
        this.tf.set(0,this.tf.getFirst()+1);
    }
    
    public void setDf(){
        this.df++;
    }
    
    public void setLastfile(String file){
        this.file.addFirst(file);
        this.tf.addFirst(1);
    }
    
    public String getTerm(){
        return this.term;
    }
    
    public int getSize(){
        return this.size;
    }
    
    public int getDf(){
        return this.df;
    }
    
    public String getLastfile(){
        return this.file.getFirst();
    }
    
}
