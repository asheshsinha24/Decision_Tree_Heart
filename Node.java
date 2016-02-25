import java.io.*;
import java.util.*;

class Node 
{
    public double entropy;
    public Vector data;
    public int decompose_att;
    public int decomp_val;
    public Node []children;
    public Node parent;
    public String split_val;
    public int pos;
    public int neg;
    
    public Node()
    {
	data=new Vector();
    }
}
