package mmst_s;

/**
 * Created by anna on 17/6/26.
 */
import java.util.ArrayList;

public class SuffixTree {
    public static StringBuffer text;
    public SfxNode m_root,m_p,m_newIn;
    public static int m_i,m_text,m_leafCnt,m_len,z,g_sfxNodeCnt=0;
    public static ArrayList<SfxNode> g_sfxNode;
    public SuffixTree(StringBuffer text){
        g_sfxNode=new ArrayList<SfxNode>();
        m_root=SfxNode.newNode(0,0);
        SuffixTree.text=text;
        SuffixTree.text.append('^');
    }

    public SfxNode addText(){
        z=m_leafCnt=m_text=m_i=0;
        m_p=m_root;
        m_len=text.length();
        for(int i=0;i<m_len;i++){
            m_newIn=null;
            for(int j=m_leafCnt;j<=i;j++){
                if(!build(m_text+j,m_text+i))
                    break;
            }
        }
        for(int t=g_sfxNodeCnt-1;t>=0;t--){
            g_sfxNode.get(t).r=g_sfxNode.get(t).r-1;
            //System.out.print("第 "+ t+" 个节点 : ");
            //for(int y=g_sfxNode.get(t).l;y<=g_sfxNode.get(t).r;y++)
            //System.out.print(SuffixTree.text.charAt(y));
            //System.out.println();
            if(g_sfxNode.get(t).leafs==-1){
                g_sfxNode.get(t).leafs=SfxNode.NumberOfLeaf(t);}
        }
        //  System.out.println("总共的节点数目："+g_sfxNodeCnt);
        //  System.out.println("叶子节点数目："+m_leafCnt);
        return m_root;
    }
    public boolean build(int i,int r){
        try{
            if(m_i<m_p.r){
                int l=0;
                if(SuffixTree.text.charAt(m_i)==SuffixTree.text.charAt(r)){
                    if(SuffixTree.text.charAt(r)!=0){
                        m_i++;
                        return false;
                    }
                    l=r-(m_p.r-m_p.l-1);
                }
                else{
                    SfxNode division=SfxNode.newNode(m_p.l,m_i);
                    division.csn=m_p.csn;
                    division.pre=m_p.pre;
                    division.prnt=m_p.prnt;
                    if(m_p.pre!=null){
                        m_p.pre.csn=division;
                    }
                    if(m_p.csn!=null){
                        m_p.csn.pre=division;
                    }
                    if(m_p.prnt.ch==m_p){
                        m_p.prnt.ch=division;
                    }
                    division.addChild(null,m_p);
                    m_p.l=m_i;
                    SfxNode leaf=SfxNode.newNode(r,m_text+m_len);
                    division.addChild(division.getchild(SuffixTree.text.charAt(r)),leaf);
                    m_leafCnt++;
                    if(m_newIn!=null){
                        m_newIn.sfxLink=division;
                    }
                    m_p=m_newIn=division;
                    z=division.num;
                    l=r-(m_p.r-m_p.l);
                }
                z=m_p.prnt.num;
                m_p=m_p.prnt;
                if(m_p.sfxLink!=null){
                    z=m_p.sfxLink.num;
                    m_p=m_p.sfxLink;
                }
                else{
                    l++;
                }
                goStr(l,r);
            }
            else{
                if(m_newIn!=null){
                    m_newIn.sfxLink=m_p;
                    m_newIn=null;
                }
                SfxNode ch=m_p.getchild(SuffixTree.text.charAt(r));
                if(ch!=null&& SuffixTree.text.charAt(ch.l)==SuffixTree.text.charAt(r)){
                    if(SuffixTree.text.charAt(r)!='^'){
                        m_p=ch;
                        z=ch.num;
                        m_i=m_p.l+1;
                        return false;
                    }
                }
                else{
                    SfxNode leaf=SfxNode.newNode(r,m_text+m_len);
                    m_p.addChild(ch,leaf);
                    m_leafCnt++;
                }
                if(i<r){
                    try{
                        z=m_p.sfxLink.num;
                    }catch(Exception e){e.printStackTrace();}
                    m_p=m_p.sfxLink;
                    goStr(0,0);
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return true;
    }
    public void goStr(int l, int r) {
        try{
            m_i=m_p.r;
        }catch(Exception e){e.printStackTrace();}
        while(l<r){
            try{
                z=m_p.getchild(SuffixTree.text.charAt(l)).num;
                m_p=m_p.getchild(SuffixTree.text.charAt(l));
            }catch(Exception e){e.printStackTrace();}
            if((r-l)<=(m_p.r-m_p.l)){
                m_i=m_p.l+(r-l);
                l=r;
            }
            else{
                m_i=m_p.r;
                l=l+(m_p.r-m_p.l);
            }
        }
    }
}













