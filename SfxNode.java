package mmst_s;

/**
 * Created by anna on 17/6/26.
 */
import java.util.ArrayList;

public class SfxNode {
    public int l,r,num,leafs;  //l,r代表节点中字符串的开始结束位置
    public SfxNode sfxLink,prnt,ch,pre,csn;
    public ArrayList<Integer> from=new ArrayList<Integer>();
    public SfxNode(){
        leafs=-1;
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
        }
        else{
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
    public static int NumberOfLeaf(int order){
        int count=0;
        if(SuffixTree.g_sfxNode.get(order).ch!=null){
            SfxNode chnode=SuffixTree.g_sfxNode.get(order).ch;
            while(chnode!=null){
                if(chnode.leafs!=-1){
                    count=count+chnode.leafs;
                    chnode=chnode.csn;
                }
                else{
                    chnode.leafs=NumberOfLeaf(chnode.num);
                    count=count+chnode.leafs;
                    chnode=chnode.csn;
                }
            }
            return count;
        }
        else
            return SuffixTree.g_sfxNode.get(order).leafs=1;
    }
}
