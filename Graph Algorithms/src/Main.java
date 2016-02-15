/**
 * Created by coque on 30/01/2016.
 */

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class Main {
    public static void main(String []args){

        Graph graph = new Graph("src\\edges_3_72537.csv",false,true);

        ArrayList<ArrayList<String>> minSpanTree = graph.getMinimalSpanningTree();

        for(ArrayList<String> spanTree : minSpanTree){
            Collections.sort(spanTree);
            System.out.println(spanTree);
        }

    }
}
