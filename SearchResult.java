package mmst;

/**
 * Created by anna on 17/6/26.
 */
import java.util.ArrayList;

public class SearchResult {
    public int move,matchnumber;
    public StringBuffer pattern;
    public ArrayList<ArrayList<ArrayList<Integer>>> Position=new ArrayList<ArrayList<ArrayList<Integer>>>();//记录匹配字符是从根节点开始第几个字符
    SearchResult(int move,StringBuffer pattern,int matchnumber, ArrayList<ArrayList<ArrayList<Integer>>> Position2){
        this.move=move;
        this.pattern=pattern;
        this.matchnumber=matchnumber;
        this.Position=Position2;
    }
}
