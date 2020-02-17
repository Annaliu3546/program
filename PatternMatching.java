package mmst;

/**
 * Created by anna on 17/6/26.
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.lang.String;

import mmst.SearchResult;
import mmst.SfxNode;
import mmst.SuffixTree;

public class PatternMatching {
    static StringBuffer text,midP,patternX;
    public static ArrayList<StringBuffer> pattern=new ArrayList<StringBuffer>();
    public static ArrayList<ArrayList<SearchResult>> subproblemlist=new ArrayList<ArrayList<SearchResult>>();
    public static ArrayList<ArrayList<ArrayList<Integer>>> positionlist=new ArrayList<ArrayList<ArrayList<Integer>>>();
    public static ArrayList<Integer> minN=new ArrayList<Integer>();
    public static ArrayList<Integer> maxM=new ArrayList<Integer>();
    public static int matchnumber=0;
    public static void main(String args[]) throws IOException{
        ArrayList<ArrayList<Integer>> submatchingPosition;//匹配位置数组
        ArrayList<ArrayList<Integer>> dealmatchingPosition;//处理位置，获得首尾位置
        ArrayList<ArrayList<ArrayList<Integer>>> matchPosition;//匹配位置，获得最终位置；
        long begin = new Date().getTime();
        PatternMatching PM=new PatternMatching();
        SfxNode treeroot=new SfxNode();
        SuffixTree tree=new SuffixTree(text);//建树
        treeroot=tree.addText();
        initialize();

        for(int i=0;i<pattern.size();i++){//提取每个Pattern的位置
            patternX=pattern.get(i);//pattern 中只存了一个；
            patternX.append('^');
            SearchResult searchresult=PatternMatching.search(treeroot,patternX,0);//重要 匹配结果
            submatchingPosition=StorePosition(searchresult);//重要匹配位置；//存储时已经排序
            dealmatchingPosition=DealPosition(submatchingPosition);
            positionlist.add(dealmatchingPosition);
        }
        //  long begin = new Date().getTime();
        matchPosition=MatchPosition(positionlist);
        System.out.println("匹配次数： "+ matchnumber);
        //  System.out.println("具体位置： "+ matchPosition);
        long end = new Date().getTime();
        System.out.println("use of time:" + (end - begin)+"ms");
        release();
    }
    public PatternMatching() throws IOException{
        //InputTP();
        ReadTP();
        pattern=dealP(midP);
        //pattern.append('^');
        //  System.out.println("读取的匹配的模式为："+midP.toString());//输出读入的匹配模式a*[0,3]gg*[0,3]t
        //  System.out.println("读取的匹配的模式为："+pattern.toString());//输出处理后的匹配模式a*[!,$]gg*[!,$]t^
    }
    public static ArrayList<ArrayList<Integer>> StorePosition(SearchResult searchresult){
        //该算法将所有的匹配位置存储到一个双重ArrayList当中，并将位置从前到后排序
        ArrayList<ArrayList<Integer>> Position=new ArrayList<ArrayList<Integer>>();
        if(searchresult.matchnumber!=0){
            for(int i=0;i<searchresult.Position.size();i++){ //position的每一行
                for(int j=0;j<searchresult.Position.get(i).get(1).size();j++){  //每一个绝对位置
                    ArrayList<Integer> subposition=new ArrayList<Integer>();
                    int absolute_position=searchresult.Position.get(i).get(1).get(j);
                    for(int k=0;k<searchresult.Position.get(i).get(0).size();k++){
                        int pot=absolute_position+searchresult.Position.get(i).get(0).get(k);
                        subposition.add(pot);
                    }
                    Position.add(subposition);
                }
            }
            Position=SortArrayList.sort(Position);//排序，不排序的话时间会节省一半。需要排序才能比较大小
            return Position;
        }else{
            System.out.println("匹配数为0,不存在位置！");
            return Position;
        }
    }
    public static ArrayList<ArrayList<Integer>> DealPosition(ArrayList<ArrayList<Integer>> submatchingPosition){
        ArrayList<ArrayList<Integer>> Position=new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> subp=new ArrayList<Integer>();
        for(int i=0;i<submatchingPosition.size();i++){
            if(submatchingPosition.get(i).size()==1){//若为一位则复制该位置；
                subp=new ArrayList<Integer>();
                int m=submatchingPosition.get(i).get(0);
                subp.add(m);
                subp.add(m);
            }
            else if(submatchingPosition.get(i).size()>=2){
                subp=new ArrayList<Integer>();
                int j=submatchingPosition.get(i).size();
                int m=submatchingPosition.get(i).get(0);
                int n=submatchingPosition.get(i).get(j-1);
                subp.add(m);
                subp.add(n);
            }
            Position.add(subp);//首尾位置存放于Position中；
        }
        return Position;
    }
    public static ArrayList<ArrayList<ArrayList<Integer>>> MatchPosition(ArrayList<ArrayList<ArrayList<Integer>>> positionlist){
        ArrayList<ArrayList<ArrayList<Integer>>> Position=new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> tempPosition=new ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>();
        ArrayList<ArrayList<ArrayList<Integer>>> Position1=new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<Integer>> subPosition=new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> subPosition2=new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> subl=new ArrayList<Integer>();
        ArrayList<Integer> subr=new ArrayList<Integer>();

        for(int i=0,n=0;i<positionlist.size()-1;i++,n++){
            Position1=new ArrayList<ArrayList<ArrayList<Integer>>>();
            for(int j=0;j<=positionlist.get(i+1).size()-1;j++){
                int r=positionlist.get(i+1).get(j).get(0);//后一项的首位位置；(只有两位，0，1)
                for(int k=0;k<=positionlist.get(i).size()-1;k++){
                    int l=positionlist.get(i).get(k).get(1);//前一项的末尾位置；增加后每次两组递增
                    if(r<l) continue;//如果后一项首位小于前一项末尾，直接进行下一个比较；
                    else{
                        int len=r-l;
                        if(len>=minN.get(n) && len<=maxM.get(n)){
                            subPosition=new ArrayList<ArrayList<Integer>>();
                            subPosition.add(positionlist.get(i).get(k));
                            subPosition.add(positionlist.get(i+1).get(j));
                            Position1.add(subPosition);
                        }
                        else continue;
                    }
                }
                continue;
            }
            tempPosition.add(Position1);
        }
        for(int i=0;i<tempPosition.size()-1;i++){//将符合的位置数组进行合并，[a,b][b,c]合为[a,b,c];
            for(int j=0;j<tempPosition.get(i).size()-1;j++){
                subl=tempPosition.get(i).get(j).get(1);
                for(int k=0;k<tempPosition.get(i+1).size()-1;k++){
                    subr=tempPosition.get(i+1).get(k).get(0);
                    if(subl==subr){//判断b是否相等
                        subPosition2=new ArrayList<ArrayList<Integer>>();
                        subPosition2.add(tempPosition.get(i).get(j).get(0));
                        subPosition2.add(tempPosition.get(i).get(j).get(1));
                        subPosition2.add(tempPosition.get(i+1).get(k).get(1));
                        Position.add(subPosition2);
                        matchnumber++;
                    }else continue;
                }
            }
        }
        return Position;
    }

    /*static ArrayList<ArrayList<Integer>> getMachingPosition(String strText,String strPattern) throws IOException{
        //该方法为外调用的接口,返回所有的匹配位置，size()即为匹配数//了解程序整体运行顺序
        StringBuffer text =  new StringBuffer(strText);
        StringBuffer pattern =  new StringBuffer(strPattern);
        ArrayList<ArrayList<Integer>> matchingPosition;
        PatternMatching1 PM=new PatternMatching1();
        SfxNode treeroot=new SfxNode();
        SuffixTree tree=new SuffixTree(text);
        treeroot=tree.addText();
        PatternMatching1.initialize();
        pattern=dealP(pattern);
        pattern.append('^');
        SearchResult searchresult=PatternMatching1.search(treeroot,pattern,0);
        matchingPosition=StorePosition(searchresult);
        return matchingPosition;
    }*/
    static void release(){//释放内存；
        SuffixTree.g_sfxNodeCnt=0;
        SuffixTree.m_leafCnt=0;
        SuffixTree.m_i=0;
        SuffixTree.m_text=0;
        SuffixTree.m_len=0;
        SuffixTree.z=0;
        SuffixTree.g_sfxNode.clear();
        SuffixTree.text=new StringBuffer();
        PatternMatching.subproblemlist.clear();
    }
    public static void initialize(){//初始化
        int much=SuffixTree.g_sfxNodeCnt;
        subproblemlist.ensureCapacity(much);
        for(int m=0;m<much;m++){
            ArrayList<SearchResult> midresult = new ArrayList<SearchResult>();
            StringBuffer sb=new StringBuffer("g");
            ArrayList<ArrayList<ArrayList<Integer>>> Position=new ArrayList<ArrayList<ArrayList<Integer>>>();
            SearchResult sr=new SearchResult(0,sb,1,Position);
            midresult.add(sr);
            subproblemlist.add(midresult);
        }
    }

    public void ReadTP() throws IOException{
        //text=this.ReadFromText("src/test/new.txt");
        text=this.ReadFromText("src/test/dna.txt");
        //midP=this.ReadFromText("src/test/pattern.txt");
        //text=this.ReadFromText("src/test/AX829170.txt");
        midP=this.ReadFromText("src/test/pattern.txt");
    }
    public StringBuffer ReadFromText(String str) throws IOException{
        FileReader y=new FileReader(str);
        BufferedReader br=new BufferedReader(y);
        StringBuffer text=new StringBuffer(br.readLine());
        return  text;
    }

    public static ArrayList<StringBuffer> dealP(StringBuffer p){//处理Pattern，将没有间隔的放在一起 ，间隔存储在数组；
        ArrayList<StringBuffer> arraystring=new ArrayList<StringBuffer>();
        StringBuffer array1=new StringBuffer();
        String s="";
        int i=0;
        while(i<p.length()){
            if(p.charAt(i)!='*'){//以*为判断存入字符串；
                s=String.valueOf(p.toString().charAt(i));//将字符转换为字符串,存储在array中
                array1.append(s);//
                i++;
                continue;
            }else{
                arraystring.add(array1);//将转换好的pattern存储。
                array1=new StringBuffer();
                minN.add(Integer.parseInt(String.valueOf(p.charAt(i+2)))+1);//记录N 间隔需加*数量+1，表示位置
                maxM.add(Integer.parseInt(String.valueOf(p.charAt(i+4)))+1);//记录M 同上 加1
                i=i+6;//*[N,M]共占6位 ，有问题当有两位数以上，就不占6位（先考虑个位数问题）；
                continue;
            }
        }
        arraystring.add(array1);//将转换好的pattern存储。
        return arraystring;
    }

    public static int minlen(String p){//最小的长度
        int len=0,i=0;
        while(i<p.length()-1){
            if(p.charAt(i)!='*'){
                len++;
                i++;
            }else{
                len=len+(p.charAt(i+2)-33);//33是！i+2？
                i=i+6;
            }
        }
        return len;
    }
    public static int maxlen(String p){//最大的长度
        int len=0,i=0;
        while(i<p.length()-1){
            if(p.charAt(i)!='*'){
                len++;
                i++;
            }else{
                len=len+(p.charAt(i+4)-33);//i+4？
                i=i+6;
            }
        }
        return len;
    }
    public static ArrayList<ArrayList<Integer>> CompareChildChar(SfxNode node,int l,int r,char p,int left,int right){
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
            }else if(chnode.ch==null && (chnode.l+left)>chnode.r){
                chnode=chnode.csn;
                continue;
            }else if(chnode.ch==null && (chnode.l+left)<=chnode.r){
                int right2 = 0;
                if(right>chnode.r-chnode.l-1){
                    right2=chnode.r-chnode.l-1;
                }else{
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
                }else{
                    result.remove(result.size()-1);
                    chnode=chnode.csn;
                    continue;
                }
            }else if(chnode.ch!=null && right<=chnode.r-chnode.l){
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
                }else{
                    result.remove(result.size()-1);
                    chnode=chnode.csn;
                    continue;
                }
            }else{
                if(left>(chnode.r-chnode.l)){
                    ArrayList<ArrayList<Integer>> midresult;
                    midresult=CompareChildChar(chnode,chnode.l,chnode.r,p,left-1,right-1);
                    for(int c=0;c<midresult.size();c++){
                        result.add(midresult.get(c));
                        t++;
                    }
                    midresult.clear();
                    chnode=chnode.csn;
                    continue;
                }else{
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
                    midresult.clear();
                    chnode=chnode.csn;
                    continue;
                }
            }
        }
        return result;
    }

    public static SearchResult search(SfxNode treeroot,StringBuffer p,int move){
        ArrayList<ArrayList<ArrayList<Integer>>> Position=new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<Integer>> leb=new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> leb1=new ArrayList<Integer>();
        ArrayList<Integer> leb2=new ArrayList<Integer>();
        leb.add(leb1);
        leb.add(leb2);
        Position.add(leb);
        int succ=0,l,r,group=0;//用于记录有多少组位置，即Position有多少行
        SearchResult searchresult=new SearchResult(move,p,0,Position);
        SfxNode varnode=treeroot;
        if(treeroot.num==0){
            varnode=SfxNode.findchild(varnode,p.charAt(0));
            if(varnode==treeroot){
                return searchresult;
            }else{
                searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode, varnode.l));
                if(p.length()-1==1){
                    if(varnode.ch!=null){
                        succ=succ+varnode.leafs;
                        searchresult.matchnumber=succ;
                        searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
                        return searchresult;
                    }else{
                        searchresult.matchnumber=++succ;
                        searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
                        return searchresult;
                    }
                }
            }
            l=varnode.l+move;
            r=varnode.r;
        }else{
            l=varnode.l+move;
            r=varnode.r;
        }
        int i=1;
        while(i<p.length()-1){
            if(p.charAt(i)!='*'){
                if(varnode.ch==null && minlen(p.substring(i))>(r-l-1)){
                    searchresult.matchnumber=succ;
                    return searchresult;
                }else if(varnode.ch==null && minlen(p.substring(i))<=(r-l-1)){
                    if(p.charAt(i)==SuffixTree.text.charAt(l+1)){
                        i++;
                        l++;
                        move++;
                        searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l));
                    }else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(l>=r && i<p.length()-1){
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(p.charAt(i)=='^' || i==p.length()-1){
                        searchresult.matchnumber=++succ;
                        searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
                        return searchresult;
                    }
                }
                else if(varnode.ch!=null && maxlen(p.substring(i))<=(r-l)){
                    if(p.charAt(i)==SuffixTree.text.charAt(l+1)){
                        i++;
                        l++;
                        move++;
                        searchresult.Position.get(group).get(0).add(SfxNode.computeThisNodeCharacters(varnode, l));
                    }else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                    if(p.charAt(i)=='^' || i==p.length()-1 ){
                        succ=succ+varnode.leafs;
                        searchresult.matchnumber=succ;
                        searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
                        return searchresult;
                    }
                }else{
                    if(l+1<=r){
                        if(p.charAt(i)== SuffixTree.text.charAt(l+1)){
                            i++;
                            l++;
                            move++;
                            searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l));
                        }else{
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                    }else{
                        SfxNode midnode=varnode;
                        varnode=SfxNode.findchild(varnode,p.charAt(i));
                        if(varnode==midnode){
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }else{
                            l=varnode.l;
                            r=varnode.r;
                            i++;
                            move=0;
                            searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l));
                        }
                    }
                    if(l>=r && i<p.length()-1){
                        if(p.charAt(i)!='*'){
                            SfxNode midlnode=varnode;
                            varnode=SfxNode.findchild(varnode,p.charAt(i));
                            if(varnode==midlnode){
                                searchresult.matchnumber=succ;
                                return searchresult;
                            }else{
                                l=varnode.l;
                                r=varnode.r;
                                i++;
                                move=0;
                                searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l));
                            }
                        }else{
                            StringBuffer PartP=new StringBuffer(p.substring(i-1));
                            if(subproblemlist.get(varnode.num).size()==1){
                                int record=1;
                                SearchResult midsearchresult=search(varnode,PartP,varnode.r-varnode.l);
                                subproblemlist.get(varnode.num).add(midsearchresult);
                                succ=succ+midsearchresult.matchnumber;
                                searchresult.matchnumber=succ;
                                ArrayList<Integer> local0=new ArrayList<Integer>();
                                ArrayList<Integer> local1=new ArrayList<Integer>();
                                local0.addAll(searchresult.Position.get(group).get(0));
                                local1.addAll(searchresult.Position.get(group).get(1));
                                for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                    if(record==1){
                                        searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                        searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                        record++;
                                    }else{
                                        ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                        ArrayList<Integer> leb31=new ArrayList<Integer>();
                                        ArrayList<Integer> leb32=new ArrayList<Integer>();
                                        leb3.add(leb31);
                                        leb3.add(leb32);
                                        searchresult.Position.add(leb3);
                                        group++;
                                        searchresult.Position.get(group).get(0).addAll(local0);
                                        searchresult.Position.get(group).get(1).addAll(local1);
                                        searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                        searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                    }
                                }
                                i=i+7;
                                return searchresult;
                            }else{
                                int mark=0;
                                for(int len=1;len<subproblemlist.get(varnode.num).size();len++){
                                    if(subproblemlist.get(varnode.num).get(len).move==(varnode.r-varnode.l) &&
                                            subproblemlist.get(varnode.num).get(len).pattern==PartP){
                                        int record=1;
                                        succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                        searchresult.matchnumber=succ;
                                        ArrayList<Integer> local0=new ArrayList<Integer>();
                                        ArrayList<Integer> local1=new ArrayList<Integer>();
                                        local0.addAll(searchresult.Position.get(group).get(0));
                                        local1.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<subproblemlist.get(varnode.num).get(len).Position.size();localgroup++){
                                            if(record==1){
                                                searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                record++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local0);
                                                searchresult.Position.get(group).get(1).addAll(local1);
                                                searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                            }
                                        }
                                        i=i+7;
                                        return searchresult;
                                    }
                                }
                                if(mark==0){
                                    int record=1;
                                    SearchResult midsearchresult=search(varnode,PartP,varnode.r-varnode.l);
                                    subproblemlist.get(varnode.num).add(midsearchresult);
                                    succ=succ+midsearchresult.matchnumber;
                                    searchresult.matchnumber=succ;
                                    ArrayList<Integer> local0=new ArrayList<Integer>();
                                    ArrayList<Integer> local1=new ArrayList<Integer>();
                                    local0.addAll(searchresult.Position.get(group).get(0));
                                    local1.addAll(searchresult.Position.get(group).get(1));
                                    for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                        if(record==1){
                                            searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                            searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            record++;
                                        }else{
                                            ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                            ArrayList<Integer> leb31=new ArrayList<Integer>();
                                            ArrayList<Integer> leb32=new ArrayList<Integer>();
                                            leb3.add(leb31);
                                            leb3.add(leb32);
                                            searchresult.Position.add(leb3);
                                            group++;
                                            searchresult.Position.get(group).get(0).addAll(local0);
                                            searchresult.Position.get(group).get(1).addAll(local1);
                                            searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                            searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                        }
                                    }
                                    i=i+7;
                                    return searchresult;
                                }
                            }
                        }
                    }
                    if(i==p.length()-1 || p.charAt(i)=='^'){
                        succ=succ+varnode.leafs;
                        searchresult.matchnumber=succ;
                        searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
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
                }else if(varnode.ch==null && minlen(p.substring(i))<=(r-l-1)){
                    int m,record=1;
                    if(right>=r-l-1){
                        right=r-l-2;
                    }
                    ArrayList<Integer> local0=new ArrayList<Integer>();
                    ArrayList<Integer> local1=new ArrayList<Integer>();
                    local0.addAll(searchresult.Position.get(group).get(0));
                    local1.addAll(searchresult.Position.get(group).get(1));
                    for(m=0;m<right-left+1;m++){
                        if(p.charAt(i+6)== SuffixTree.text.charAt(l+left+m+1)){
                            if(record==1){
                                searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                                record++;
                            }else{
                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                leb3.add(leb31);
                                leb3.add(leb32);
                                searchresult.Position.add(leb3);
                                group++;
                                searchresult.Position.get(group).get(0).addAll(local0);
                                searchresult.Position.get(group).get(1).addAll(local1);
                                searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                            }
                            if(p.charAt(i+7)!='^'){
                                StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                if(subproblemlist.get(varnode.num).size()==1){
                                    int record2=1;
                                    SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                    subproblemlist.get(varnode.num).add(midsearchresult);
                                    succ=succ+midsearchresult.matchnumber;
                                    ArrayList<Integer> local02=new ArrayList<Integer>();
                                    ArrayList<Integer> local12=new ArrayList<Integer>();
                                    local02.addAll(searchresult.Position.get(group).get(0));
                                    local12.addAll(searchresult.Position.get(group).get(1));
                                    for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                        if(record2==1){
                                            searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                            searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            record2++;
                                        }else{
                                            ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                            ArrayList<Integer> leb31=new ArrayList<Integer>();
                                            ArrayList<Integer> leb32=new ArrayList<Integer>();
                                            leb3.add(leb31);
                                            leb3.add(leb32);
                                            searchresult.Position.add(leb3);
                                            group++;
                                            searchresult.Position.get(group).get(0).addAll(local02);
                                            searchresult.Position.get(group).get(1).addAll(local12);
                                            searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                            searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                        }
                                    }
                                }else{
                                    int mark=0;
                                    for(int len=1;len<subproblemlist.get(varnode.num).size();len++){
                                        if(subproblemlist.get(varnode.num).get(len).move==left+m+1+move &&
                                                subproblemlist.get(varnode.num).get(len).pattern==PartP){
                                            int record3=1;
                                            succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                            ArrayList<Integer> local03=new ArrayList<Integer>();
                                            ArrayList<Integer> local13=new ArrayList<Integer>();
                                            local03.addAll(searchresult.Position.get(group).get(0));
                                            local13.addAll(searchresult.Position.get(group).get(1));
                                            for(int localgroup=0;localgroup<subproblemlist.get(varnode.num).get(len).Position.size();localgroup++){
                                                if(record3==1){
                                                    searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                    record3++;
                                                }else{
                                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                    leb3.add(leb31);
                                                    leb3.add(leb32);
                                                    searchresult.Position.add(leb3);
                                                    group++;
                                                    searchresult.Position.get(group).get(0).addAll(local03);
                                                    searchresult.Position.get(group).get(1).addAll(local13);
                                                    searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                }
                                            }
                                            mark=1;
                                            break;
                                        }
                                    }
                                    if(mark==0){
                                        int record4=1;
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                        ArrayList<Integer> local04=new ArrayList<Integer>();
                                        ArrayList<Integer> local14=new ArrayList<Integer>();
                                        local04.addAll(searchresult.Position.get(group).get(0));
                                        local14.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                            if(record4==1){
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                record4++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local04);
                                                searchresult.Position.get(group).get(1).addAll(local14);
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            }
                                        }
                                    }
                                }
                            }else{
                                occur++;
                                searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
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
                    }else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }else if(varnode.ch!=null && maxlen(p.substring(i))<=r-l){
                    int m,record=1;
                    ArrayList<Integer> local0=new ArrayList<Integer>();
                    ArrayList<Integer> local1=new ArrayList<Integer>();
                    local0.addAll(searchresult.Position.get(group).get(0));
                    local1.addAll(searchresult.Position.get(group).get(1));
                    for(m=0;m<right-left+1;m++){
                        if(p.charAt(i+6)== SuffixTree.text.charAt(l+left+m+1)){
                            if(record==1){
                                searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                                record++;
                            }else{
                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                leb3.add(leb31);
                                leb3.add(leb32);
                                searchresult.Position.add(leb3);
                                group++;
                                searchresult.Position.get(group).get(0).addAll(local0);
                                searchresult.Position.get(group).get(1).addAll(local1);
                                searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                            }
                            if(p.charAt(i+7)!='^'){
                                StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                if(subproblemlist.get(varnode.num).size()==1){
                                    int record2=1;
                                    SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                    subproblemlist.get(varnode.num).add(midsearchresult);
                                    succ=succ+midsearchresult.matchnumber;
                                    ArrayList<Integer> local02=new ArrayList<Integer>();
                                    ArrayList<Integer> local12=new ArrayList<Integer>();
                                    local02.addAll(searchresult.Position.get(group).get(0));
                                    local12.addAll(searchresult.Position.get(group).get(1));
                                    for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                        if(record2==1){
                                            searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                            searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            record2++;
                                        }else{
                                            ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                            ArrayList<Integer> leb31=new ArrayList<Integer>();
                                            ArrayList<Integer> leb32=new ArrayList<Integer>();
                                            leb3.add(leb31);
                                            leb3.add(leb32);
                                            searchresult.Position.add(leb3);
                                            group++;
                                            searchresult.Position.get(group).get(0).addAll(local02);
                                            searchresult.Position.get(group).get(1).addAll(local12);
                                            searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                            searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                        }
                                    }
                                }else{
                                    int mark=0;
                                    for(int len=1;len<subproblemlist.get(varnode.num).size();len++){
                                        if(subproblemlist.get(varnode.num).get(len).move==left+m+1+move &&
                                                subproblemlist.get(varnode.num).get(len).pattern==PartP){
                                            int record3=1;
                                            succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                            ArrayList<Integer> local03=new ArrayList<Integer>();
                                            ArrayList<Integer> local13=new ArrayList<Integer>();
                                            local03.addAll(searchresult.Position.get(group).get(0));
                                            local13.addAll(searchresult.Position.get(group).get(1));
                                            for(int localgroup=0;localgroup<subproblemlist.get(varnode.num).get(len).Position.size();localgroup++){
                                                if(record3==1){
                                                    searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                    record3++;
                                                }else{
                                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                    leb3.add(leb31);
                                                    leb3.add(leb32);
                                                    searchresult.Position.add(leb3);
                                                    group++;
                                                    searchresult.Position.get(group).get(0).addAll(local03);
                                                    searchresult.Position.get(group).get(1).addAll(local13);
                                                    searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                }
                                            }
                                            mark=1;
                                            break;
                                        }
                                    }
                                    if(mark==0){
                                        int record4=1;
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                        ArrayList<Integer> local04=new ArrayList<Integer>();
                                        ArrayList<Integer> local14=new ArrayList<Integer>();
                                        local04.addAll(searchresult.Position.get(group).get(0));
                                        local14.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                            if(record4==1){
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                record4++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local04);
                                                searchresult.Position.get(group).get(1).addAll(local14);
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            }
                                        }

                                    }
                                }
                            }else{
                                occur++;
                                searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
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
                    }else{
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }else{
                    if(right<r-l){
                        int m,record=1;
                        ArrayList<Integer> local0=new ArrayList<Integer>();
                        ArrayList<Integer> local1=new ArrayList<Integer>();
                        local0.addAll(searchresult.Position.get(group).get(0));
                        local1.addAll(searchresult.Position.get(group).get(1));
                        for(m=0;m<right-left+1;m++){
                            if(p.charAt(i+6)== SuffixTree.text.charAt(l+left+m+1)){
                                if(record==1){
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                                    record++;
                                }else{
                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                    leb3.add(leb31);
                                    leb3.add(leb32);
                                    searchresult.Position.add(leb3);
                                    group++;
                                    searchresult.Position.get(group).get(0).addAll(local0);
                                    searchresult.Position.get(group).get(1).addAll(local1);
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                                }
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(varnode.num).size()==1){
                                        int record2=1;
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                        ArrayList<Integer> local02=new ArrayList<Integer>();
                                        ArrayList<Integer> local12=new ArrayList<Integer>();
                                        local02.addAll(searchresult.Position.get(group).get(0));
                                        local12.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                            if(record2==1){
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                record2++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local02);
                                                searchresult.Position.get(group).get(1).addAll(local12);
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            }
                                        }
                                    }else{
                                        int mark=0;
                                        for(int len=1;len<subproblemlist.get(varnode.num).size();len++){
                                            if(subproblemlist.get(varnode.num).get(len).move==left+m+1+move &&
                                                    subproblemlist.get(varnode.num).get(len).pattern==PartP){
                                                int record3=1;
                                                succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                                ArrayList<Integer> local03=new ArrayList<Integer>();
                                                ArrayList<Integer> local13=new ArrayList<Integer>();
                                                local03.addAll(searchresult.Position.get(group).get(0));
                                                local13.addAll(searchresult.Position.get(group).get(1));
                                                for(int localgroup=0;localgroup<subproblemlist.get(varnode.num).get(len).Position.size();localgroup++){
                                                    if(record3==1){
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                        record3++;
                                                    }else{
                                                        ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                        ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                        ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                        leb3.add(leb31);
                                                        leb3.add(leb32);
                                                        searchresult.Position.add(leb3);
                                                        group++;
                                                        searchresult.Position.get(group).get(0).addAll(local03);
                                                        searchresult.Position.get(group).get(1).addAll(local13);
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                    }
                                                }
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            int record4=1;
                                            SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                            subproblemlist.get(varnode.num).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                            ArrayList<Integer> local04=new ArrayList<Integer>();
                                            ArrayList<Integer> local14=new ArrayList<Integer>();
                                            local04.addAll(searchresult.Position.get(group).get(0));
                                            local14.addAll(searchresult.Position.get(group).get(1));
                                            for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                                if(record4==1){
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                    record4++;
                                                }else{
                                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                    leb3.add(leb31);
                                                    leb3.add(leb32);
                                                    searchresult.Position.add(leb3);
                                                    group++;
                                                    searchresult.Position.get(group).get(0).addAll(local04);
                                                    searchresult.Position.get(group).get(1).addAll(local14);
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    occur++;
                                    searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
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
                    }else if(left>=r-l){
                        int record=1;
                        ArrayList<Integer> local0=new ArrayList<Integer>();
                        ArrayList<Integer> local1=new ArrayList<Integer>();
                        local0.addAll(searchresult.Position.get(group).get(0));
                        local1.addAll(searchresult.Position.get(group).get(1));
                        ArrayList< ArrayList<Integer> > midresult;
                        midresult=CompareChildChar(varnode,l,r,p.charAt(i+6),left,right);
                        for(int from=0;from<midresult.size();from++){
                            int e=midresult.get(from).size()-1,total=0;
                            for(int c=0;c<e-1;c++){
                                if(record==1){
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(
                                            SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).l+midresult.get(from).get(c)));
                                    record++;
                                }else{
                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                    leb3.add(leb31);
                                    leb3.add(leb32);
                                    searchresult.Position.add(leb3);
                                    group++;
                                    searchresult.Position.get(group).get(0).addAll(local0);
                                    searchresult.Position.get(group).get(1).addAll(local1);
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(
                                            SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).l+midresult.get(from).get(c)));
                                }
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(midresult.get(from).get(e-1)).size()==1){
                                        int record2=1;
                                        SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                PartP,midresult.get(from).get(c));
                                        subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                        ArrayList<Integer> local02=new ArrayList<Integer>();
                                        ArrayList<Integer> local12=new ArrayList<Integer>();
                                        local02.addAll(searchresult.Position.get(group).get(0));
                                        local12.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                            if(record2==1){
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                record2++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local02);
                                                searchresult.Position.get(group).get(1).addAll(local12);
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            }
                                        }
                                    }else{
                                        int mark=0;
                                        for(int len=1;len<subproblemlist.get(midresult.get(from).get(e-1)).size();len++){
                                            if(subproblemlist.get(midresult.get(from).get(e-1)).get(len).move==midresult.get(from).get(c)
                                                    && subproblemlist.get(midresult.get(from).get(e-1)).get(len).pattern==PartP){
                                                int record3=1;
                                                succ=succ+subproblemlist.get(midresult.get(from).get(e-1)).get(len).matchnumber;
                                                ArrayList<Integer> local03=new ArrayList<Integer>();
                                                ArrayList<Integer> local13=new ArrayList<Integer>();
                                                local03.addAll(searchresult.Position.get(group).get(0));
                                                local13.addAll(searchresult.Position.get(group).get(1));
                                                for(int localgroup=0;localgroup<subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.size();localgroup++){
                                                    if(record3==1){
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(1));
                                                        record3++;
                                                    }else{
                                                        ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                        ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                        ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                        leb3.add(leb31);
                                                        leb3.add(leb32);
                                                        searchresult.Position.add(leb3);
                                                        group++;
                                                        searchresult.Position.get(group).get(0).addAll(local03);
                                                        searchresult.Position.get(group).get(1).addAll(local13);
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(1));
                                                    }
                                                }
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            int record4=1;
                                            SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                    PartP,midresult.get(from).get(c));
                                            subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                            ArrayList<Integer> local04=new ArrayList<Integer>();
                                            ArrayList<Integer> local14=new ArrayList<Integer>();
                                            local04.addAll(searchresult.Position.get(group).get(0));
                                            local14.addAll(searchresult.Position.get(group).get(1));
                                            for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                                if(record4==1){
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                    record4++;
                                                }else{
                                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                    leb3.add(leb31);
                                                    leb3.add(leb32);
                                                    searchresult.Position.add(leb3);
                                                    group++;
                                                    searchresult.Position.get(group).get(0).addAll(local04);
                                                    searchresult.Position.get(group).get(1).addAll(local14);
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    total++;
                                    searchresult.Position.get(group).get(1).addAll(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).listOfStartPosition);
                                }
                            }
                            if(total!=0){
                                succ=succ+(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).leafs)*total;
                            }
                        }
                        midresult.clear();
                        if(p.charAt(i+6)=='^'){
                            System.out.println("4,模式的格式输入错误!!!");
                            searchresult.matchnumber=succ;
                            return searchresult;
                        }
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }else{
                        int m,record=1;
                        ArrayList<Integer> local0=new ArrayList<Integer>();
                        ArrayList<Integer> local1=new ArrayList<Integer>();
                        local0.addAll(searchresult.Position.get(group).get(0));
                        local1.addAll(searchresult.Position.get(group).get(1));
                        ArrayList<ArrayList<Integer>> midresult;
                        for(m=0;l+left+m+1<=r;m++){
                            if(p.charAt(i+6) == SuffixTree.text.charAt(l+left+m+1)){
                                if(record==1){
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                                    record++;
                                }else{
                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                    leb3.add(leb31);
                                    leb3.add(leb32);
                                    searchresult.Position.add(leb3);
                                    group++;
                                    searchresult.Position.get(group).get(0).addAll(local0);
                                    searchresult.Position.get(group).get(1).addAll(local1);
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(varnode,l+left+m+1));
                                }
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(varnode.num).size()==1){
                                        int record2=1;
                                        SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                        subproblemlist.get(varnode.num).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                        ArrayList<Integer> local02=new ArrayList<Integer>();
                                        ArrayList<Integer> local12=new ArrayList<Integer>();
                                        local02.addAll(searchresult.Position.get(group).get(0));
                                        local12.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                            if(record2==1){
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                record2++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local02);
                                                searchresult.Position.get(group).get(1).addAll(local12);
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            }
                                        }
                                    }else{
                                        int mark=0;
                                        for(int len=1;len<subproblemlist.get(varnode.num).size();len++){
                                            if(subproblemlist.get(varnode.num).get(len).move==left+m+1+move &&
                                                    subproblemlist.get(varnode.num).get(len).pattern==PartP){
                                                int record3=1;
                                                succ=succ+subproblemlist.get(varnode.num).get(len).matchnumber;
                                                ArrayList<Integer> local03=new ArrayList<Integer>();
                                                ArrayList<Integer> local13=new ArrayList<Integer>();
                                                local03.addAll(searchresult.Position.get(group).get(0));
                                                local13.addAll(searchresult.Position.get(group).get(1));
                                                for(int localgroup=0;localgroup<subproblemlist.get(varnode.num).get(len).Position.size();localgroup++){
                                                    if(record3==1){
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                        record3++;
                                                    }else{
                                                        ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                        ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                        ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                        leb3.add(leb31);
                                                        leb3.add(leb32);
                                                        searchresult.Position.add(leb3);
                                                        group++;
                                                        searchresult.Position.get(group).get(0).addAll(local03);
                                                        searchresult.Position.get(group).get(1).addAll(local13);
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(varnode.num).get(len).Position.get(localgroup).get(1));
                                                    }
                                                }
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            int record4=1;
                                            SearchResult midsearchresult=search(varnode,PartP,left+m+1+move);
                                            subproblemlist.get(varnode.num).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                            ArrayList<Integer> local04=new ArrayList<Integer>();
                                            ArrayList<Integer> local14=new ArrayList<Integer>();
                                            local04.addAll(searchresult.Position.get(group).get(0));
                                            local14.addAll(searchresult.Position.get(group).get(1));
                                            for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                                if(record4==1){
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                    record4++;
                                                }else{
                                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                    leb3.add(leb31);
                                                    leb3.add(leb32);
                                                    searchresult.Position.add(leb3);
                                                    group++;
                                                    searchresult.Position.get(group).get(0).addAll(local04);
                                                    searchresult.Position.get(group).get(1).addAll(local14);
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    occur++;
                                    searchresult.Position.get(group).get(1).addAll(varnode.listOfStartPosition);
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
                        midresult=CompareChildChar(varnode,l,varnode.r,p.charAt(i+6),varnode.r-l,right);
                        for(int from=0;from<midresult.size();from++){
                            int e=midresult.get(from).size()-1,total=0;
                            for(int c=0;c<e-1;c++){
                                if(record==1){
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(
                                            SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).l+midresult.get(from).get(c)));
                                    record++;
                                }else{
                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                    leb3.add(leb31);
                                    leb3.add(leb32);
                                    searchresult.Position.add(leb3);
                                    group++;
                                    searchresult.Position.get(group).get(0).addAll(local0);
                                    searchresult.Position.get(group).get(1).addAll(local1);
                                    searchresult.Position.get(group).get(0).add(SfxNode.calculatePathNumberOfCharacters(
                                            SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).l+midresult.get(from).get(c)));

                                }
                                if(p.charAt(i+7)!='^'){
                                    StringBuffer PartP=new StringBuffer(p.substring(i+6));
                                    if(subproblemlist.get(midresult.get(from).get(e-1)).size()==1){
                                        int record2=1;
                                        SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                PartP,midresult.get(from).get(c));
                                        subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                        succ=succ+midsearchresult.matchnumber;
                                        ArrayList<Integer> local02=new ArrayList<Integer>();
                                        ArrayList<Integer> local12=new ArrayList<Integer>();
                                        local02.addAll(searchresult.Position.get(group).get(0));
                                        local12.addAll(searchresult.Position.get(group).get(1));
                                        for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                            if(record2==1){
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                record2++;
                                            }else{
                                                ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                leb3.add(leb31);
                                                leb3.add(leb32);
                                                searchresult.Position.add(leb3);
                                                group++;
                                                searchresult.Position.get(group).get(0).addAll(local02);
                                                searchresult.Position.get(group).get(1).addAll(local12);
                                                searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                            }
                                        }
                                    }else{
                                        int mark=0;
                                        for(int len=1;len<subproblemlist.get(midresult.get(from).get(e-1)).size();len++){
                                            if(subproblemlist.get(midresult.get(from).get(e-1)).get(len).move==midresult.get(from).get(c)
                                                    && subproblemlist.get(midresult.get(from).get(e-1)).get(len).pattern==PartP){
                                                int record3=1;
                                                succ=succ+subproblemlist.get(midresult.get(from).get(e-1)).get(len).matchnumber;
                                                ArrayList<Integer> local03=new ArrayList<Integer>();
                                                ArrayList<Integer> local13=new ArrayList<Integer>();
                                                local03.addAll(searchresult.Position.get(group).get(0));
                                                local13.addAll(searchresult.Position.get(group).get(1));
                                                for(int localgroup=0;localgroup<subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.size();localgroup++){
                                                    if(record3==1){
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(1));
                                                        record3++;
                                                    }else{
                                                        ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                        ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                        ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                        leb3.add(leb31);
                                                        leb3.add(leb32);
                                                        searchresult.Position.add(leb3);
                                                        group++;
                                                        searchresult.Position.get(group).get(0).addAll(local03);
                                                        searchresult.Position.get(group).get(1).addAll(local13);
                                                        searchresult.Position.get(group).get(0).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(0));
                                                        searchresult.Position.get(group).get(1).addAll(subproblemlist.get(midresult.get(from).get(e-1)).get(len).Position.get(localgroup).get(1));
                                                    }
                                                }
                                                mark=1;
                                                break;
                                            }
                                        }
                                        if(mark==0){
                                            int record4=1;
                                            SearchResult midsearchresult=search(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)),
                                                    PartP,midresult.get(from).get(c));
                                            subproblemlist.get(midresult.get(from).get(e-1)).add(midsearchresult);
                                            succ=succ+midsearchresult.matchnumber;
                                            ArrayList<Integer> local04=new ArrayList<Integer>();
                                            ArrayList<Integer> local14=new ArrayList<Integer>();
                                            local04.addAll(searchresult.Position.get(group).get(0));
                                            local14.addAll(searchresult.Position.get(group).get(1));
                                            for(int localgroup=0;localgroup<midsearchresult.Position.size();localgroup++){
                                                if(record4==1){
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                    record4++;
                                                }else{
                                                    ArrayList<ArrayList<Integer>> leb3=new ArrayList<ArrayList<Integer>>();
                                                    ArrayList<Integer> leb31=new ArrayList<Integer>();
                                                    ArrayList<Integer> leb32=new ArrayList<Integer>();
                                                    leb3.add(leb31);
                                                    leb3.add(leb32);
                                                    searchresult.Position.add(leb3);
                                                    group++;
                                                    searchresult.Position.get(group).get(0).addAll(local04);
                                                    searchresult.Position.get(group).get(1).addAll(local14);
                                                    searchresult.Position.get(group).get(0).addAll(midsearchresult.Position.get(localgroup).get(0));
                                                    searchresult.Position.get(group).get(1).addAll(midsearchresult.Position.get(localgroup).get(1));
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    total++;
                                    searchresult.Position.get(group).get(1).addAll(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).listOfStartPosition);
                                }
                            }
                            if(total!=0){
                                succ=succ+(SuffixTree.g_sfxNode.get(midresult.get(from).get(e-1)).leafs)*total;
                            }
                        }
                        midresult.clear();
                        searchresult.matchnumber=succ;
                        return searchresult;
                    }
                }
            }
        }
        return searchresult;
    }
}
