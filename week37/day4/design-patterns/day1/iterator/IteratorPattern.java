package iterator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

interface Iterator<T>{
    boolean hasNext();
    T next();
}

interface Iterable<T> {
    Iterator<T> getIterator();
}

class LinkedList implements Iterable<Integer>{

    public int data;
    public LinkedList next;

    public LinkedList(int d)
    {
        this.data=d;
        next = null;
    }

    @Override
    public Iterator<Integer> getIterator() {
      return new LinkedListIterator(this);
    }
}

class BinaryTree implements Iterable<Integer>{
    public int data;
    public BinaryTree left;
    public BinaryTree right;

    public BinaryTree(int d)
    {
        this.data = d;
        left = null;
        right = null;
    }

    @Override
    public Iterator<Integer> getIterator() {
        return new BinaryTreeInOrderIterator(this);
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}

class Song{
    public String title;
    public String artist;

    public Song(String t,String at)
    {
        title = t;
        artist = at;
    }
}

class PlayList implements Iterable<Song>{

    public List<Song> songs = new ArrayList<>();

    public void addSong(Song s)
    {
        songs.add(s);
    }

    @Override
    public Iterator<Song> getIterator() {
        return new PlayListIterator(songs);
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}

class LinkedListIterator implements Iterator<Integer>
{
    private LinkedList current;

    public LinkedListIterator(LinkedList head)
    {
        current = head;
    }

    public boolean hasNext(){
        return current != null;
    }

    public Integer next(){
        int val = current.data;
        current = current.next;
        return val;
    }
}

class BinaryTreeInOrderIterator implements Iterator<Integer>{
    private Deque<BinaryTree> stk = new ArrayDeque<>();

    private void pushLefs(BinaryTree node)
    {
        while(node != null)
        {
            stk.push(node);
            node = node.left;
        }
    }

    public BinaryTreeInOrderIterator(BinaryTree root)
    {
        pushLefs(root);
    }

    public boolean hasNext() {
        return !stk.isEmpty();
    }

    public Integer next() {
        BinaryTree node = stk.pop();
        int val = node.data;
        if (node.right != null) {
            pushLefs(node.right);
        }
        return val;
    }
}


class PlayListIterator implements Iterator<Song> {
    private List<Song> vec;
    private int index = 0;

    public PlayListIterator(List<Song> v) {
        vec = v;
    }

    public boolean hasNext() {
        return index < vec.size();
    }

    public Song next() {
        return vec.get(index++);
    }
}

public class IteratorPattern {
    public static void main(String[] args) {
          // LinkedList: 1 → 2 → 3
        LinkedList list = new LinkedList(1);
        list.next = new LinkedList(2);
        list.next.next = new LinkedList(3);

        Iterator<Integer> iterator1 = list.getIterator();

        System.out.print("LinkedList contents: ");
        while (iterator1.hasNext()) {
            System.out.print(iterator1.next() + " ");
        }
        System.out.println();

        //------------------------------------------------

        // BinaryTree:
        //    2
        //   / \
        //  1   3
        BinaryTree root = new BinaryTree(2);
        root.left  = new BinaryTree(1);
        root.right = new BinaryTree(3);

        Iterator<Integer> iterator2 = root.getIterator();

        System.out.print("BinaryTree inorder: ");
        while (iterator2.hasNext()) {
            System.out.print(iterator2.next() + " ");
        }
        System.out.println();

        //------------------------------------------------

        // Playlist
        PlayList playlist = new PlayList();
        playlist.addSong(new Song("Admirin You", "Karan Aujla"));
        playlist.addSong(new Song("Husn", "Anuv Jain"));

        Iterator<Song> iterator3 = playlist.getIterator();

        System.out.println("Playlist songs:");
        while (iterator3.hasNext()) {
            Song s = iterator3.next();
            System.out.println("  " + s.title + " by " + s.artist);
        }
    }
}
