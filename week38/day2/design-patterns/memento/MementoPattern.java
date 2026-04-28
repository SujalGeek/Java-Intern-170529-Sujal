package memento;

import java.util.HashMap;
import java.util.Map;


class DatabaseMemento{

    private Map<String,String> map;

    public DatabaseMemento(Map<String,String> dbData)
    {
        this.map = new HashMap<>(dbData);
    }

    public Map<String,String> getState(){
        return map;
    }
}

// Originator - the database whose state we want to save/ delete

class Database{
    private Map<String ,String> records;

    public Database(){
        records = new HashMap<>();
    }

    public void insert(String key,String value)
    {
        records.put(key, value);
        System.out.println("Inserted: "+key+" =" +value);
    }


    // update a record
    public void update(String key,String value)
    {
    if(records.containsKey(key))
    {
        records.put(key, value);
        System.out.println("Updated: "+key+" = "+value);
    }
    else{
        System.out.println("Key not found for update: "+key);
    }
    }


    public void remove(String key)
    {
        if(records.containsKey(key))
        {
            records.remove(key);
           System.out.println("Deleted: " + key);
        }
        else{
            System.out.println("Key not found for deletion: "+key);
        }
    }


    // create the memento and save the current state

    public DatabaseMemento createMemento()
    {
        System.out.println("Creating database backup....");
        return new DatabaseMemento(records);
    }

    // restore from memento - Rollback to saved state
    public void restoreFromMemento(DatabaseMemento memento)
    {
        records = new HashMap<>(memento.getState());
        System.out.println("Database restored from backup!!");
    }

    public void displayRecords(){
        System.out.println("\n-------Current Database State ----");
        if(records.isEmpty())
        {
            System.out.println("Database is empty");
        }
        else{
            for(Map.Entry<String, String> record : records.entrySet())
            {
                System.out.println(record.getKey() + "= "+record.getValue());
            }

            System.out.println("-----------------------------\n");

        }
    }
}

// CareTaker - It manages the memento (transaction manager)
class TransactionManager{
    private DatabaseMemento backup;

    public TransactionManager(){
        backup = null;
    }

     public void beginTransaction(Database db) {
       System.out.println("=== BEGIN TRANSACTION ===");
       backup = db.createMemento();
   }

   public void commitTransaction() {
       System.out.println("=== COMMIT TRANSACTION ===");
       if (backup != null) {
           backup = null;
       }
       System.out.println("Transaction committed successfully!");
   }

   // Rollback transaction - restore from backup
   public void rollbackTransaction(Database db) {
       System.out.println("=== ROLLBACK TRANSACTION ===");
       if (backup != null) {
           db.restoreFromMemento(backup);
           backup = null;
           System.out.println("Transaction rolled back!");
       } else {
           System.out.println("No backup available for rollback!");
       }
   }

}


public class MementoPattern {
    public static void main(String[] args) {
        Database db = new Database();
       TransactionManager txManager = new TransactionManager();
      
       //success scenario
       txManager.beginTransaction(db);
       db.insert("user1", "Aditya");
       db.insert("user2", "Rohit");
       txManager.commitTransaction();

       db.displayRecords();

       // Failed scenario
       txManager.beginTransaction(db);
       db.insert("user3", "Saurav");
       db.insert("user4", "Manish");

       db.displayRecords();
       
       // Some error -> Rollback
       System.out.println("ERROR: Something went wrong during transaction!");
       txManager.rollbackTransaction(db);
       
       db.displayRecords();
    }
}
