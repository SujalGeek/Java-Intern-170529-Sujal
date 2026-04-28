package proxy;

/*
Proxy pattern provides a surrogate or placeholder for another object to control access to it
Lazy loading (object baad me banega)
Jab zarurat ho tab banega
*/

interface IImage{
    void display();
}

class RealImage implements IImage{
    private String filename;

    public RealImage(String file)
    {
        this.filename=file;
        System.out.println("[RealImage] Loading Image from disk: "+filename);
    }

    @Override
    public void display() {
        System.out.println("[RealImage] Displaying "+ filename);
        // throw new UnsupportedOperationException("Not supported yet.");
    }

}

class ImageProxy implements IImage{
    private RealImage realImage;
    private String filename;

    public ImageProxy(String file)
    {
        this.filename=file;
        this.realImage=null;
    }

    @Override
    public void display() {
        
        if(realImage == null)
        {
            realImage = new RealImage(filename);
        }
        realImage.display();
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}

public class VirtualProxy {
    public static void main(String[] args) {
        IImage image1 = new ImageProxy("sample.jpg");
        image1.display();
    }
}
