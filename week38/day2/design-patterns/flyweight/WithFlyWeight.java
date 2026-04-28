package flyweight;


/*
1. Memory optimization
Same object reuse
2. Performance improve
Object creation kam
3. Scalability
Large systems handle

Sab objects share nahi kar sakte

Only when:

Data same ho
Immutable ho

Flyweight pattern is used to minimize memory 
usage by sharing common parts of objects instead of creating multiple identical instances.

Real Life Use Cases
Text editor (characters reuse)
Game development (trees, bullets)
Caching systems
String pool in Java

Flyweight = “Memory saving pattern"
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AsteriodFlyWeight{
    private int length;
    private int weight;
    private int width;
    private String color;
    private String texture;
    private String material;

    public AsteriodFlyWeight(int l,int w,int wt,String c,String t,String m)
    {
        this.length=l;
        this.weight=wt;
        this.width=w;
        this.color=c;
        this.texture=t;
        this.material=m;
    }

    public void render(int posX,int posY,int velocityX,int velocityY)
    {
        System.out.println("Rendering "+color+", "+texture+", "+ material
        + " asteriod at (" + posX + "," + posY
        +") Size: "+ length + "x"+ width
        + " Velocity: ("+ velocityX + ", "
        + velocityY + ")"
        );
    }

    public static long getMemoryUsage(){
        return Integer.BYTES*3 + // length,width,weight
        40*3 ;                  // approximate String data
    }

}


class AsteriodFactory{
    private static Map<String,AsteriodFlyWeight> flyweights = new HashMap<>();
    
    public static AsteriodFlyWeight getAsteriod(int length,int weight,int width,String color,String texture,String material)
    {
        String key = length+ "_" + width + "_" + weight + "_" + color+ "_" + texture + "_" + material;

        if(!flyweights.containsKey(key))
        {
            flyweights.put(key, new AsteriodFlyWeight(length, weight, width, color, texture, material));
        }
        return flyweights.get(key);
    }

    public static int getFlyWeightCount(){
        return flyweights.size();
    }

    public static long getTotalFlyWeightMemory(){
        return flyweights.size() * AsteriodFlyWeight.getMemoryUsage();
    }

    public void cleanUp(){
        flyweights.clear();
    }
}

class AsteriodContext{
    private AsteriodFlyWeight flyWeight;
    private int posX,posY;
    private int velocityX, velocityY;

    public AsteriodContext(AsteriodFlyWeight fw,int posX,int posY, int velX,int velY)
    {
        this.flyWeight=fw;
        this.posX=posX;
        this.posY=posY;
        this.velocityX=velX;
        this.velocityY=velY;
    }

    public void render(){
        flyWeight.render(posX,posY,velocityX,velocityY);
    }

     public static long getMemoryUsage() {
        return 8 + Integer.BYTES * 4; // approximate pointer + ints
    }
}

class SpaceGameWithFlyweight{
    private List<AsteriodContext> asteriods = new ArrayList<>();

    public void spawnAsteriods(int count)
    {
        System.out.println("\n===Spwaning "+count+" asteriods===");
    

    String[] colors = {"Red","Blue","Gray"};
    String[] textures = {"Rocky","Metallic","Icy"};
    String[] materials = {"Iron","Stone","Ice"};
    int[] sizes = {25,35,45};

    for(int i=0;i<count;i++)
    {
        int type = i % 3;
        AsteriodFlyWeight flyweight = AsteriodFactory.getAsteriod(
         sizes[type],sizes[type],sizes[type] * 10,
         colors[type],textures[type],materials[type]   
        );

        asteriods.add(new AsteriodContext(
            flyweight,
            100 * i + 50,
            200 * i+ 30,
            1,
            2
        ));
    }
    
        System.out.println("Created " + asteriods.size() + " asteroid contexts");
        System.out.println("Total flyweight objects: " + AsteriodFactory.getFlyWeightCount());

}

    public void renderAll(){
        System.out.println("\n---Rendering first 5 asteroids---");
        for(int i=0;i<Math.min(5, asteriods.size());i++)
        {
            asteriods.get(i).render();
        }
    }

       public long calculateMemoryUsage() {
        long contextMemory = asteriods.size() * AsteriodContext.getMemoryUsage();
        long flyweightMemory = AsteriodFactory.getTotalFlyWeightMemory();
        return contextMemory + flyweightMemory;
    }

    public int getAsteroidCount() {
        return asteriods.size();
    }
}

public class WithFlyWeight {
    public static void main(String[] args) {
        final int ASTERIOD_COUNT = 1_000_000;

        System.out.println("\n Testing with FlyWeight Pattern");

         SpaceGameWithFlyweight game = new SpaceGameWithFlyweight();
         game.spawnAsteriods(ASTERIOD_COUNT);

         game.renderAll();

         long totalMemory = game.calculateMemoryUsage();
         
         System.out.println("\n == Memory Usage ====");
        System.out.println("Total asteroids: " + ASTERIOD_COUNT);
        System.out.println("Memory per asteroid: " + AsteriodContext.getMemoryUsage() + " bytes");
        System.out.println("Total memory used: " + totalMemory + " bytes");
        System.out.println("Memory in MB: " + (totalMemory / (1024.0 * 1024.0)) + " MB");
    }
}
