package template;

abstract class ModelTrainer{
    // the template method - final so subclass cant change the sequence

    public final void trainPipeline(String dataPath)
    {
        loadData(dataPath);
        preProcessData();
        trainModel();
        evaluateModel();
        saveModel();
    }

    protected void loadData(String loadPath)
    {
        System.out.println("[Common] loading dataset from "+loadPath);
    }

    protected void preProcessData(){
        System.out.println("[Common] Spiliiting into train/test and normalizing");
    }

    protected abstract void trainModel();
    protected abstract void evaluateModel();

    protected void saveModel(){
        System.out.println("[Common] Saving model to disk as default format");
    }

}

// 2 concrete sub class : neural network

class NeuralNetworkTrainer extends ModelTrainer{
    protected void trainModel(){
        System.out.println("[Neural Net] Training Neural Network for 100 epochs");
    }

    protected void evaluateModel(){
        System.out.println("[Neural Net] Evaluating accuracy and loss on validation set");
    }

    protected void saveModel(){
        System.out.println("[Neural Net] Serializing network weights to .h5 file");
    }
}


// 3 concrete class : decision tree

class DecisionTreeTrainer extends ModelTrainer{
     // Use the default preprocessData() (train/test split + normalize)

    @Override
    protected void trainModel() {
        System.out.println("[DecisionTree] Building decision tree with max_depth=5");
        // pseudo-code: recursive splitting on features...
    }

    @Override
    protected void evaluateModel() {
        System.out.println("[DecisionTree] Computing classification report (precision/recall)");
    }
}


public class TemplateDesignPattern {
    public static void main(String[] args) {
        System.out.println("=== Neural Network Training ===");
        ModelTrainer nnTrainer = new NeuralNetworkTrainer();
        nnTrainer.trainPipeline("data/images/");

        System.out.println("\n=== Decision Tree Training ===");
        ModelTrainer dtTrainer = new DecisionTreeTrainer();
        dtTrainer.trainPipeline("data/iris.csv");
    }
}
