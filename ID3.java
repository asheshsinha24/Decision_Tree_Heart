import java.io.*;
import java.util.*;

public class ID3
{
    int att_num;
    String [] att_name;
    Vector [] domain;
    Vector [] domainReal;

    public ID3()
    {
	att_name=new String[20];
	domain = new Vector[20];
	domainReal = new Vector[20];
	for (int i=0; i<20;i++)
	    {
	     domain[i] = new Vector();
	     domainReal[i] = new Vector();
	     
	     
	    }
    }

    public void createID3(ID3 d_tree2, Node root, Node root1, int m, int []real_val)
    {
       
	root.decompose_att=-1;
	decomposeNode(root,m, real_val);
	
	customPrint(root, real_val, "");
	
	
	int count =0; 
	DataPoint point = new DataPoint (att_num);
	for (int i=0; i< root1.data.size(); i++)
	    {
		point = (DataPoint)root1.data.elementAt(i);
		int c_check = d_tree2.Learn(root,point,0, real_val);
	      
		
		if(c_check>0)
		    count++;
	    }
	System.out.println(count + " " + root1.data.size());
	
	
    }
    
    public void decomposeNode(Node node, int m, int []real_val)
    {
	double b_entropy=0;
	boolean select = false;
	int select_att = 0;

	int numdata = node.data.size();
	int numinput_att = att_num -1;
	
       
	node.entropy = calcEntropy(node.data);
	
	if(node.entropy ==0)
	    return;
	else
	    {
		for (int i = 0; i< numinput_att; i++)
		    {
			int real_or=0;

			for (int k=0; k<real_val.length; k++)
			    if(real_val[k]==i)
				real_or=1;

			
			if (att_num-1==i)
			    continue;
			
			int num_val = domain[i].size();
			
			
			double avg_entropy = 0;

			if(real_or==1)
			    {
				
				double b_real_entropy=0;
				Vector domain1 = getSubset1(node.data, i);

				
				int Flag1=0;
				int newj=1;
				
				for (int j=0; j<domain1.size(); j++)
				    { 
					String str11 = (String) domain1.elementAt(j);
					//System.out.println(att_name[i] + " " + str11);
					if(alreadyDecompose(node,i, str11))
						    {
							Flag1=j+1;
							continue;
						    }
					else 
					    {
						if(newj==1)
						    newj=0;
					    }
						
					
					avg_entropy = 0;
				     
					
					for (int k=0; k<2; k++)
					    {
						String str = (String) domain1.elementAt(j);
						Vector subset = getSubsetReal(node.data,i, str,k);
					    
						
						if(subset.size()==0) 
						    continue;
	 
						double subentropy = calcEntropy(subset);
						avg_entropy  = avg_entropy + subentropy*subset.size();
					       
					    }
					
					avg_entropy=avg_entropy/numdata;
					String str = (String) domain1.elementAt(j);

				 
					if(j==Flag1*newj)
					    {
						b_real_entropy=avg_entropy;
						Double val1 = Double.parseDouble(str)-1;
				      		Double val2 = Double.parseDouble(str)+1;
					       	domain[i].set(0, Double.toString(val1));
				       		domain[i].set(1, Double.toString(val2));
					    }
					else
					    {
						if(b_real_entropy > avg_entropy)
						    {
							b_real_entropy=avg_entropy;
							Double val1 = Double.parseDouble(str)-1;
							Double val2 = Double.parseDouble(str)+1;
							domain[i].set(0, Double.toString(val1));
							domain[i].set(1, Double.toString(val2));
						    }
							
					    }
					
				    }
				if(domain1.size()==0)
				    avg_entropy = node.entropy*numdata;
				else
				    avg_entropy=b_real_entropy*numdata;
			    }
			
			if(real_or==0)
			    {
				if(alreadyDecompose(node,i, "none"))
				    continue;
			
				for (int j =0; j<num_val; j++)
				    {
					Vector subset = getSubset(node.data, i, j);
				
					if(subset.size()==0) 
					    continue;
					double subentropy = calcEntropy(subset);
					avg_entropy  = avg_entropy + subentropy*subset.size();
				    }
				
			    }

			avg_entropy = avg_entropy/numdata;
		       
			if(node.entropy-avg_entropy>0)
			if(select == false)
			    {
				select = true;
				b_entropy = avg_entropy;
				select_att = i;
			       
			    }
			else
			    {
				if(avg_entropy < b_entropy)
				    {
					select = true;
					b_entropy = avg_entropy;
					select_att=i;
				 
				    }
			    }
		    }
	    }
	
	if( select == false)
	    return;
       
	int num_val = domain[select_att].size();
	
	node.decompose_att = select_att;
	
	node.children = new Node[num_val];
	
	for (int j = 0; j< num_val; j++)
	    {
		node.children[j] = new Node();
		
		node.children[j].parent=node;
		
		
		int real_or=0;

		 for (int k=0; k<real_val.length; k++)
		     if(real_val[k]==select_att)
			 real_or=1;

		 if(real_or==1)
		     {
			
			 String str1 = (String) domain[select_att].elementAt(0);
			 Double val = Double.parseDouble(str1)+1;
			 String str = Double.toString(val);
			 String newstr = "aaaa";

			 
			 node.children[j].split_val=str;
			 //System.out.println(att_name[select_att] + "  " + node.children[j].split_val);
			 node.children[j].data=getSubsetReal(node.data, select_att, node.children[0].split_val, j);
			 node.children[j].decompose_att=select_att;
			 
			 
		     }
		 else
		     {
		     node.children[j].data=getSubset(node.data, select_att,j);
		     node.children[j].split_val=(String) domain[select_att].elementAt(j);
		     //System.out.println(att_name[select_att] + "  " + node.children[j].split_val);
		     node.children[j].decompose_att=-1;
		     }
		 
		 int []val1 = getVal(node.children[j].data, att_num-1 );
			int pos_count=0, neg_count=0;
			
			for (int kk=0; kk< val1.length; kk++)
			    {
				if(val1[kk]==0)
				    neg_count++;
				else
				    pos_count++;
			    }
			
		node.children[j].pos=pos_count;
		node.children[j].neg=neg_count;
	       
		node.children[j].decomp_val = j;
		
		if(node.children[j].data.size()>=m)
		    {
		     //System.out.println("enetered");
			decomposeNode(node.children[j],m, real_val);
		    }
	     
	    }
	node.data = null;
    }
  
    public Vector getSubset1(Vector data, int i)
    {
	
	Vector domain1 = new Vector();
	int class_index=0;
	int t_size=data.size();
	Double [][]sorted_arr = new Double[t_size][2];
	for (int j=0;j<t_size;j++)
	    {
		DataPoint point1 = (DataPoint)data.elementAt(j);
		String str1 = (String)domainReal[i].elementAt(point1.attributes[i]);
	        Double val = Double.parseDouble(str1);
		 
	        int temp = point1.attributes[att_num-1];
		Double val1 = (double) temp;
		sorted_arr[j][0]=val;
		sorted_arr[j][1]=val1;
	    }

	for (int k=1; k<t_size; k++)
	    {
		double temp = sorted_arr[k][0];
		double temp1 = sorted_arr[k][1];
		int j;
		for (j=k-1; j>=0 && temp < sorted_arr[j][0]; j--)
		    {
		    sorted_arr[j+1][0]=sorted_arr[j][0];
		    sorted_arr[j+1][1]=sorted_arr[j][1];
		    }
		    sorted_arr[j+1][0]=temp;
		    sorted_arr[j+1][1]=temp1;
	    }
	
	Double [][]unique_sort = new Double[t_size][2];
	int new_size=0;
	int check=0;
	unique_sort[new_size][0]=sorted_arr[0][0];
	unique_sort[new_size][1]=sorted_arr[0][1];
	for (int k=0; k<t_size-1; k++)
	    {		

		if(Math.abs(sorted_arr[k][0]-sorted_arr[k+1][0])<0.001)
		    {
		      
			unique_sort[new_size][0]=sorted_arr[k][0];
			if(Math.abs(sorted_arr[k][1]-sorted_arr[k+1][1])<0.001 && check==0)
			    {
			      
			     unique_sort[new_size][1]=sorted_arr[k][1];
			    }
			else
			    {
			      
			    unique_sort[new_size][1]=2.0;
			    check=1;
			    }
		    }
		else
		    {
			
			check=0;
			
			new_size++;
			unique_sort[new_size][0]=sorted_arr[k+1][0];
			unique_sort[new_size][1]=sorted_arr[k+1][1];
		    }
	       
		  
		
	    }
		    
		for (int k=0; k<new_size; k++)
		    {
			
			
			
			if(Math.abs(unique_sort[k][1]-unique_sort[k+1][1])>0.001)
			    {
				 String str = Double.toString(0.5*(unique_sort[k][0]+unique_sort[k+1][0]));
					int index = domain1.indexOf(str);
					if(index<0)
					    {
					       
						domain1.addElement(str);
					    }
			    }
			else
			    {
				if(Math.abs(unique_sort[k][1]-2.0)<0.001)
				    {
					String str = Double.toString(0.5*(unique_sort[k][0]+unique_sort[k+1][0]));
					int index = domain1.indexOf(str);
					if(index<0)
					    {
					      
						domain1.addElement(str);
					    }
				       
					
				    }
			    }
				
		    }
				    		   
	       
        return domain1;
	    
    }
    
    public Vector getSubsetReal(Vector data, int i, String str, int k)
    {
     
	Vector subset = new Vector();
	for (int j=0;j<data.size();j++)
	    {
		DataPoint point1 = (DataPoint)data.elementAt(j);
		String str1 = (String)domainReal[i].elementAt(point1.attributes[i]);
		Double val = Double.parseDouble(str1);
		
		if(k<1)
		    {
			if(val <=Double.parseDouble(str))
			    subset.addElement(point1);
		    }

		if(k>=1)
		    {
			if(val >Double.parseDouble(str))
			    subset.addElement(point1);
		    }
	    }
	return subset;
		
    }

    public double calcEntropy(Vector data)
    {
	int s_data = data.size();
	double prob;
	double sum=0.0;
	
	if(s_data == 0)
	    return 0;
	else
	    {
		int att = att_num -1;
		int n_val = domain[att].size();
		sum=0.0;
		
		for(int i=0; i< n_val; i++)
		    {
			int count=0;
			
			for (int j=0; j< s_data; j++)
			    {
				DataPoint point = (DataPoint) data.elementAt(j);
				
				if (point.attributes[att] == i)
				    count++;
			    }
			
			prob = 1.0*count/s_data;
			
			if (count>0)
			    sum = sum -prob*Math.log(prob);
			
		       
		    }
		return sum;
	    }
    }

     public Vector getSubset(Vector data, int attribute, int val)
     {
	 Vector subset = new Vector();
	 int n = data.size();
	 
	 for (int i = 0; i< n; i++)
	     {
		 DataPoint point = (DataPoint) data.elementAt(i);
					 
		 if(point.attributes[attribute] == val)
		     subset.addElement(point);
	     }
	 return subset;
     }

    public boolean alreadyDecompose ( Node node, int attr, String str)
    {
	if(node.children != null)
	    {
		if(!str.equals("none"))
		   {
		       
		     
		       if(node.decompose_att==attr)
		       {
		    	   //System.out.println(node.decomp_val);
		       if(node.children[0].split_val.equals(str))
			   {
			   
			     return true;
			   }
		       }
		  
		   }
		    else
		    {
		if(node.decompose_att == attr)
		    return true;
		     }
	    }
	if(node.parent == null) 
	    return false;
	
	return alreadyDecompose(node.parent,attr,str);
    }

    public int []getVal(Vector data, int attribute) 
    {

	Vector val = new Vector();
	int n_size = data.size();
      
	for (int i=0; i< n_size; i++) 
	    {

		DataPoint point = (DataPoint)data.elementAt(i);
		String str = (String) domain[attribute].elementAt(point.attributes[attribute] );
		int index = val.indexOf(str);

			val.addElement(str);

  

	    }

	int []arr = new int[val.size()];
	for (int i=0; i< arr.length; i++) 
	    {

		String str = (String)val.elementAt(i);
		arr[i] = domain[attribute].indexOf(str);

	    }

	val = null;
	return arr;

    }


    public void customPrint(Node node, int []real_val, String str) 
    {
	int o_att = att_num-1;
       
	if (node.children == null) 
	    {
	       
		
		int []val = getVal(node.data, o_att );
		int pos_count=0, neg_count=0;
		
		for (int i=0; i< val.length; i++)
		    {
			if(val[i]==0)
			    neg_count++;
			else
			    pos_count++;
		    }
			    

		if (pos_count>0) 
		    {
			if(neg_count>=pos_count)
			System.out.println( ": " +  domain[o_att].elementAt(0));
			else
			 System.out.println( ": " +  domain[o_att].elementAt(1));
			return;

		     }
		else
		    {
			if(neg_count>=pos_count)
			System.out.println( ": " +  domain[o_att].elementAt(0));
			return;

		    }
		
		
		       

	    }

       

	int n_val = node.children.length;
	int real_index=-1;
	

	int pos_count=0, neg_count=0;
	
	
	
	for (int j=0; j<real_val.length;j++)
		if(real_val[j]==node.decompose_att)
		    real_index=node.decompose_att;

	if(real_index>-1)
	    {
		if(node.children[0].children==null)
		    System.out.print(str + att_name[node.decompose_att] + " <= " + Double.parseDouble(node.children[0].split_val) + " ["+ node.children[0].neg + "  " + node.children[0].pos + "]" );
		 else
		     System.out.println(str  + att_name[node.decompose_att] + " <= " + Double.parseDouble(node.children[0].split_val) + " ["+ node.children[0].neg + "  " + node.children[0].pos + "]");
			
		 customPrint(node.children[0], real_val, str + "|       ");

		 if(node.children[1].children==null)
		     System.out.print(str + att_name[node.decompose_att] + " > " + Double.parseDouble(node.children[0].split_val) + " ["+ node.children[1].neg + "  " + node.children[1].pos + "]");
		 else
		     System.out.println(str  + att_name[node.decompose_att] + " > " + Double.parseDouble(node.children[0].split_val) + " ["+ node.children[1].neg + "  " + node.children[1].pos + "]" );
			
		 customPrint(node.children[1], real_val, str + "|       ");
		
	    }
	else
	    {

		for (int i=0; i < n_val; i++) 
		    {
				
				
			
			if(node.children[i].children==null)
			    System.out.print(str + att_name[node.decompose_att] + " = " + domain[node.decompose_att].elementAt(i) + " ["+ node.children[i].neg + "  " + node.children[i].pos + "]" );
			else
			    System.out.println(str  + att_name[node.decompose_att] + " = " + domain[node.decompose_att].elementAt(i) + " ["+ node.children[i].neg + "  " + node.children[i].pos + "]");
			
			customPrint(node.children[i], real_val, str + "|       ");

		    }
	    }

    }

    
    public int Learn(Node root1, DataPoint point, int count, int []real_val)
    {
	int o_att = att_num-1;

	
	if(root1.children ==null)
	    {

		int []val = getVal(root1.data, o_att );
		int pos_count=0, neg_count=0;
		
		for (int i=0; i< val.length; i++)
		    {
			if(val[i]==0)
			    neg_count++;
			else
			    pos_count++;
		    }
			 
		for (int i=0; i<o_att;i++)
		{ int Flag=0;
			for (int j=0; j<real_val.length;j++)
				if(real_val[j]==i)
				   Flag=1;
			
			if(Flag==1)
			System.out.print(domainReal[i].elementAt(point.attributes[i]) + " ");
			else
			System.out.print(domain[i].elementAt(point.attributes[i]) + " ");	
		}

		if (pos_count>0) 
		    {
			if(neg_count>=pos_count)
			    {
				System.out.println(domain[o_att].elementAt(0) + " " + domain[o_att].elementAt(point.attributes[o_att]));
				if (domain[o_att].elementAt(0).equals(domain[o_att].elementAt(point.attributes[o_att])))
				     count++;
			    }
				
			else
			   {
			       System.out.println(domain[o_att].elementAt(1) + " " + domain[o_att].elementAt(point.attributes[o_att]));
			       if (domain[o_att].elementAt(1).equals(domain[o_att].elementAt(point.attributes[o_att])))
				     count++;
			   }
			

		     }
		else
		    {
			if(neg_count>=pos_count)
			    {
				System.out.println(domain[o_att].elementAt(0) + " " + domain[o_att].elementAt(point.attributes[o_att]) );
				if (domain[o_att].elementAt(0).equals(domain[o_att].elementAt(point.attributes[o_att])))
				     count++;
			    }
			

		    }
		
		
		return count;
	    }
	
	int n_val = root1.children.length;
	int real_index=-1;
	
	for (int j=0; j<real_val.length;j++)
		if(real_val[j]==root1.decompose_att)
		    real_index=root1.decompose_att;

	if(real_index>-1)
	    {
		String str1 = root1.children[0].split_val;
		Double val1 = Double.parseDouble(str1);

		String str2 = (String) domainReal[real_index].elementAt(point.attributes[root1.decompose_att]);
		Double val2 = Double.parseDouble(str2);
		
		if((val2-val1)<0.0001)
		    count = Learn(root1.children[0],point, count, real_val);
		else
		    count = Learn(root1.children[1],point, count, real_val);
		     		
	    }
	else
	    {

		for (int i=0; i < n_val; i++) 
		    {
			
		      if(point.attributes[root1.decompose_att]==i)
			  {
			
			      count = Learn(root1.children[i],point, count, real_val);
			
			  }

		    }
	    }




	return count;
	    
    }
    
		
    
}
