package org.edg.data.replication.optorsim.optor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;




import org.edg.data.replication.optorsim.BatchComputingElement;
import org.edg.data.replication.optorsim.GridConfFileReader;
import org.edg.data.replication.optorsim.SimpleComputingElement;
import org.edg.data.replication.optorsim.auctions.AccessMediator;
import org.edg.data.replication.optorsim.infrastructure.GridContainer;
import org.edg.data.replication.optorsim.infrastructure.GridSite;
import org.edg.data.replication.optorsim.infrastructure.OptorSimParameters;
import org.edg.data.replication.optorsim.infrastructure.StorageElement;
import org.edg.data.replication.optorsim.optor.StorageElementFactory;
import org.edg.data.replication.optorsim.optor.SkelOptor;

public class HH extends SkelOptor{
	public static Vector<Vector<GridSite>> gridde =new Vector <Vector <GridSite> > ();
	public Vector<GridSite>centres =new Vector<GridSite>();
	public static Vector<GridSite> bloc1 =new Vector<GridSite>();
	public static Vector<GridSite> bloc2 =new Vector<GridSite>();
	public static Vector<GridSite> bloc3=new Vector<GridSite>();
	public static Vector<GridSite> bloc6 =new Vector<GridSite>();
	public static Vector<GridSite> bloc4 =new Vector<GridSite>();
	public static Vector<GridSite> bloc5 =new Vector<GridSite>();
	public static Vector<GridSite> bloc7 =new Vector<GridSite>();
	public Vector<GridSite> centrescluster = new Vector<GridSite>();
	public Vector<GridSite> _gridsites = new Vector<GridSite>();
	public Vector<Integer> _cofCEs = new Vector<Integer>();
	public Vector<Vector<Long>> _cofSEs = new Vector<Vector<Long>>();
	public Vector<Float> _nbofSE = new Vector<Float>(); // AV Computation capacity of WN
	public float[][] _Matricebandwith = new float[27][31];
	public GridSite[] tab = new GridSite[27];
	public  String _filename;
	public int _numSites;
	public  GridConfFileReader _gridConfFileReaderInstance = null;
	
	
	
	
	
	  
public Vector<GridSite>  createGridSites (){
	    	int i, j,  numSE, workerNodes;
	        float wnCapacity ;
	       
	        OptorSimParameters params = OptorSimParameters.getInstance();
	        GridContainer gc = GridContainer.getInstance();
	        StorageElementFactory seFactory = StorageElementFactory.getInstance();
	        for(i=0;i< _numSites; i++) {
	            GridSite gsite = new GridSite();
	            workerNodes = ((Integer)_cofCEs.get(i)).intValue();
	            wnCapacity = ((Float)_nbofSE.get(i)).floatValue();
	            numSE = ((Long)((Vector<?>)_cofSEs.get(i)).get(0)).intValue();
	            if(workerNodes > 0) {
	                if (params.getComputingElement()==1)
	                    new SimpleComputingElement( gsite, workerNodes, wnCapacity);
	                if (params.getComputingElement() == 2)
	                    new BatchComputingElement(gsite, workerNodes, wnCapacity);
	                if(params.auctionOn()) AccessMediator.addAM(gsite);
	            }
	            for (j=1;j<=numSE;j++) {
	                long size = ((Long)((Vector<?>)_cofSEs.get(i)).get(j)).longValue();
	                StorageElement se = seFactory.getStorageElement( gsite, size);
	            }
	            gc.addSite( gsite);
	            _gridsites.add( gsite);
	                 }		
	                 return( _gridsites);
	                 }
	   
	   
  void read() {
                String inputLine,tmp;
             int j=0, noSE=0;
           int i=0,offset,oldoffset,linelength;
              FileReader read=null;
 
             try{
               read = new FileReader( _filename);
              }
              catch(FileNotFoundException e) {
                 System.out.println("\n ERROR::GridConfFileReader> File "
             +_filename+" not found.\n");
                System.exit(1);
                       }
                 BufferedReader in = new BufferedReader( read);		
                    try{
                 while( (inputLine = in.readLine()) != null) {
                  inputLine=inputLine.trim();
                     if( inputLine.startsWith("#"))  // skip lines starting with a #
                      continue;
                        Vector<Long> seCapacity = new Vector<Long>();
                        i=0;
                        oldoffset=0;
                        linelength = inputLine.length();
                          offset=inputLine.indexOf(" ");
         
                       while(offset<linelength) {
                        if(offset==(linelength-1)) {
                            tmp=inputLine.substring(oldoffset);
                        } else {
                 tmp=inputLine.substring(oldoffset,offset);
             }
             if(i==0) {
                 _cofCEs.add(new Integer(Integer.parseInt(tmp)));
             } else if(i==1){ // capacity of CEs
                 _nbofSE.add(new Float(Float.parseFloat(tmp))) ;
             } else if(i==2) { // number of SE
                 noSE = Integer.parseInt(tmp);
                 seCapacity.add(new Long( noSE));
                 if(noSE==0)
                     _cofSEs.add(seCapacity);
             } else if(i<noSE+3) { // capacity of SEs
                 Double tmpdouble = new Double((Long.parseLong(tmp)));
                 seCapacity.add(new Long(tmpdouble.longValue()));
                 if(i==noSE+2) // the capacity of the last SE
                     _cofSEs.add(seCapacity);
             } else { // network bandwith matrix
             	_Matricebandwith[j][i]= new Float(Float.parseFloat(tmp));
             }
             oldoffset=offset;
             oldoffset++;
             if(oldoffset<linelength) {
                 offset=inputLine.indexOf(" ",oldoffset);
                 if(offset==-1)
                     offset=linelength-1;
             } else {
                 offset=linelength;
             }
             i++;
         }
         j++;
     }
     in.close();
     read.close();
 }
 catch( Exception e) {
     System.out.println("Exception: "+e.getMessage()+ " whilst using file");
     System.exit(1);
 }
 i-=2; // Just matrix dimension
 _numSites=j;
}

	
	  
	  

public Vector<GridSite> MAP (Vector<GridSite> g){
	Vector< GridSite> centres =new 	Vector<GridSite>();
 for  (GridSite gs : g){
int cles =  gs.exposeIndex() ;
centres.add(  calculIntermediaire(gs,cles));}
return centres;}


public  GridSite calculIntermediaire(GridSite g, int cl) {
	GridSite Ng=new GridSite();
while( cl ==1 || cl==5 || cl==9 || cl==13 || cl==17 || cl==21 || cl==25 )
{ Ng= g;}
	
return (Ng);}


public void repartir (){
	 OptorSimParameters params = OptorSimParameters.getInstance();
 GridContainer gc = GridContainer.getInstance();
 _filename = params.getGridConfigfiString();
 read();

 bloc1.addElement(_gridsites.elementAt(1)) ;
 bloc1.addElement(_gridsites.elementAt(2)) ; 
 bloc1.addElement(_gridsites.elementAt(3));
 bloc1.addElement(_gridsites.elementAt(4));
 
 bloc2.addElement(_gridsites.elementAt(5)) ;
 bloc2.addElement(_gridsites.elementAt(6)) ; 
 bloc2.addElement(_gridsites.elementAt(7));
 bloc2.addElement(_gridsites.elementAt(8));

 bloc3.addElement(_gridsites.elementAt(9)) ;
 bloc3.addElement(_gridsites.elementAt(10)) ; 
 bloc3.addElement(_gridsites.elementAt(11));
 bloc3.addElement(_gridsites.elementAt(12));

 bloc4.addElement(_gridsites.elementAt(13)) ;
 bloc4.addElement(_gridsites.elementAt(14)) ; 
 bloc4.addElement(_gridsites.elementAt(15));
 bloc4.addElement(_gridsites.elementAt(16)); 

 bloc5.addElement(_gridsites.elementAt(17)) ;
 bloc5.addElement(_gridsites.elementAt(18)) ; 
 bloc5.addElement(_gridsites.elementAt(19));
 bloc5.addElement(_gridsites.elementAt(20));
 gridde.add(bloc1); gridde.add(bloc2); gridde.add(bloc3); gridde.add(bloc4); gridde.add(bloc5); gridde.add(bloc6); gridde.add(bloc7);
}

public static Vector<GridSite> getBloc(int i){
	if (i == 1){ return bloc1 ;}
	else if  (i==2){return bloc2;}
	else if  (i==3){return bloc3;}
	else if  (i==4){return bloc4;}
	else if  (i==5){return bloc5;}
	else if  (i==6){return bloc6;}
	else  return bloc7;
}

public void assigner (GridSite center, Vector<GridSite> bloc)	{
	

for (  GridSite site : bloc ) {
 
	double minDistance = distance(site,bloc.get(1) );
        int closestCluster = 0;
           for ( int i = 1; i < 7; i++ ) {
             double distance = distance( site,center);
           if ( distance < minDistance ) {
               closestCluster = i;
               minDistance = distance;
           }
       }
        getBloc( closestCluster ).add(site);
      
      }  
}
public GridSite recalculecentre(Vector<GridSite> bloc){
	double sum=0;
for(int i =0;i<4;i++){
 sum=sum+ distance(bloc.get(i),bloc.get(i+1));
	double v =sum /bloc.size();
	
	double bd=1/(  v-1);
for(int j=0;j<27; j++){
   for( int c=0; c<31;c++){
	   if (_Matricebandwith[c][j] == bd) 
	   {
	GridSite s1 = bloc.get(c);
	
//		/* if*s1 appartient bloc" ){ */
	float   min = distance(s1,bloc.get(1));
	   for(GridSite  s :bloc ){
	 if( min > distance(s1,bloc.get(c))){
		 min=distance(s1,bloc.get(c));}
		 
	 } return 
			 bloc.get(c);
			  }
  
/*else if s2 app bloc */ 
	   GridSite s2=bloc.get(j);
	   float   min = distance(s2,bloc.get(1));
	   for(GridSite  s :bloc ){
	 if( min > distance(s2,bloc.get(j))){
		 min=distance(s2,bloc.get(j));}
		 
	 } return 
			 bloc.get(j);
			  }}}
   /* else if  ni s1 ni s2 appartient au bloc*/
   /*else  aucun site  correspand*/
return null;
}
	
public float distance( GridSite g1,GridSite g2)	{
	
	float dist =0 ;
int 	index1= g1.exposeIndex();
int 	index2= g2.exposeIndex();
	float bd =_Matricebandwith[index1][index2];
	return dist =(1/bd )+1;
}


public void MAPclust (Vector <Vector<GridSite>> g){

for  (Vector<GridSite> gs : g){

assigner (gs.get(1),gs);}
}

public void Reduce  (Vector <Vector<GridSite>> g){

for  (Vector<GridSite> gs : g){

centres.add(recalculecentre(gs));
     }}
public Vector<GridSite> classification (){
int j=0;
while((!(centres.equals(centrescluster) ))&& j<10){
   centrescluster=centres;
    MAPclust(gridde);
  
  Reduce(gridde);
  j++;
}return centrescluster;
}}