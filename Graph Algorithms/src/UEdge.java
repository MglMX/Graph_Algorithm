/**
 * Created by coque on 31/01/2016.
 */
public class UEdge {
    private String n1;
    private String n2;
    private double weight;
    private String id;

    public UEdge(String u,String v){
        n1=u;
        n2=v;
    }

    public UEdge(String u,String v,double w, String my_id){
        n1=u;
        n2=v;
        weight=w;
        id=my_id;
    }

    public String  getFirstNode(){
        return n1;
    }

    public String getSecondNode(){
        return n2;
    }


    @Override
    public boolean equals(Object o){
        boolean equals = false;
        if(o instanceof UEdge){
            if((((UEdge)o).getFirstNode().equals(n1) && ((UEdge)o).getSecondNode().equals(n2)) || (((UEdge)o).getFirstNode().equals(n2) && ((UEdge)o).getSecondNode().equals(n1)))
                equals = true;
        }
        return equals;
    }

    @Override
    public String toString(){
        return "("+n1+","+n2+")";
    }

}
