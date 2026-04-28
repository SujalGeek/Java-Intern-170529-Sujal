package interpreter;

interface Expression{
    int interpret();
}

class Number implements Expression{
    private int value;

    public Number(int val)
    {
        this.value=val;
    }

    public int interpret(){
        return value;
    }
}


class Add implements Expression{
    private Expression left;
    private Expression right;

    public Add(Expression l,Expression r)
    {
        this.left=l;
        this.right=r;
    }

    public int interpret(){
        return left.interpret() + right.interpret();
    }
}

public class InterpreterPattern {
 
    public static void main(String[] args) {
        Expression exp = new Add(new Number(5), new Number(1));
        System.out.println(exp.interpret());
    }
}
