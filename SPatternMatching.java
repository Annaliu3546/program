package mmst_s;

/**
 * Created by anna on 17/6/26.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
public class SPatternMatching {
    static StringBuffer text,midP, pattern;
    public static int numbers=0,count=0;
    public static ArrayList<ArrayList<SearchResult>> subproblemlist=new ArrayList<ArrayList<SearchResult>>();
    public static void main(String args[]) throws IOException{
        long begin = new Date().getTime();
        SPatternMatching PM=new SPatternMatching();
        SfxNode treeroot=new SfxNode();
        SuffixTree tree=new SuffixTree(text);
        treeroot=tree.addText();
        initialize();
        // long begin = new Date().getTime();
        //  System.out.println("测试： "+ subproblemlist.size());
        //  long end = new Date().getTime();
        SearchResult searchresult=SPatternMatching.search(treeroot,pattern,0);
        System.out.println("匹配次数： "+ searchresult.matchnumber);

        long end = new Date  ().getTime();
        //  System.out.println("search执行次数:" + numbers+"    compare执行次数：" + count);
        System.out.println("use of time:" + (end - begin)+"ms");
    }
    public SPatternMatching() throws IOException{
        //InputTP();
        ReadTP();
        pattern=dealP(midP);
        pattern.append('^');
    }
    public static void initialize(){
        int much=SuffixTree.g_sfxNodeCnt;
        subproblemlist.ensureCapacity(much);
        for(int m=0;m<much;m++){
            ArrayList<SearchResult> midresult = new ArrayList<SearchResult>();
            subproblemlist.add(midresult);
        }
    }
    //agcatgatagcatga是compare方法参数的传递出现了问题，已解决
    //a*[0,4]g*[0,1]t
    //aggggagtggaggat//少了返回的语句在普通字符的程序段里searchresult.matchnumber=succ;
    //a*[0,3]gg*[0,3]t
    //agcatgatagcatga
    //g*[2,9]g*[0,6]t*[0,3]t//测试search执行次数
    /**   public void InputTP(){
     text=new StringBuffer("agcatgatagcatga");
     midP=new StringBuffer("g*[2,9]g*[0,6]t*[0,3]t");
     }*/
    public void ReadTP() throws IOException{
        text=this.ReadFromText("src/test/dna.txt");
        midP=this.ReadFromText("src/test/pattern.txt");
    }
    public StringBuffer ReadFromText(String str) throws IOException{
        FileReader y=new FileReader(str);
        BufferedReader br=new BufferedReader(y);
        StringBuffer text=new StringBuffer(br.readLine());
        return  text;
    }
    public StringBuffer dealP(StringBuffer p){
        StringBuffer midP=new StringBuffer();
        int i=0;
        while(i<p.length()){
            if(p.charAt(i)!='*'){
                midP.append(p.charAt(i));
                i++;
                continue;
            }
            else{
                int n=1,j=1,m=0;
                char ch='!';
                midP.append(p.charAt(i));
                midP.append(p.charAt(++i));
                while(p.charAt(i+j)>=48 && p.charAt(i+j)<=57){
                    j++;
                }
                m=j-1;
                while(n<j){
                    ch=(char)(ch+(p.charAt(i+n)-48)* Math.pow(10,--m));
                    n++;
                }
                midP.append(ch);
                i=i+j;
                midP.append(p.charAt(i));
                n=1;j=1;ch='!';
                while(p.charAt(i+j)>=48 && p.charAt(i+j)<=57){
                    j++;
                }
                m=j-1;
                while(n<j){
                    ch=(char)(ch+(p.charAt(i+n)-48)* Math.pow(10,--m));
                    n++;
                }
                i=i+j;
                midP.append(ch);
                midP.append(p.charAt(i));
                i++;
            }
        }
        return midP;
    }
    public static int minlen(String p){
        int len=0,i=0;
        while(i<p.length()-1){
            if(p.charAt(i)!='*'){
                len++;
                i++;
            }
            else{
                len=len+(p.charAt(i+2)-33);
                i=i+6;
            }
        }
        return len;
    }
    public static int maxlen(String p){
        int len=0,i=0;
        while(i<p.length()-1){
            if(p.charAt(i)!='*'){
                len++;
                i++;
            }
            else{
                len=len+(p.charAt(i+4)-33);
                i=i+6;
            }
        }
        return len;
    }
    public static ArrayList<ArrayList<Integer>> CompareChildChar(SfxNode node,int l,int r,char p,int left,int right){
        count++;
        ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
        SfxNode chnode=node.ch;
        int gap=r-l,t=0;
        left=left-gap;
        right=right-gap;
        if(left>right || right<0){
            return result;
        }
        while(chnode!=null){
            if(SuffixTree.text.charAt(chnode.l)=='^'){
                chnode=chnode.csn;
                continue;
            }
            else if(chnode.ch==null && (chnode.l+left)>chnode.r){
                chnode=chnode.csn;
                continue;
            }
            else if(chnode.ch==null && (chnode.l+left)<=chnode.r){
                int right2 = 0;
                if(right>chnode.r-chnode.l-1){
                    right2=chnode.r-chnode.l-1;
                }
                else{
                    right2=right;
                }
                ArrayList<Integer> line=new ArrayList<Integer>();
                result.add(line);
                for(int m=0;m<right2-left+1;m++){
                    if(SuffixTree.text.charAt(chnode.l+left+m)==p){
                        result.get(t).add(left+m);
                    }
                }
                if(result.get(t).size()!=0){
                    result.get(t).add(chnode.num);
                    result.get(t).add(-1);
                    t++;
                    chnode=chnode.csn;
                    continue;
                }
                else{
                    result.remove(result.size()-1);
                    chnode=chnode.csn;
                    continue;
                }
            }
            else if(chnode.ch!=null && right<=chnode.r-chnode.l){
                ArrayList<Integer> line=new ArrayList<Integer>();
                result.add(line);
                for(int m=0;m<right-left+1;m++){
                    if(SuffixTree.text.charAt(chnode.l+left+m)==p){
                        result.get(t).add(left+m);
                    }
                }
                if(result.get(t).size()!=0){
                    result.get(t).add(chnode.num);
                    result.get(t).add(-1);
                    t++;
                    chnode=chnode.csn;
                    continue;
                }
                else{
                    result.remove(result.size()-1);
                    chnode=chnode.csn;
                    continue;
                }
            }
            else{
                if(left>(chnode.r-chnode.l)){
                    ArrayList<ArrayList<Integer>> midresult;
                    midresult=CompareChildChar(chnode,chnode.l,chnode.r,p,left-1,right-1);
                    for(int c=0;c<midresult.size();c++){
                        result.add(midresult.get(c));
                        t++;
                    }
                    //midresult.clear();
                    chnode=chnode.csn;
                    continue;
                }
                else{
                    ArrayList<Integer> line=new ArrayList<Integer>();
                    result.add(line);
                    for(int m=0;chnode.l+left+m<=chnode.r;m++){
                        if(SuffixTree.text.charAt(chnode.l+left+m)==p){
                            result.get(t).add(left+m);
                        }
                        if((chnode.l+left+m)==chnode.r && result.get(t).size()!=0){
                            result.get(t).add(chnode.num);
                            result.get(t).add(-1);
                            t++;
                            break;
                        }
                        if((chnode.l+left+m)==chnode.r && result.get(t).size()==0){
                            result.remove(result.size()-1);
                            break;
                        }
                    }
                    ArrayList<ArrayList<Integer>> midresult;
                    midresult=CompareChildChar(chnode,chnode.l,chnode.r,p,chnode.r-chnode.l,right-1);
                    for(int c=0;c<midresult.size();c++){
                        result.add(midresult.get(c));
                        t++;
                    }
                    //midresult.clear();
                    chnode=chnode.csn;
                    continue;
                }
            }
        }
        return result;
    }
    public static SearchResult search(SfxNode treeroot,StringBuffer p,int move){
        numbers++;
        //System.out.println(numbers);
        //System.out.println("subproblemlist.size()： "+ subproblemlist.size());
        int succ=0,l,r;
        SearchResult searchresult=new SearchResult(move,p,0);
        SfxNode varnode=treeroot;
        if(treeroot.num==0){
            varnode=SfxNode.findchild(varnode,p.charAt(0));
            if(varnode==treeroot){
                return searchresult;
            }
            else{
                if(p.length()-1==1){
                    if(varnode.ch!=null){
                        succ=succ+varnode.leafs;
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    else{
                        searchresult.matchnumber=++succ;
                        return searchresult;
                    }
                }
            }
            l=varnode.l+move;
            r=varnode.r;
        }
        else{
            l=varnode.l+move;
            r=varnode.r;
        }
        int i=1;
        while(i<p.length()-1){
            if(p.charAt(i)!='*'){
                if(varnode.ch==null && minlen(p.substring(i))>(r-l-1)){
                    searchresult.matchnumber=succ;
                    return searchresult;
                }
                else if(varnode.ch==null && minlen(p.substring(i))<=(r-l-1)){
                    if(p.charAt(i)==SuffixTree.text.charAt(l+1)){
                        i++;
                        l++;
                        move++;
                    }
                    else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(l>=r && i<p.length()-1){
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(p.charAt(i)=='^' || i==p.length()-1){
                        searchresult.matchnumber=++succ;
                        return searchresult;
                    }
                }
                else if(varnode.ch!=null && maxlen(p.substring(i))<=(r-l)){
                    if(p.charAt(i)==SuffixTree.text.charAt(l+1)){
                        i++;
                        l++;
                        move++;
                    }
                    else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(p.charAt(i)=='^' || i==p.length()-1 ){
                        succ=succ+varnode.leafs;
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }
                else{
                    if(l+1<=r){
                        if(p.charAt(i)== SuffixTree.text.charAt(l+1)){
                            i++;
                            l++;
                            move++;
                        }
                        else{
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                    }
                    else{
                        SfxNode midnode=varnode;
                        varnode=SfxNode.findchild(varnode,p.charAt(i));
                        if(varnode==midnode){
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                        else{
                            l=varnode.l;
                            r=varnode.r;
                            i++;
                            move=0;
                        }
                    }
                    if(l>=r && i<p.length()-1){
                        if(p.charAt(i)!='*'){
                            SfxNode midlnode=varnode;
                            varnode=SfxNode.findchild(varnode,p.charAt(i));
                            if(varnode==midlnode){
                                searchresult.matchnumber=succ;
                                return searchresult;
                            }
                            else{
                                l=varnode.l;
                                r=varnode.r;
                                i++;
                                move=0;
                            }
                        }
                        else{
                            StringBuffer PartP=new StringBuffer(p.substring(i-1));
                            if(subproblemlist.get(varnode.num).size()==0){
                                SearchResult midsearchresult=search(varnode,PartP,varnode.r-varnode.l);
                                subproblemlist.get(varnode.num).add(midsearchresult);
                                succ=succ+midsearchresult.matchnumber;
                                searchresult.matchnumber=succ;
                                i=i+7;
                                return searchresult;
                            }
                            else{
                                int mark=0;
                                for(int len=0;len<subproblemlist.get(varnode.num).size();len++){
                                    if(subproblemlist.get(varnode.num).get(len).move==(varnode.r-varnode.l) &&
                                            subproblemlist.get(varnode.num).get(len).pattern.toString().equals(PartP.toString())){
                                        succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                        searchresult.matchnumber=succ;
                                        i=i+7;
                                        return searchresult;
                                    }
                                }
                                if(mark==0){
                                    SearchResult midsearchresult=search(varnode,PartP,varnode.r-varnode.l);
                                    subproblemlist.get(varnode.num).add(midsearchresult);
                                    succ=succ+midsearchresult.matchnumber;
                                    searchresult.matchnumber=succ;
                                    i=i+7;
                                    return searchresult;
                                }
                            }
                        }
                    }
                    if(i==p.length()-1 || p.charAt(i)=='^'){
                        succ=succ+varnode.leafs;
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }
            }
            else{
                int left,right,occur=0;
                left=p.charAt(i+2)-33;
                right=p.charAt(i+4)-33;
                if(varnode.ch==null && minlen(p.substring(i))>(r-l-1)){
                    searchresult.matchnumber=succ;
                    return searchresult;
                }
                else if(varnode.ch==null && minlen(p.substring(i))<=(r-l-1)){
                    int m;
                    if(right>=r-l-1){
                        right=r-l-2;
                    }
                    for(m=0;m<right-left+1;m++){
                        if(p.charAt(i+6)== SuffixTree.text.charAt(l+left+m+1)){
                            if(p.charAt(i+7)!='^'){
                                StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                if(subproblemlist.get(varnode.num).size()==0){
                                    SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                    subproblemlist.get(varnode.num).add(midsearchresult);
                                    succ=succ+midsearchresult.matchnumber;
                                }
                                else{
                                    int mark=0;
                                    for(int len=0;len<subproblemlist.get(varnode.num).size();len++){
                                        if(subproblemlist.get(varnode.num).get(len).move==(left+m+1+move) &&
                                                subproblemlist.get(varnode.num).get(len).pattern.toString().equals(PartP.toString())){
                                            succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                            mark=1;
                                            break;
                                        }
                                    }
                                    if(mark==0){
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                    }
                                }
                            }
                            else{
                                occur++;
                            }
                        }
                    }
                    if(p.charAt(i+6)=='^'){
                        System.out.println("1,模式的格式输入错误!!!");
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(occur!=0){
                        i=i+7;
                        succ=(succ+1)*occur;
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }
                else if(varnode.ch!=null && maxlen(p.substring(i))<=r-l){
                    int m;
                    for(m=0;m<right-left+1;m++){
                        if(p.charAt(i+6)== SuffixTree.text.charAt(l+left+m+1)){
                            if(p.charAt(i+7)!='^'){
                                StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                if(subproblemlist.get(varnode.num).size()==0){
                                    SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                    subproblemlist.get(varnode.num).add(midsearchresult);
                                    succ=succ+midsearchresult.matchnumber;
                                }
                                else{
                                    int mark=0;
                                    for(int len=0;len<subproblemlist.get(varnode.num).size();len++){
                                        if(subproblemlist.get(varnode.num).get(len).move==(left+m+1+move) &&
                                                subproblemlist.get(varnode.num).get(len).pattern.toString().equals(PartP.toString())){
                                            succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                            mark=1;
                                            break;
                                        }
                                    }
                                    if(mark==0){
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                    }
                                }
                            }
                            else{
                                occur++;
                            }
                        }
                    }
                    if(p.charAt(i+6)=='^'){
                        System.out.println("2,模式的格式输入错误!!!");
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(occur!=0){
                        i=i+7;
                        succ=(succ+varnode.leafs)*occur;
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }
                else{
                    if(right<r-l){
                        int m;
                        for(m=0;m<right-left+1;m++){
                            if(p.charAt(i+6)== SuffixTree.text.charAt(l+left+m+1)){
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(varnode.num).size()==0){
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                    }
                                    else{
                                        int mark=0;
                                        for(int len=0;len<subproblemlist.get(varnode.num).size();len++){
                                            if(subproblemlist.get(varnode.num).get(len).move==(left+m+1+move) &&
                                                    subproblemlist.get(varnode.num).get(len).pattern.toString().equals(PartP.toString())){
                                                succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                            subproblemlist.get(varnode.num).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                        }
                                    }
                                }
                                else{
                                    occur++;
                                }
                            }
                        }
                        if(p.charAt(i+6)=='^'){
                            System.out.println("3,模式的格式输入错误!!!");
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                        if(occur!=0){
                            i=i+7;
                            succ=(succ+varnode.leafs)*occur;
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                        else{
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                    }
                    else if(left>=r-l){
                        ArrayList< ArrayList<Integer> > midresult;
                        midresult=CompareChildChar(varnode,l,r,p.charAt(i+6),left,right);
                        for(int from=0;from<midresult.size();from++){
                            int e=midresult.get(from).size()-1,total=0;
                            for(int c=0;c<e-1;c++){
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(midresult.get(from).get(e-1)).size()==0){
                                        SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                PartP,midresult.get(from).get(c));
                                        subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                    }
                                    else{
                                        int mark=0;
                                        for(int len=0;len<subproblemlist.get(midresult.get(from).get(e-1)).size();len++){
                                            if(subproblemlist.get(midresult.get(from).get(e-1)).get(len).move==midresult.get(from).get(c) &&
                                                    subproblemlist.get(midresult.get(from).get(e-1)).get(len).pattern.toString().equals(PartP.toString())){
                                                succ=succ+subproblemlist.get(midresult.get(from).get(e-1)).get(len).matchnumber;
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                    PartP,midresult.get(from).get(c));
                                            subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                        }
                                    }
                                }
                                else{
                                    total++;
                                }
                            }
                            if(total!=0){
                                succ=succ+(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).leafs)*total;
                            }
                        }
                        if(p.charAt(i+6)=='^'){
                            System.out.println("4,模式的格式输入错误!!!");
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    else{
                        int m;
                        ArrayList<ArrayList<Integer>> midresult;
                        for(m=0;l+left+m+1<=r;m++){
                            if(p.charAt(i+6) == SuffixTree.text.charAt(l+left+m+1)){
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(varnode.num).size()==0){
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                    }
                                    else{
                                        int mark=0;
                                        for(int len=0;len<subproblemlist.get(varnode.num).size();len++){
                                            if(subproblemlist.get(varnode.num).get(len).move==left+m+1+move &&
                                                    subproblemlist.get(varnode.num).get(len).pattern.toString().equals(PartP.toString())){
                                                succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                            subproblemlist.get(varnode.num).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                        }
                                    }
                                }
                                else{
                                    occur++;
                                }
                            }
                        }
                        if(p.charAt(i+6)=='^'){
                            System.out.println("5,模式的格式输入错误!!!");
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                        if(occur!=0){
                            succ=(succ+varnode.leafs)*occur;
                        }
                        //midresult=CompareChildChar(varnode,varnode.l,varnode.r,p.charAt(i+6),varnode.r-varnode.l,right);
                        midresult=CompareChildChar(varnode,l,varnode.r,p.charAt(i+6),varnode.r-l,right);
                        for(int from=0;from<midresult.size();from++){
                            int e=midresult.get(from).size()-1,total=0;
                            for(int c=0;c<e-1;c++){
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(midresult.get(from).get(e-1)).size()==0){
                                        SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),PartP,midresult.get(from).get(c));
                                        subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                    }
                                    else{
                                        int mark=0;
                                        for(int len=0;len<subproblemlist.get(midresult.get(from).get(e-1)).size();len++){
                                            if(subproblemlist.get(midresult.get(from).get(e-1)).get(len).move==midresult.get(from).get(c)
                                                    && subproblemlist.get(midresult.get(from).get(e-1)).get(len).pattern.toString().equals(PartP.toString())){
                                                succ=succ+subproblemlist.get(midresult.get(from).get(e-1)).get(len).matchnumber;
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                    PartP,midresult.get(from).get(c));
                                            subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                        }
                                    }
                                }
                                else{
                                    total++;
                                }
                            }
                            if(total!=0){
                                succ=succ+(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).leafs)*total;
                            }
                        }
                        //midresult.clear();
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }
            }
        }
        return searchresult;
    }
}
