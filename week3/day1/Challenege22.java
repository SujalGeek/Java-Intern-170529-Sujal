class StackOverFlow extends Exception {
  public String toString() {
    return "Stack is Full!!";
  }
}

class StackUnderFlow extends Exception {
  public String toString() {
    return "Stack is empty";
  }
}

class Stack {
  int size;
  int top = -1;
  int arr[];

  public Stack(int sz) {
    size = sz;
    arr = new int[sz];
  }

  public void push(int x) throws StackOverFlow {
    if (top == size - 1) {
      throw new StackOverFlow();
    } else {
      top++;
      arr[top] = x;
    }
  }

  public int pop() throws StackUnderFlow {
    int x = -1;
    if (top == -1) {
      throw new StackUnderFlow();
    }
    x = arr[top];
    top--;
    return x;
  }
}

public class Challenege22 {

  public static void main(String[] args) {
    Stack st = new Stack(5);
    try {
      st.push(1);
      st.push(2);
      st.push(3);
      st.push(4);
      st.push(5);
      st.push(6);
    } catch (StackOverFlow e) {
      System.out.println(e);
    }

  }
}
