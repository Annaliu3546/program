package mmst;

/**
 * Created by anna on 17/6/26.
 */
import java.util.ArrayList;

public class SfxNode {
    public int l,r,num,leafs,suffixStartPosition,toRootPathCharNumber;
    /*leafs代表每个节点的叶子数，suffixStartPosition代表叶子节点对应的后缀在文本中的起始位置（只有叶子节点有）
    l,r代表节点中字符串的开始结束位置,toRootPathCharNumber代表节点最后一个字符到根节点共有多少个字符*/
    public SfxNode sfxLink,prnt,ch,pre,csn;
    public ArrayList<Integer> from=new ArrayList<Integer>();
    ArrayList<Integer> listOfStartPosition=new ArrayList<Integer>();
    /*listOfStartPosition代表每个节点的所有叶子对应的后缀的在文本中的起始位置列表*/
    public SfxNode(){
        leafs=suffixStartPosition=toRootPathCharNumber=-1;
        l=r=num=0;
        sfxLink=prnt=ch=pre=csn=null;
    }
    public SfxNode getchild(char c){
        SfxNode p = null;
        if(ch!=null && SuffixTree.text.charAt(ch.l) <= c)
            p=ch;
        else
            return p;
        while(p.csn!=null && SuffixTree.text.charAt(p.csn.l)<=c)
            p=p.csn;
        return p;
    }
    public void addChild(SfxNode p,SfxNode add){
        add.prnt=this;
        if(p!=null){
            add.csn=p.csn;
            add.pre=p;
            p.csn=add;
            if(add.csn!=null)
                add.csn.pre=add;
        }else{
            add.pre=null;
            add.csn=this.ch;
            this.ch=add;
            if(add.csn!=null)
                add.csn.pre=add;
        }
    }
    public static SfxNode newNode(int l,int r){
        SfxNode p = new SfxNode();
        p.l=l;
        p.r=r;
        p.from.clear();
        p.leafs=-1;
        p.sfxLink=p.prnt=p.ch=p.pre=p.csn=null;
        p.num=SuffixTree.g_sfxNodeCnt++;
        SuffixTree.g_sfxNode.add(p);
        return p;
    }
    public static SfxNode findchild(SfxNode fixnode,char cha){
        SfxNode varnode=fixnode;
        SfxNode node=varnode.ch;
        if(node!=null){
            while(node!=null){
                if(SuffixTree.text.charAt(node.l)==cha){
                    varnode=node;
                    return varnode;
                }
                node=node.csn;
            }
        }
        return varnode;
    }
    public static ArrayList<Integer> NumberOfLeafAndList(SfxNode node){// 该方法得到该节点所有叶子的个数以及叶子的起始位置
        int count=0;
        ArrayList<Integer> listPosition=new ArrayList<Integer>();
        SfxNode chnode=node.ch;
        if(chnode!=null){
            while(chnode!=null){
                if(chnode.leafs!=-1){
                    listPosition.addAll(chnode.listOfStartPosition);
                    count=count+chnode.leafs;
                    chnode=chnode.csn;
                }
                else{
                    chnode.listOfStartPosition=NumberOfLeafAndList(chnode);
                    chnode.leafs=chnode.listOfStartPosition.size();
                    listPosition.addAll(chnode.listOfStartPosition);
                    count=count+chnode.leafs;
                    chnode=chnode.csn;
                }
            }
            return listPosition;
        }
        else{
            listPosition.add(node.suffixStartPosition);
            node.leafs=1;
            return listPosition;
        }
    }
    public static int calculatePathNumberOfCharacters(SfxNode node,int endposition){ //该方法计算该节点从move开始的字符到根节点对应的路径中有多少字符
        int numberofchar=0;
        numberofchar=computeThisNodeCharacters(node,endposition);
        SfxNode chnode=node.prnt;
        while(chnode!=null && chnode.num!=0){
            if(chnode.toRootPathCharNumber!=-1){
                numberofchar=numberofchar+chnode.toRootPathCharNumber;
                break;
            }
            else{
                numberofchar=numberofchar+computeThisNodeCharacters(chnode,chnode.r);
                chnode=chnode.prnt;
            }
        }
        return numberofchar;
    }
    public static int computeThisNodeCharacters(SfxNode node,int move){//该方法计算该节点中从move开始的字符之前有多少字符
        return(move - node.l+1);
    }
}


