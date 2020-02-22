import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class MeThinksItIsLikeAWeasel {
    public static void main(String[] args) {
        Properties prop = new Properties();
        String fileName = "app.config";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
        }
        try {
            prop.load(is);
        }
        catch (IOException ex) {
        }
        System.out.println(prop.getProperty("app.name"));
        System.out.println(prop.getProperty("app.version"));
        //System.out.println(prop.getProperty("config.target"));

        String target = prop.getProperty("config.target");
        int stableStrandSize = Integer.valueOf(prop.getProperty("config.stableStrandSize"));
        int printEveryNthGeneration = Integer.valueOf(prop.getProperty("config.printEveryNthGeneration"));
        int numberOfChildrenPerGeneration = Integer.valueOf(prop.getProperty("config.numberOfChildrenPerGeneration"));


        //Configure firstParent
        //String firstParent;
        Organism parent;
        if(prop.getProperty("config.firstParent") == null){
            //Random parent
            parent = new Organism(target.length());
        }
        else {
            String firstParent = prop.getProperty("config.firstParent");
            if(firstParent.length() == target.length()){
                System.out.println("lengths are equal");
                parent = new Organism(firstParent);
            }
            else {
                System.out.println("lengths are NOT equal");
                parent = new Organism(target.length());
            }
        }

        System.out.println("Target: " + target + ". Length: " + target.length()); // Display the string.

        //Organism parent = new Organism(target.length());
        System.out.println("Parent:" + parent.getOrganismValue() + " | Deviation Idx:" + parent.getDeviationIndexWrtTarget(target) + " | Median dIdx:" + parent.getDeviationIndexWrtTarget(target)/26);

        Organism[] child;
        child = new Organism[numberOfChildrenPerGeneration];
        long generation = 0;
        ArrayList<Integer> devIndicesArrayList = new ArrayList<Integer>();
        devIndicesArrayList.add(parent.getDeviationIndexWrtTarget(target));
        do {

            generation++;
            // Create children
            for (int i = 0; i < child.length; i++) {
                //child[i] = new Organism(parent.createChildSeed());
                child[i] = new Organism(parent.createChildSeedFromMutabilityIndices(parent.getMutableIndices(target, stableStrandSize)));
            }

            // Find the fittest child
            int cHealth = child[0].getDeviationIndexWrtTarget(target);
            Organism candidate = child[0];
            for (int i = 0; i < child.length; i++) {
                //System.out.print("   Child/DI: " + child[i].getOrganismValue() + "/" + child[i].getDeviationIndexWrtTarget(target));
                int candidateDevIdx = child[i].getDeviationIndexWrtTarget(target);
                //devIndicesArrayList.add(candidateDevIdx);
                if (cHealth >= candidateDevIdx) {
                    cHealth = child[i].getDeviationIndexWrtTarget(target);
                    candidate = child[i];
                }
            }
            devIndicesArrayList.add(cHealth);
            //System.out.println();
            if(generation % printEveryNthGeneration == 0) {
                System.out.println("Generation: " + generation + ": Choosing child: " + candidate.getOrganismValue()
                        + " | dIdx:" + candidate.getDeviationIndexWrtTarget(target)
                        + " | Vulnerable genes:" + candidate.getMutableIndices(target, stableStrandSize).size() + "/" + target.length()
                );
            }
            //System.out.println("Mutability indices: " + candidate.getMutableIndices(target, 2).toString());
            parent = candidate;

        } while (parent.getDeviationIndexWrtTarget(target) != 0);

        System.out.println("Generation: " + generation + ":     Last child: " + parent.getOrganismValue()
                + " | dIdx:" + parent.getDeviationIndexWrtTarget(target)
                + " | Vulnerable genes:" + parent.getMutableIndices(target, stableStrandSize).size()
        );

        //DeviaitonIndicesArrayList
        System.out.println("DeviationIndicesArrayList: " + devIndicesArrayList.toString());
    }
}

class Organism {
    private String value;
    private char[] organismCharArray;

    private String target;
    private char[] targetCharArray;

    private int devIdxWrtTarget;

    public Organism(String str) {
        this.value = str;
        this.organismCharArray = this.value.toCharArray();
        this.devIdxWrtTarget = -1;
    }

    public Organism(int size) {
        char[] charArray = new char[size];
        for (int i = 0; i < charArray.length; i++) {
            int seed = (int) (Math.random() * (122 - 32 + 1)) + 32;
            charArray[i] = (char) seed;
        }
        this.value = new String(charArray);
        this.organismCharArray = this.value.toCharArray();
        this.devIdxWrtTarget = -1;
    }

    public String getOrganismValue() {
        return this.value;
    }

    public char[] getOrganismCharArray(){
        return this.organismCharArray;
    }

    public String createChildSeed() {
        char[] childSeedArray = this.value.toCharArray();
        int mutationPoint = (int) (Math.random() * this.value.length());
        int childSeedMember = (int) (Math.random() * (122 - 32 + 1)) + 32;
        childSeedArray[mutationPoint] = (char) childSeedMember;
        return new String(childSeedArray);
    }

    public int getDeviationIndexWrtTarget(String target) {

        if(!target.equals(this.target)){
            //System.out.println(" $$$$$ getDeviationIndexWrtTarget: " + this.value);
            this.target = target;
            this.targetCharArray = target.toCharArray();
            int deviationIndex = 0;
            for (int i = 0; i < this.targetCharArray.length; i++) {
                deviationIndex += Math.abs(this.targetCharArray[i] - this.organismCharArray[i]);
            }
            this.devIdxWrtTarget = deviationIndex;
        }
        return this.devIdxWrtTarget;
    }

    public ArrayList<Integer> getMutableIndices(String target, int groupImmutablityThreshold){

        //construct Match array
        boolean[] matchArray = new boolean[target.length()];
        for(int i=0; i<this.targetCharArray.length; i++){
            matchArray[i] = (this.targetCharArray[i] == this.organismCharArray[i]);
        }

        //construct Group immutability count array
        int[] groupImmutabilityCountArray = new int[target.length()];
        for(int i=0; i<groupImmutabilityCountArray.length; i++){
            int count = matchArray[i] ? 1 : 0;
            if(matchArray[i]){
                //check left strand
                for(int j=0; j<groupImmutablityThreshold-1; j++){
                    int k = i-j-1;
                    if(k<0 || !matchArray[k]){
                        break;
                    }
                    count += matchArray[k] ? 1 : 0;
                }
                //check right strand
                for(int j=0; j<groupImmutablityThreshold-1; j++){
                    int k = i+j+1;
                    if(k>=groupImmutabilityCountArray.length || !matchArray[k]){
                        break;
                    }
                    count += matchArray[k] ? 1 : 0;
                }
            }
            groupImmutabilityCountArray[i] = count;
        }

        //construct mutable indices array
        //int[] mutableIndices = new int[target.length()];
        ArrayList<Integer> mutableIndices = new ArrayList<Integer>();
        for(int i=0; i<groupImmutabilityCountArray.length; i++){
            if(groupImmutabilityCountArray[i] < groupImmutablityThreshold){
                mutableIndices.add(i);
            }
        }
        return mutableIndices;
    }

    public String createChildSeedFromMutabilityIndices(ArrayList<Integer> mutabilityIndices){
        char[] childSeedArray = this.value.toCharArray();

        int mutationPoint = (int)(Math.random() *  mutabilityIndices.size());
        int childSeedMember = (int) (Math.random()*(122-32+1))+32;
        childSeedArray[mutabilityIndices.get(mutationPoint)] = (char)childSeedMember;
        return new String(childSeedArray);
    }
}

