package mmst_s;

/**
 * Created by anna on 17/6/26.
 */
public class SearchResult {
    public int move,matchnumber;
    public StringBuffer pattern;
    SearchResult(int move,StringBuffer pattern,int matchnumber){
        this.move=move;
        this.pattern=pattern;
        this.matchnumber=matchnumber;
    }
}
