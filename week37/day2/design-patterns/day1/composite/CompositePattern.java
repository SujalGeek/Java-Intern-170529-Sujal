package composite;

import java.util.ArrayList;
import java.util.List;


interface FileSystemItem{
    void ls(int indent);
    void openAll(int indent);
    int getSize();
    FileSystemItem cd(String name);
    String getName();
    boolean isFolder();
}

class File implements FileSystemItem{
    private String name;
    private int size;

    public File(String n,int s)
    {
        this.name=n;
        this.size=s;
    }

    @Override
    public void ls(int indent) {
        String indentSpaces = " ".repeat(indent);
        System.out.println(indentSpaces + name);
        //throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void openAll(int indent) {
        String indentSpaces = " ".repeat(indent);
        System.out.println(indentSpaces + name);
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSize() {
        return size;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileSystemItem cd(String name) {
        return null;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return name;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFolder() {
        return false;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

}

class Folder implements FileSystemItem{

    private String name;
    private List<FileSystemItem> children;

    public Folder(String n)
    {
        name = n;
        children= new ArrayList<>();
    }

    public void add(FileSystemItem item)
    {
        children.add(item);
    }

    @Override
    public void ls(int indent) {
        String indentSpaces = " ".repeat(indent);
        for(FileSystemItem child : children)
        {
            if(child.isFolder())
            {
                System.out.println(indentSpaces + ' '+ child.getName());
            }
            else{
                System.out.println(indentSpaces + child.getName() );
            }
        }
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void openAll(int indent) {
        String indentSpaces = " ".repeat(indent);
        System.out.println(indentSpaces+"+" +name);
        for(FileSystemItem child : children)
        {
            child.openAll(indent + 4);
        }
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getSize() {
     
     int total = 0;
     for(FileSystemItem child : children)
     {
        total += child.getSize();
     }
     return total;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileSystemItem cd(String name) {
        for(FileSystemItem child : children)
        {
            if(child.isFolder() && child.getName().equals(name))
            {
                return child;
            }
        }
        return null;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return name;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFolder() {
        return true;
        // throw new UnsupportedOperationException("Not supported yet.");
    }

}

public class CompositePattern {
    public static void main(String[] args) {
        Folder root = new Folder("root");
        root.add(new File("file1.txt", 1));
        root.add(new File("file2.txt", 2));


        Folder docs = new Folder("docs");
        docs.add(new File("docs1.txt",3));
        docs.add(new File("docs2.txt", 4));
        docs.add(new File("resume.pdf", 8));

        root.add(docs);

        Folder images = new Folder("images");
        images.add(new File("photo.jpg",1));
        root.add(images);

        root.ls(0);

        docs.ls(0);

        root.openAll(0);

        FileSystemItem cwd = root.cd("docs");
        if(cwd != null)
        {
            cwd.ls(0);
        }
        else{
            System.out.println("\n Could not cd into docs\n");
        }
        System.out.println(root.getSize());
    }

}
