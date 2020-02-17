package mmst;

/**
 * Created by anna on 17/6/26.
 */
import java.util.ArrayList;

public class SortArrayList {
    public static void main(String args[]){
        ArrayList<ArrayList<Integer>> Position=new ArrayList<ArrayList<Integer>>();
        Position.ensureCapacity(100);
        ArrayList<Integer> eachPosition1=new ArrayList<Integer>();
        ArrayList<Integer> eachPosition2=new ArrayList<Integer>();
        ArrayList<Integer> eachPosition3=new ArrayList<Integer>();
        eachPosition1.add(127);
        eachPosition1.add(129);
        eachPosition1.add(130);
        eachPosition1.add(133);
        eachPosition2.add(127);
        eachPosition2.add(129);
        eachPosition2.add(131);
        eachPosition2.add(135);
        eachPosition3.add(128);
        eachPosition3.add(129);
        eachPosition3.add(130);
        eachPosition3.add(133);
        Position.add(eachPosition1);
        Position.add(eachPosition2);
        Position.add(eachPosition3);
        System.out.println("排序之前： " + Position);
        Position=sort(Position);
        System.out.println("排序之后： " + Position);
    }

    public static ArrayList<ArrayList<Integer>> sort(ArrayList<ArrayList<Integer>> Position){//该方法实现对二维ArrayList的基数排序
        if(Position.isEmpty() || Position.size()==1){
            return Position;
        }else{
            ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<Integer>> list=new ArrayList<ArrayList<Integer>>();
            int sign=getRowFigure(Position);
            if(sign==-1){
                return Position;
            }
            for(int i=0;i<Position.size();i++){ //得到该列数字的从小到大的序列,并记录所在的行
                numberInsertToArrayList(list,Position.get(i).get(sign),i);
            }
            int length=list.size();
            for(int j=0;j<length;j++){ //得到前一部分相同的一维ArrayList
                ArrayList<ArrayList<Integer>> subresult=new ArrayList<ArrayList<Integer>>();
                int positionlength=list.get(j).size();
                for(int k=1;k<positionlength;k++){
                    subresult.add(Position.get(list.get(j).get(k)));
                }
                subresult=sort(subresult);
                result.addAll(result.size(),subresult);
                subresult.clear();
            }
            return result;
        }
    }
    public static int getRowFigure(ArrayList< ArrayList<Integer> > list){//该方法得到二重ArrayList中第一个列中不完全相同的列数
        int row;
        if(list.isEmpty()){
            return -1;
        }else{
            row=list.get(0).size()-1;
        }
        for(int i=0;i<=row;i++){        //循环每一列
            int number=list.get(0).get(i);
            for(int j=0;j<list.size();j++){//循环，比较每一行中的该列的数字
                if(list.get(j).get(i)!=number){
                    return i;
                }
            }
        }
        return -1;
    }
    public static ArrayList<ArrayList<Integer>> numberInsertToArrayList(ArrayList<ArrayList<Integer>> list,int num,int line){
        //该方法实现一个数字插入二维ArrayList中，并记录出现的行数
        if(list.isEmpty()){
            ArrayList<Integer> eachdiff=new ArrayList<Integer>();
            eachdiff.add(num);
            eachdiff.add(line);
            list.add(eachdiff);
            return list;
        }else{
            int wethercontain=contain(list,num);//判断是否已经包含num，并返回所在的行数，否则返回-1
            if(wethercontain!=-1){
                list.get(wethercontain).add(line);
                return list;
            }else{
                if(num<list.get(0).get(0)){
                    ArrayList<Integer> eachdiff=new ArrayList<Integer>();
                    eachdiff.add(num);
                    eachdiff.add(line);
                    list.add(0,eachdiff);
                    return list;
                }else{
                    for(int i=num-1;i>=list.get(0).get(0);i--){
                        //int index=list.lastIndexOf(i);
                        int index=contain(list,i);
                        //System.out.println("得到"+i+"出现的位置："+index);
                        if(index!=-1){
                            ArrayList<Integer> eachdiff=new ArrayList<Integer>();
                            eachdiff.add(num);
                            eachdiff.add(line);
                            list.add(index+1, eachdiff);
                            return list;
                        }
                    }
                }
                return list;
            }
        }
    }
    public static int contain(ArrayList<ArrayList<Integer>>list,int con){
        //该方法判断二重ArrayList中元素第一个数字是否包含con，并返回所在的行数，否则返回-1
        int len=list.size();
        for(int i=0;i<len;i++){
            if(list.get(i).get(0).equals(con)){
                return i;
            }
        }
        return -1;
    }
}
