package proxy;

interface IDataService{
    String fetchData();
}

class RealDataService implements IDataService{

    public RealDataService(){
        System.out.println("[RealDataService] intialized (simulating remote setup)");
    }


    @Override
    public String fetchData() {
       return "[RealDataService] Data from server";
        // throw new UnsupportedOperationException("Not supported yet.");
    }

}

class DataServiceProxy implements IDataService{
    private RealDataService realDataService;


    public DataServiceProxy() {
        realDataService = new RealDataService();
    }

    @Override
    public String fetchData() {
        System.out.println("[DataServiceProxy] Connecting to remote service...");
        return realDataService.fetchData();
        // throw new UnsupportedOperationException("Not supported yet.");
    }
}



public class RemoteProxy {
 
    public static void main(String[] args) {
        IDataService dataService = new DataServiceProxy();
        dataService.fetchData();
    }
}
