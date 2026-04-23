package decorater;

interface Character{
    String getAbilities();
}

class Mario implements Character{
    public String getAbilities(){
        return "Mario";
    }
}

abstract class CharacterDecorater implements Character{
    protected Character character;

    public CharacterDecorater(Character c)
    {
        this.character=c;
    }
}

class HeightUp extends CharacterDecorater{
    public HeightUp(Character c)
    {
        super(c);
    }
    public String getAbilities() {
        return character.getAbilities() + " with HeightUp";
    }
}


// Concrete Decorator: Gun Shooting Power-Up.
class GunPowerUp extends CharacterDecorater {
    public GunPowerUp(Character c) {
        super(c);
    }

    public String getAbilities() {
        return character.getAbilities() + " with Gun";
    }
}

// Concrete Decorator: Star Power-Up (temporary ability).
class StarPowerUp extends CharacterDecorater {
    public StarPowerUp(Character c) {
        super(c);
    }

    public String getAbilities() {
        return character.getAbilities() + " with Star Power (Limited Time)";
    }
}

public class DecoraterPattern {
 
    public static void main(String[] args) {
        
    
       Character mario = new Mario();
        System.out.println("Basic Character: " + mario.getAbilities());

        // Decorate Mario with a HeightUp power-up.
        mario = new HeightUp(mario);
        System.out.println("After HeightUp: " + mario.getAbilities());

        // Decorate Mario further with a GunPowerUp.
        mario = new GunPowerUp(mario);
        System.out.println("After GunPowerUp: " + mario.getAbilities());

        // Finally, add a StarPowerUp decoration.
        mario = new StarPowerUp(mario);
        System.out.println("After StarPowerUp: " + mario.getAbilities());

    }
}