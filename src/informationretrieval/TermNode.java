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
public class TermNode {
    
    private final String term;
    private int size;
    
    
    public TermNode(String term){
        this.term=term;
        this.size=1;
    }
    
    public void setSize(){
        this.size++;
    }
    
    public String getTerm(){
        return this.term;
    }
    
    public int getSize(){
        return this.size;
    }
    
}
