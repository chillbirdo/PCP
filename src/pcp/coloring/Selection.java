package pcp.coloring;

public class Selection {

    int[] selectedIds;
    int idx;
    
    public Selection( int size){
        selectedIds = new int[size];
        idx = 0;
    }
    
    public void add( int id){
        if( idx >= selectedIds.length){
            return;
        }
        selectedIds[idx] = id;
        idx++;
    }
    
    public void applySelectionToColoring( Coloring c){
        for( int i = 0; i < selectedIds.length; i++){
            c.getNciById( selectedIds[i]).setColorUncolored();
        }
    }
}
