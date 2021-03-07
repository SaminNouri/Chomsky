import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Scanner;

public  class CC {

    public static void main(String[] args)
    {
        InputCl inputCl=new InputCl();
        inputCl.getAllInput();
        GeneralInfo.performRules();
        OutputCl outputCl=new OutputCl();
        System.out.println(outputCl.getOutput());

    }


}

class InputCl {
    public static Integer numAlphs;
    public static Integer numVars;
    public static String[] alphs;
    public static String[] vars;


    private void getNums(Scanner scanner){
        String line=scanner.nextLine();
        line.trim();
        String[] nums=line.split(" ");
        numAlphs=Integer.valueOf(nums[0]);
        numVars=Integer.valueOf(nums[1]);

    }

    private void getVars(Scanner scanner){
        String line=scanner.nextLine();
        line.trim();
        this.vars=line.split(" ");
        for(int i=0;i<vars.length;i++){
            Element.addVar(vars[i]);
        }
    }

    private void getAlphs(Scanner scanner){
        String line=scanner.nextLine();
        line.trim();
        this.alphs=line.split(" ");
        for(int i=0;i<alphs.length;i++){
            Element.addAlph(alphs[i]);
        }
    }

    private static void getRules(Scanner scanner){
        String line="";
        for (int i=0;i<numVars;i++){
            line=scanner.nextLine();
            line.trim();
            String[] rs=line.split("\\|");

            for(int j=0;j<rs.length;j++) {
                if(rs[j].length()!=0) {
                    Rule r = new Rule(vars[i], rs[j]);
                }
            }
        }

    }

    public void getAllInput(){

        Scanner scanner=new Scanner(System.in);
        getNums(scanner);
        getVars(scanner);
        getAlphs(scanner);
        getRules(scanner);

    }

}
class Element {

    public static ArrayList<Element> variables=new ArrayList<>();
    public static ArrayList<Element> alphs=new ArrayList<>();
    public boolean variable;
    public String value;
    public static int uCounter=1;
    public static int vCounter=1;

    static {
        alphs=new ArrayList<>();
        alphs.add(new Element("-",false));
    }


    public static Element createNewV(){
        Element e=new Element("V"+vCounter,true);
        variables.add(e);
        vCounter++;
        return e;
    }

    public static Element createNewU(){
        Element e=new Element("U"+uCounter,true);
        alphs.add(e);
        uCounter++;
        return e;
    }



    public Element(String value,boolean variable){
        this.value=value;
        this.variable=variable;
    }

    public Element(String value){
        this.value=value;
        if(variables.contains(this))
            this.variable=true;
        else
            this.variable=false;

    }

    public static void addAlph(String value){
        Element e=new Element(value,false);
        alphs.add(e);
    }

    public static void addVar(String value){
        Element e=new Element(value,true);
        variables.add(e);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return Objects.equals(value, element.value);
    }

    @Override
    public String toString() {
        return "Element{" +
                "value='" + value + '\'' +
                '}';
    }
}



class Rule {
    public Element begin;
    public ArrayList<Element> end;
    public Type type;
    public Element start;
    public Boolean startB=false;


    public Rule(String be,String en){
        end=new ArrayList<>();
        begin=new Element(be);
        for (int i=0;i<en.length();i++){
            char ch=en.charAt(i);
            String chStr=ch+"";
            end.add(new Element(chStr));
        }
        determineType();
        GeneralInfo.addRule(this);
    }

    //without adding to GeneralInfo
    public Rule(Element begin,ArrayList<Element> end){
        if(end.size()==0)
            end.add(new Element("-"));
        this.begin=begin;
        this.end=end;
        determineType();
    }

    public void determineType(){

        if(begin.value.equals("Z")) {
            startB=true;
        }
        if(end.size()==1  && startB==false && end.get(0)!=null && end.get(0).value.equals("-"))
        {
            type=Type.DEL;
            return;
        }
        if(end.size()==1 && end.get(0)!=null  && end.get(0).variable==true)
        {
            type=Type.UNIT;
            return;
        }
        if(end.size()>=2)
        {
            int counter=0;
            for (int i=0;i<end.size();i++){
                Element e=end.get(i);
                if(e.variable==false)
                {
                    counter++;
                }
                if(counter>=1){
                    type=Type.TERM;
                    return;
                }

            }
        }
        if(end.size()>2)
        {
            int counter=0;
            for (int i=0;i<end.size();i++){
                Element e=end.get(i);
                if(e.variable==true)
                {
                    counter++;
                }
                if(counter>2){
                    type=Type.BIN;
                    return;
                }

            }
        }
        type=Type.NONE;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(begin, rule.begin) && Objects.equals(end, rule.end);
    }

    @Override
    public String toString() {
        String str="";
        for(Element e:end) {
            str+=e.value;
        }
        return "Rule{" +
                "begin=" + begin +
                ", end=" + str +
                ", type=" + type +
                ", start=" + start +
                ", startB=" + startB +
                '}';
    }



}

enum Type {
    START,
    TERM,
    BIN,
    DEL,
    UNIT,
    NONE

}


class GeneralInfo {
    public static ArrayList<Rule> rules=new ArrayList<>();
    public static ArrayList<Rule> DEL=new ArrayList<>();
    public static ArrayList<Rule> UNIT=new ArrayList<>();
    public static ArrayList<Rule> BIN=new ArrayList<>();
    public static ArrayList<Rule> TERM=new ArrayList<>();
    public static ArrayList<Rule> removed=new ArrayList<>();

    public static void addRule(Rule rule){
        if(rules.contains(rule))
            return;
        if(removed.contains(rule))
            return;
        rules.add(rule);
        if(rule.type==Type.TERM)
        {
            TERM.add(rule);
        }
        if(rule.type==Type.DEL)
        {
            DEL.add(rule);
        }
        if(rule.type==Type.BIN)
        {
            BIN.add(rule);
        }
        if(rule.type==Type.UNIT)
        {
            UNIT.add(rule);
        }
    }

    public static void addRuleDel(Rule rule, ListIterator<Rule> listIterator, ListIterator<Rule> delLi){
        if(rules.contains(rule))
            return;
        if(removed.contains(rule))
            return;
        listIterator.add(rule);
        if(rule.type==Type.TERM)
        {
            TERM.add(rule);
        }
        if(rule.type==Type.DEL)
        {
            delLi.add(rule);
        }
        if(rule.type==Type.BIN)
        {
            BIN.add(rule);
        }
        if(rule.type==Type.UNIT)
        {
            UNIT.add(rule);
        }
    }


    public static void addRuleUnit(Rule rule,ListIterator<Rule> listIterator,ListIterator<Rule> uniti){
        if(rules.contains(rule))
            return;
        if(removed.contains(rule))
            return;
        listIterator.add(rule);
        if(rule.type==Type.TERM)
        {
            TERM.add(rule);
        }
        if(rule.type==Type.DEL)
        {
            DEL.add(rule);
        }
        if(rule.type==Type.BIN)
        {
            BIN.add(rule);
        }
        if(rule.type==Type.UNIT)
        {
            uniti.add(rule);
        }
    }

    public static void addRuleTerm(Rule rule,ListIterator<Rule> listIterator){
        if(rules.contains(rule))
            return;
        if(removed.contains(rule))
            return;
        rules.add(rule);
        if(rule.type==Type.TERM)
        {
            listIterator.add(rule);
        }
        if(rule.type==Type.DEL)
        {
            DEL.add(rule);
        }
        if(rule.type==Type.BIN)
        {
            BIN.add(rule);
        }
        if(rule.type==Type.UNIT)
        {
            UNIT.add(rule);
        }
    }

    public static void addRuleBin(Rule rule,ListIterator<Rule> listIterator){
        if(rules.contains(rule))
            return;
        if(removed.contains(rule))
            return;
        rules.add(rule);
        if(rule.type==Type.TERM)
        {
            TERM.add(rule);
        }
        if(rule.type==Type.DEL)
        {
            DEL.add(rule);
        }
        if(rule.type==Type.BIN)
        {
            listIterator.add(rule);
        }
        if(rule.type==Type.UNIT)
        {
            UNIT.add(rule);
        }
    }



    public static void performDEL(){
        while (DEL.size()!=0) {
            ListIterator<Rule> listIterator = DEL.listIterator();
            while (listIterator.hasNext()) {
                Rule cur = listIterator.next();
                listIterator.remove();
                rules.remove(cur);
                removed.add(cur);
                DEL1(cur.begin, listIterator);
            }
        }

    }


    public static void DEL1(Element begin,ListIterator<Rule> delLi){
        ListIterator<Rule> listIterator=rules.listIterator();
        while (listIterator.hasNext())
        {
            Rule r=listIterator.next();
            if( r.end.contains(begin))
                DEL(begin,listIterator,r,delLi,new ArrayList<Rule>());
        }

    }


    private static void DEL(Element begin,ListIterator<Rule> listIterator,Rule r,ListIterator<Rule> delLi,ArrayList<Rule> seen){
        // if(seen.contains(r))
        // return;
        seen.add(r);
        if(!r.end.contains(begin))
            return;
        ListIterator<Element> elementsIterator=r.end.listIterator();
        int index=0;
        while (elementsIterator.hasNext()){
            Element curElement=elementsIterator.next();
            if(curElement.equals(begin)) {
                ArrayList<Element> list = GeneralInfo.copy(r.end);
                list.remove(index);
                Rule newRule = new Rule(r.begin, list);
                addRuleDel(newRule, listIterator, delLi);
                DEL(begin, listIterator, newRule, delLi,seen);
            }
            index++;
        }


    }

    public static ArrayList<Element> copy(ArrayList<Element> main){
        ArrayList<Element> copy=new ArrayList<>();
        for(Element e:main)
        {
            copy.add(e);

        }
        return copy;

    }



    public static void performUNIT(){
        Rule r1=new Rule("S","S");
        Rule r2=new Rule("Z","S");
        if(UNIT.contains(r2))
        {
            rules.remove(r2);
            rules.add(0,r2);
            UNIT.remove(r2);
            UNIT.add(0,r2);
        }
        if(UNIT.contains(r1))
        {
            rules.remove(r1);
            rules.add(1,r1);
            UNIT.remove(r1);
            UNIT.add(1,r1);
        }
        while (UNIT.size() != 0) {
            ListIterator<Rule> listIterator = UNIT.listIterator();
            while (listIterator.hasNext()) {
                Rule cur = listIterator.next();
                rules.remove(cur);
                removed.add(cur);
                listIterator.remove();
                UNIT1(cur.begin, cur.end.get(0), listIterator);

            }
        }

    }


    public static void UNIT1(Element begin,Element end,ListIterator<Rule> unitLi){
        ListIterator<Rule> listIterator=rules.listIterator();
        while (listIterator.hasNext())
        {
            Rule r=listIterator.next();
            if(r.begin.equals(end))
            {
                Rule newRule=new Rule(begin,r.end);
                addRuleUnit(newRule,listIterator,unitLi);
            }
        }

    }

    public static void performTerm(){
        while (TERM.size()!=0) {
            ListIterator<Rule> listIterator = TERM.listIterator();
            while (listIterator.hasNext()) {
                Rule r = listIterator.next();
                listIterator.remove();
                removed.add(r);
                rules.remove(r);
                ArrayList<Element> copy = copy(r.end);
                ArrayList<Element> copyCopy = copy(copy);
                int index = 0;
                for (Element e : copy) {
                    if (!e.variable) {
                        copyCopy.remove(index);
                        Element newV = Element.createNewV();
                        copyCopy.add(index, newV);
                        ArrayList<Element> arr = new ArrayList<>();
                        arr.add(e);
                        Rule newRule = new Rule(newV, arr);
                        addRuleTerm(newRule, listIterator);
                    }
                    index++;
                }
                Rule newRule = new Rule(r.begin, copyCopy);
                addRuleTerm(newRule, listIterator);


            }
        }


    }

    public static void performBin(){
        while (BIN.size()!=0) {
            ListIterator<Rule> listIterator = BIN.listIterator();
            while (listIterator.hasNext()) {
                Rule r = listIterator.next();
                listIterator.remove();
                removed.add(r);
                rules.remove(r);
                int index = 0;
                ArrayList<Element> copy = copy(r.end);
                Element startEl = r.begin;
                ArrayList<Element> arr = null;
                for (Element e : copy) {
                    if (index < copy.size() - 2) {

                        Element newU = Element.createNewU();
                        arr = new ArrayList<>();
                        arr.add(e);
                        arr.add(newU);
                        Rule newRule = new Rule(startEl, arr);
                        startEl = newU;
                        addRuleBin(newRule, listIterator);
                    }
                    if (index == copy.size() - 2) {
                        arr = new ArrayList<>();
                        arr.add(e);
                    }
                    if (index == copy.size() - 1) {
                        arr.add(e);
                        Rule newRule = new Rule(startEl, arr);
                        addRuleBin(newRule, listIterator);
                    }

                    index++;


                }

            }
        }
    }
    public static void performRules(){
        Element.addVar("Z");
        Rule r=new Rule("Z","S");
        if(!rules.contains(r))
            rules.add(0,r);
        if(rules.contains(r)) {
            rules.remove(r);
            UNIT.remove(r);
            UNIT.add(0,r);
            rules.add(0, r);
        }

        GeneralInfo.performDEL();
        GeneralInfo.performUNIT();
        GeneralInfo.performTerm();
        GeneralInfo.performBin();


    }

    private static ArrayList<Rule> copyR(ArrayList<Rule> rules) {
        ArrayList<Rule> arr=new ArrayList<>();
        for (Rule r:rules)
        {
            arr.add(r);
        }
        return arr;
    }


}


class OutputCl {
    public void prepareOutput(){

    }

    public ArrayList<Rule> prepareVar(String var){
        ArrayList<Rule> arr=new ArrayList<>();
        for (Rule r:GeneralInfo.rules)
        {
            if(r.begin.value.equals(var))
            {
                arr.add(r);
            }
        }
        return arr;

    }

    public String getOutput(){
        String str="";
        int counter=0;
        String temp=prepareVar1("Z");
        if(temp!=null)
        {
            counter=1;
            str=temp;
            str+="\n";


        }
        for (int i=0;i<InputCl.vars.length;i++)
        {
            temp=prepareVar1(InputCl.vars[i]);
            if(temp==null)
                continue;
            str+=temp;
            str+="\n";
            counter++;

        }
        for (Rule r:GeneralInfo.rules)
        {
            if(r.begin.value.charAt(0)=='U' || r.begin.value.charAt(0)=='V' ) {
                temp=prepareVar1(r.begin.value);
                if(temp==null)
                    continue;
                str+=temp;
                str+="\n";
                counter++;
            }
        }
        str.trim();
        return counter+"\n"+str;

    }



    public String prepareVar1(String var){
        String str="";
        str+=var+"->";
        ArrayList<Rule> arr=prepareVar(var);
        int index=0;
        String strT="";
        for (Rule r:arr)
        {
            strT+=getRes(r);
            index++;
            if(index!=arr.size())
            {
                strT+="|";
            }
        }
        if (strT.equals("")){
            return null;
        }
        return str+strT;


    }

    public String getRes(Rule r){
        String str="";
        for (Element e:r.end)
        {
            str+=e.value;
        }
        return str;
    }








}

