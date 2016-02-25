import java.io.*;
import java.util.*;

class dtlearn
{   public static ID3 d_tree1;
    public static ID3 d_tree2;
    public static Node root1;
    public static Node root2;
    
    public static void main (String []args)
    {
	
	d_tree1 = new ID3();
	d_tree2 = new ID3();
	root1 =new Node();
	root2 = new Node();
	String filename1 = args[0];
	String filename2 = args[1];
	int m= Integer.parseInt(args[2]);
	int real_val[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	real_val = arffRead(1, d_tree1, root1, filename1, real_val);
	real_val = arffRead(2, d_tree2, root2, filename2, real_val);
	   
		
	d_tree1.createID3(d_tree2, root1,root2,m, real_val);
	
	
    }

    
    public static int[] arffRead ( int id_reading, ID3 d_tree, Node root, String filename, int real_val[])
    {
	int count =0, count1=0;
	
	int Flag=0, Flag1=0;
	double min[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	double max[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	int ind=0;
	
	String inputLine=null;
	File inputFile = new File (filename);

	
	
	try {
	    
	    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	    int data_count=0;
	    while(true)
		{
		    
		    
		    inputLine = reader.readLine();
		    
		   
		    if(inputLine==null)
		       break;
		   
		    
		    if (inputLine.startsWith("//"))
		       continue;
		         
		    if (inputLine.startsWith("@attribute"))
			    {
				
				StringTokenizer token = new StringTokenizer(inputLine);
				token.nextToken();
			       	String attribute_name = token.nextToken();
				
				attribute_name = attribute_name.substring(1, attribute_name.length()-1);
				

				d_tree.att_name[count]=attribute_name;
				
				int format=0;

				while(token.hasMoreTokens())
				    {
					String element_val = token.nextToken();
					
					if(element_val.equals("real"))
					   {
					       real_val[count1]=count;
					       d_tree.domain[count].addElement("0");
					       d_tree.domain[count].addElement("1");
					       
					       count1++;
					       
					   }
					else
					    {
						if(format==0)
						    format++;
						else
						    {
						d_tree.domain[count].addElement(element_val.substring(0, element_val.length()-1));
					     
						    }
					    }
				     }
				
			       
				count++;
			      }
					
		  
			     
			   if(Flag ==1)
			      {

				  data_count++;
 
				  List<String> str_val = Arrays.asList(inputLine.split(","));
				  
				  DataPoint point = new DataPoint(d_tree.att_num);

				  int ind1=1;
				  if(ind==0)
				      {
					  ind1=0;
					  ind++;
				      }
				  
				  for (int i=0; i<str_val.size(); i++)
				      {
					  int real_or=0;
					 
					  for (int j=0; j<count1; j++)
					      {
						  if(i==real_val[j])
						      {
							  real_or=1;
							  int index = d_tree.domainReal[i].indexOf(str_val.get(i));
							  if(index<0)
							      {
								  
								  d_tree.domainReal[i].addElement(str_val.get(i));
								  point.attributes[i]=d_tree.domainReal[i].size()-1;
							       
							      }
							  else
							  point.attributes[i]=index;
						      }
					      }
					  
					  if(real_or==0)
					      point.attributes[i] = d_tree.domain[i].indexOf(str_val.get(i));
				      }
				  root.data.addElement(point);
			      }

			   if(inputLine.startsWith("@data"))
			      {
				  

				  d_tree.att_num = count;
				  Flag =1;
				  
			      }

		     }
	    reader.close();
		} catch (Exception e) {
		     System.err.println("Unable to open file");
		}

	
	return real_val;
		       
		       
	   }
				 
    
}
