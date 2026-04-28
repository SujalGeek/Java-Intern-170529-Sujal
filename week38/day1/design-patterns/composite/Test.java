package composite;

import java.util.ArrayList;
import java.util.List;



/*
Composite -> tree structure ma objects ko treat same way se karna hota hai

What is the problem related to the composite design pattern
during that we get the different methods
the client can be confused 
code complexity is there bro 

so we need to create the interface


*/
// class File{
//     void delete(){}
// }

// class Folder{
//     void deleteFolder(){}
//     void deleteFile(){}
// }

interface FileSystem1 {
    void delete();

}

interface FileSystem {
    void show();
}

class File implements FileSystem{

    private String name;

    File(String name)
    {
        this.name=name;
    }

    @Override
    public void show() {
        // throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("File: "+name);
    }

}

class Folder implements FileSystem{
    private String name;
    private List<FileSystem> items = new ArrayList<>();

    Folder(String name)
    {
        this.name=name;
    }

    public void add(FileSystem fs)
    {
        items.add(fs);
    }

    public void show(){
        System.out.println("Folder: "+name);

        for(FileSystem fs : items)
        {
            fs.show();
        }

    }
}

public class Test {
    public static void main(String[] args) {

        File f1 = new File("file1.txt");
        File f2 = new File("file2.txt");

        Folder folder = new Folder("Docs");
        folder.add(f1);
        folder.add(f2);

        Folder mainFolder = new Folder("Main");
        mainFolder.add(folder);

        mainFolder.show();
    }
}
