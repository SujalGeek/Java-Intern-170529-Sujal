package isp;

class Document{}

interface Machine{

    void print(Document d);
    void fax(Document d) throws Exception;
    void scan(Document d) throws Exception;
}


// now if need a malfucntion machine

class MultiFunctionPrinter implements Machine{

    @Override
    public void print(Document d) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fax(Document d) throws Exception {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scan(Document d) throws Exception {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

}


class OldFashionedPrinter implements Machine{

    @Override
    public void print(Document d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fax(Document d) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scan(Document d) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}


interface Printer{
    void Print(Document d) throws Exception;
}

interface IScanner{
    void Scan(Document d) throws Exception;
}

class JustAPrinter implements Printer{

    @Override
    public void Print(Document d) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

class PhotoCopier implements Printer,IScanner{

    @Override
    public void Print(Document d) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void Scan(Document d) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

interface MultiFunctionDevice extends Printer, IScanner //
{

}

class MultiFunctionMachine implements MultiFunctionDevice
{
  // compose this out of several modules
  private Printer printer;
  private IScanner scanner;

  public MultiFunctionMachine(Printer printer, IScanner scanner)
  {
    this.printer = printer;
    this.scanner = scanner;
  }

  public void Print(Document d) throws Exception
  {
    printer.Print(d);
  }

  public void Scan(Document d) throws Exception
  {
    scanner.Scan(d);
  }
}
// Do not force classes to implement methods they don’t need.



public class Demo4 {
    
}
