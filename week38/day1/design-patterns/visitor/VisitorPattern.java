package visitor;


/*
allows to add new operation to existing classes
without changing there structure. Seprates operation
from the object it operates.
*/

abstract class FileSystem{
    protected String name;

    public FileSystem(String fileName)
    {
        this.name = fileName;
    }

    public String getName(){
        return name;
    }

    public abstract void accept(FileSystemVisitor visitor);
}


class TextFile extends  FileSystem{
    private String content;

    public TextFile(String fileName,String fileContent)
    {
        super(fileName);
        this.content = fileContent;
    }

    public String getContent(){
        return content;
    }

    @Override
    public void accept(FileSystemVisitor visitor)
    {
        visitor.visit(this);
    }
}

class ImageFile extends FileSystem{
    public ImageFile(String fileName)
    {
        super(fileName);
    }

    @Override
    public void accept(FileSystemVisitor visitor)
    {
        visitor.visit(this);
    }
}

class VideoFile extends FileSystem{
      public VideoFile(String fileName) {
        super(fileName);
    }
    
    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
}

interface FileSystemVisitor{
    void visit(TextFile file);
    void visit(ImageFile file);
    void visit(VideoFile file);
}

class SizeCalculationVisitor implements FileSystemVisitor{

    @Override
    public void visit(TextFile file)
    {
        System.out.println("Calculating size for TEXT file: "+file.getName());
    }

    @Override
    public void visit(ImageFile file) {
        System.out.println("Calculating size for IMAGE file: "+file.getName());
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void visit(VideoFile file) {
        System.out.println("Calculating size for VIDEO file: "+file.getName());
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}


// 2 compression visitor
class CompressionVisitor implements FileSystemVisitor{

    @Override
    public void visit(TextFile file)
    {
        System.out.println("Compressing TEXT File: "+file.getName());
    }

    @Override
    public void visit(ImageFile file)
    {
        System.out.println("Compressing IMAGE file: "+file.getName());
    }

    @Override
    public void visit(VideoFile file)
    {
        System.out.println("Compressing VIDEO file: "+file.getName());
    }
}

// 3 virus scanning visitor

class VirusScanningVisitor implements FileSystemVisitor{

    @Override
    public void visit(TextFile file)
    {
        System.out.println("Scanning TEXT file: "+file.getName());
    }

    @Override
    public void visit(ImageFile file)
    {
        System.out.println("Scanning IMAGE file: "+file.getName());
    }

    @Override
    public void visit(VideoFile file)
    {
        System.out.println("Scanning VIDEO file: "+file.getName());
    }
}

public class VisitorPattern {
    public static void main(String[] args) {
        FileSystem img1 = new ImageFile("sample.jpg");
        img1.accept(new SizeCalculationVisitor());
        img1.accept(new CompressionVisitor());
        img1.accept(new VirusScanningVisitor());

        FileSystem vid1 = new VideoFile("test.mp4");
        vid1.accept(new CompressionVisitor());
    }
    
}
