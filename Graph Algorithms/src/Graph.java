import com.opencsv.CSVReader;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.opencsv.CSVWriter;
import java.io.FileWriter;

/**
 * Created by coque on 31/01/2016.
 */
public class Graph {
    private Map<String,ArrayList<String> > nodes;
    private Map<String,Integer> DFI;
    private Map<String,Integer> P;
    private Map<String,String> father;
    private Stack<UEdge> edgeStack;
    private int i;
    private ArrayList<DEdge> edges;
    private Map<Integer,String> nodeMap;
    private Map<String,Integer> numberMap;

    private double [][] w;


    public Graph(){
        nodes = new HashMap< >();
        DFI = new HashMap<>(this.nNodes());
        P = new HashMap<>(this.nNodes());
        edgeStack = new Stack<>();
        father = new HashMap<>();
    }

    public Graph(String file, boolean directed, boolean hasWeight){
        try{
            nodes = new HashMap< >();
            DFI = new HashMap<>(this.nNodes());
            P = new HashMap<>(this.nNodes());
            edgeStack = new Stack<>();
            father = new HashMap<>();
            edges = new ArrayList<>();



            CSVReader reader = new CSVReader(new FileReader(file),'\t');
            String [] nextLine;

            reader.readNext(); //We don't want the first line

            while ((nextLine = reader.readNext()) != null) {

                String v1 = nextLine[0];
                String v2 = nextLine[1];

                if(hasWeight) {
                    String weight = nextLine[2];
                    // System.out.println(v1+" "+v2+": "+weight);
                    String id = nextLine[3];
                    //Add the edge
                    edges.add(new DEdge(v1, v2,Double.parseDouble(weight),id));
                    if(!directed)
                        edges.add(new DEdge(v2, v1,Double.parseDouble(weight),id));

                }


                //Check if the first node is in the map
                if(nodes.get(v1)==null) {
                    nodes.put(v1, new ArrayList<>());
                }

                //Check if the second node is in the map
                if(nodes.get(v2)==null)
                    nodes.put(v2,new ArrayList<>());

                nodes.get(v1).add(v2); //Add v2 to the adjacency list

                if(!directed) {
                    nodes.get(v2).add(v1); //Add v1 to the adjacency list
                }

            }
        }catch (Exception e){
            System.out.println("Error:"+e.getMessage());
        }
    }

    public int nNodes(){
        return nodes.size();
    }

    public ArrayList<String> getAdjNodes(String v){
        return nodes.get(v);
    }
    
    public void printAdjNodes(String v){

        String vertexesList = new String();
        for (String vertex: nodes.get(v)) {
            vertexesList+=vertex+", ";
        }

        System.out.println("Adjacent nodes to "+ v+": "+vertexesList);
    }

    public Set<String> getNodesSet(){
        return nodes.keySet();
    }

    private void makeNodeMap(){
        nodeMap = new HashMap<>();
        numberMap = new HashMap<>();

        int i=0;
        for(String n : nodes.keySet()){
            nodeMap.put(i,n);
            numberMap.put(n,i);
            i++;
        }
    }

    public void printNodeMap(){
        if(nodeMap==null)
            this.makeNodeMap();
        for(int i=0;i<this.nNodes();i++){
            System.out.println("\""+i+"\": "+nodeMap.get(i));
        }
    }

    private double[][] getWeightMatrix(){

        this.makeNodeMap();

        if(w==null){
            w = new double [this.nNodes()][this.nNodes()];

            for(int i=0;i<this.nNodes();i++){
                for(int j=0; j<this.nNodes();j++){
                    if(i==j){
                        w[i][j]=0.0;
                    }else if(edges.indexOf(new DEdge(nodeMap.get(i),nodeMap.get(j),0))!=-1){
                        w[i][j]=edges.get(edges.indexOf(new DEdge(nodeMap.get(i),nodeMap.get(j),0))).getWeight();
                    }else{
                        w[i][j]=Double.POSITIVE_INFINITY;
                    }
                }
            }
        }

        return w;
    }

    public void printWMatrix(){
        double[][] m = getWeightMatrix();

        String line = "";

        for(int i=0;i<this.nNodes();i++){
            for(int j=0;j<this.nNodes();j++){
                if(m[i][j]==Double.POSITIVE_INFINITY){
                    line+= nodeMap.get(i)+"-"+nodeMap.get(j)+": "+"^.^\t";
                }else
                    line+=nodeMap.get(i)+"-"+nodeMap.get(j)+": "+m[i][j]+"\t";
            }

            System.out.println(line);
            line="";
        }
    }


    private double [][] calculatePathMatrix(){
        double [][] dist = this.getWeightMatrix();

        for(int k=0; k<this.nNodes();k++){
            for(int i=0;i<this.nNodes();i++){
                for(int j=0; j<this.nNodes();j++){
                    if(dist[i][k]+dist[k][j]<dist[i][j])
                        dist[i][j] = dist[i][k]+dist[k][j];
                }
            }
        }

        return dist;

    }

    public void makeCSVPathMatrix(){
        double [][] m= this.calculatePathMatrix();
        String [] stringM = new String[m[0].length+1];

        try {

            CSVWriter writer = new CSVWriter(new FileWriter("solution.csv"), '\t');
            // feed in your array (or convert your data to an array)


            for(int i=-1;i<m.length;i++){
                if(i==-1){
                    stringM[0]="";
                }else {
                    stringM[0] = nodeMap.get(i);
                }

                for(int j=0;j<this.nNodes();j++){
                    if(i==-1){
                        stringM[j+1]=nodeMap.get(j);
                    }else{
                        if(m[i][j]==Double.POSITIVE_INFINITY)
                            stringM[j+1]="--";
                        else
                            stringM[j+1]=String.valueOf(m[i][j]);
                    }

                }
                writer.writeNext(stringM);

            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> getMinimalSpanningTree(){

        Set <DEdge> tree = new HashSet<>();
        ArrayList<ArrayList<String>> treeId = new ArrayList<>();
        HashSet <String> nodesToInspect = new HashSet<>(this.getNodesSet());
        HashSet<String> inspectedNodes = new HashSet<>();

        int i=0; //Counter to now in which connected component we are
        treeId.add(new ArrayList<>());

        //Take one random element
        String u=nodesToInspect.iterator().next();

        nodesToInspect.remove(u);
        inspectedNodes.add(u);

        double [][] w = getWeightMatrix();

        Map <String,Double> L = new HashMap<>();
        Map <String,String> whoUpdated = new HashMap<>();

        for(String node : nodesToInspect){
            L.put(node,w[numberMap.get(u)][numberMap.get(node)]);
            //System.out.println("L("+node+")="+L.get(node));
            whoUpdated.put(node,u);
        }

        double min = Double.POSITIVE_INFINITY;
        String minNode = "";
        boolean updated = false;

        while(!nodesToInspect.isEmpty()){

            for(String n : nodesToInspect){

                if(L.get(n)<min){
                    min=L.get(n);
                    minNode=n;
                    updated=true;
                }
            }

            if(updated){
                DEdge newEdge = new DEdge(whoUpdated.get(minNode),minNode);

                //Search for that edge to get the id
                for(DEdge e : edges){
                    if(e.equals(newEdge)){
                        tree.add(e);
                        treeId.get(i).add(e.getId());
                    }
                }

                min = Double.POSITIVE_INFINITY;

                nodesToInspect.remove(minNode);
                inspectedNodes.add(minNode); //Add to V'

                for(String n : nodesToInspect){
                    if(w[numberMap.get(n)][numberMap.get(minNode)]<L.get(n)){
                        L.put(n,w[numberMap.get(n)][numberMap.get(minNode)]);
                        whoUpdated.put(n,minNode);
                    }
                }
                updated=false;
            }else if(!nodesToInspect.isEmpty()){
                //Take one new random element for another connected component
                String v=nodesToInspect.iterator().next();

                nodesToInspect.remove(v);
                inspectedNodes.add(v);

                //Updated L for the next conected component
                for(String node : nodesToInspect){
                    L.put(node,w[numberMap.get(v)][numberMap.get(node)]);
                    //System.out.println("L("+node+")="+L.get(node));
                    whoUpdated.put(node,v);
                }
                i++;//Indicate new connected component
                treeId.add(new ArrayList<>()); //Inizialization of the array
            }
        }
        return treeId;
    }

    public void printPathMatrix(){
        double [][] m = this.calculatePathMatrix();

        String line = "";

        System.out.println("nNodes: "+this.nNodes());

        for(int i=-1; i<this.nNodes();i++){
            if(i!=-1)
                line+=nodeMap.get(i)+"\t";
            else{
                line+=" \t";
            }
            for(int j=0; j<this.nNodes();j++){
                if(i==-1)
                    line+=nodeMap.get(j)+"\t";
                else if(m[i][j]!=Double.POSITIVE_INFINITY)
                    line+=m[i][j]+"\t";
                else
                    line+="--\t";
            }
            System.out.println(line);
            line="";
        }
    }






    public void DFSB(String node) {

        DFI.put(node, i);
        P.put(node, DFI.get(node));
        i++;

        for (String adjNode : this.getAdjNodes(node)) {

            UEdge edge = new UEdge(node, adjNode); //Create an edge object
            if (!edgeStack.empty()) {

                String sStack = new String();

                for (UEdge e : edgeStack) {
                    sStack += e.toString() + " ";
                }

                if (edgeStack.search(edge) == -1) {
                    edgeStack.push(edge);
                }

            } else {
                edgeStack.push(edge);
            }

            if (DFI.get(adjNode) == 0) {
                father.put(adjNode, node);
                DFSB(adjNode);

                if (P.get(adjNode) >= DFI.get(node)) {

                    String sBlock = new String();
                    Set<String> block = new HashSet<>();

                    //Pop
                    while (!edgeStack.isEmpty() && !edgeStack.peek().equals(edge)) {
                        UEdge poppedEdge = edgeStack.pop();
                        block.add(poppedEdge.getFirstNode());
                        block.add(poppedEdge.getSecondNode());
                    }
                    if(!edgeStack.isEmpty()){
                        UEdge poppedEdge = edgeStack.pop();
                        block.add(poppedEdge.getFirstNode());
                        block.add(poppedEdge.getSecondNode());
                    }

                    System.out.println("Block:"+block);
                }
                P.put(node, Math.min(P.get(node), P.get(adjNode)));
            } else {

                if (  !adjNode.equals(father.get(node))) {
                    P.put(node, Math.min(P.get(node), DFI.get(adjNode)));
                }
            }
        }
    }

    public void printDFI(){
        System.out.println("DFI");

        for(String node : DFI.keySet()){
            System.out.println(node+": "+DFI.get(node));
        }
    }
    public void printP(){
        System.out.println("P");

        for(String node : P.keySet()){
            System.out.println(node+": "+P.get(node));
        }
    }

    public void printBlocks(){
        i = 1;

        for(String node: this.getNodesSet()){
            DFI.put(node,0);
        }

        for(String node: this.getNodesSet()){
            if(DFI.get(node)==0)
                DFSB(node);
        }

        i=0;
        DFI.clear();
        P.clear();
        edgeStack.clear();
        father.clear();
    }
}
