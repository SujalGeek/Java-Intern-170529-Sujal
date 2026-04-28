package fascadepattern; 

class PowerSupply{
    public void providePower(){
        System.out.println("Power Supply: Providing Power...");
    }
}

class CoolingSystem{
    public void startFans(){
        System.out.println("Cooling System: Fans started...");
    }
}

class CPU{
    public void initialize(){
        System.out.println("CPU: Initialization started...");
    }
}

class Memory {
    public void selfTest(){
        System.out.println("Memory: Self Test passed...");
    }
}

class HardDrive{
    public void spinUp(){
        System.out.println("Hard Drive: Spinning Up....");
    }
}

class BIOS{
    public void load(){
        System.out.println("Operating System: Loading into memory...");
    }
}

class OperatingSystem{
    public void load(){
        System.out.println("Operating System: Loading into memory...");
    }
}

class ComputerFacade{
    private PowerSupply powerSupply = new PowerSupply();
    private CoolingSystem coolingSystem = new CoolingSystem();
    private CPU cpu = new CPU();
    private HardDrive hardDrive = new HardDrive();
    private Memory memory = new Memory();
    private BIOS bios = new BIOS();
    private OperatingSystem operatingSystem = new OperatingSystem();


        public void startComputer() {
        System.out.println("Starting the computer...");
        powerSupply.providePower();
        coolingSystem.startFans();
        cpu.initialize();
        hardDrive.spinUp();
        memory.selfTest();
        bios.load();
        operatingSystem.load();
        System.out.println("Computer booted sucessfully....");
        
        }

}

public class FascadePattern {
    public static void main(String[] args) {
        ComputerFacade computer = new ComputerFacade();
        computer.startComputer();
    }
}
