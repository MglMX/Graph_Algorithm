/**
 * Created by coque on 06/02/2016.
 */
public class DEdge {
    private String n1;
    private String n2;
    private double weight;
    private String id;

    public DEdge(String u,String v){
        n1=u;
        n2=v;
    }

    public DEdge(String u,String v, double w){
        n1=u;
        n2=v;
        weight=w;
    }

    public DEdge(String u,String v, double w, String myId){
        n1=u;
        n2=v;
        weight=w;
        id=myId;
    }


    public String getId(){
        return id;
    }
    public double getWeight(){ return weight; }

    public String  getFirstNode(){
        return n1;
    }

    public String getSecondNode(){
        return n2;
    }


    @Override
    public boolean equals(Object o){
        boolean equals = false;
        if(o instanceof DEdge){
            if((((DEdge)o).getFirstNode().equals(n1) && ((DEdge)o).getSecondNode().equals(n2)))
                equals = true;
        }
        return equals;
    }

    @Override
    public String toString(){
        return "("+n1+","+n2+")";
    }
}
