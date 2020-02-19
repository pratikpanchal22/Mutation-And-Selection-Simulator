import java.util.ArrayList;

public class MeThinksItIsLikeAWeasel {
    public static void main(String[] args) {
        String target;
        int stableStrandSize = 7;

        if (args.length == 0) {
            System.out.println("No target string provided. Using default.");
            target = "ME THINKS IT IS LIKE A WEASEL";
        } else {
            target = args[0];
        }
        System.out.println("Target: " + target + ". Length: " + target.length()); // Display the string.

        Organism parent = new Organism(target.length());
        System.out.println("Parent:" + parent.getOrganismValue() + " | Deviation Idx:" + parent.getDeviationIndexWrtTarget(target) + " | Median dIdx:" + parent.getDeviationIndexWrtTarget(target)/26);

        Organism[] child;
        child = new Organism[5];
        long generation = 0;
        do {
            for (int i = 0; i < child.length; i++) {
                //child[i] = new Organism(parent.createChildSeed());
                child[i] = new Organism(parent.createChildSeedFromMutabilityIndices(parent.getMutableIndices(target, stableStrandSize)));
            }

            int cHealth = child[0].getDeviationIndexWrtTarget(target);
            Organism candidate = child[0];

            for (int i = 0; i < 5; i++) {
                //System.out.print(" Child/DI: " + child[i].getOrganismValue() + "/" + child[i].getDeviationIndexWrtTarget(target));
                if (cHealth >= child[i].getDeviationIndexWrtTarget(target)) {
                    cHealth = child[i].getDeviationIndexWrtTarget(target);
                    candidate = child[i];
                }
            }
            //System.out.println();
            System.out.println("Generation: " + generation++ + ": Choosing child " + candidate.getOrganismValue()
                    + " | dIdx:" + candidate.getDeviationIndexWrtTarget(target)
                    + " | Vulnerable genes:" + candidate.getMutableIndices(target,stableStrandSize).size()
            );

            //System.out.println("Mutability indices: " + candidate.getMutableIndices(target, 2).toString());
            parent = candidate;

        } while (parent.getDeviationIndexWrtTarget(target) != 0);
    }
}

class Organism {
    String value;

    public Organism(String str) {
        this.value = str;
    }

    public Organism(int size) {
        char[] charArray = new char[size];
        for (int i = 0; i < charArray.length; i++) {
            int seed = (int) (Math.random() * (122 - 32 + 1)) + 32;
            charArray[i] = (char) seed;
        }
        this.value = new String(charArray);
    }

    public String getOrganismValue() {
        return this.value;
    }

    public String createChildSeed() {
        char[] childSeedArray = this.value.toCharArray();
        int mutationPoint = (int) (Math.random() * this.value.length());
        int childSeedMember = (int) (Math.random() * (122 - 32 + 1)) + 32;
        childSeedArray[mutationPoint] = (char) childSeedMember;
        return new String(childSeedArray);
    }

    public String createChildSeedFromMutabilityIndices(ArrayList<Integer> mutabilityIndices){
        char[] childSeedArray = this.value.toCharArray();

        int mutationPoint = (int)(Math.random() *  mutabilityIndices.size());
        int childSeedMember = (int) (Math.random()*(122-32+1))+32;
        childSeedArray[mutabilityIndices.get(mutationPoint)] = (char)childSeedMember;
        return new String(childSeedArray);
    }

    public int getDeviationIndexWrtTarget(String target) {
        char[] targetArray = target.toCharArray();
        char[] organismArray = this.value.toCharArray();
        //int match=0;

        int deviationIndex = 0;
        for (int i = 0; i < targetArray.length; i++) {
         /*if(targetArray[i] == organismArray[i]){
            match++;
         }*/
            deviationIndex += Math.abs(targetArray[i] - organismArray[i]);
        }

        //return (match*100)/target.length();
        return deviationIndex;
    }

    public ArrayList<Integer> getMutableIndices(String target, int groupImmutablityThreshold){
        char[] targetArray = target.toCharArray();
        char[] organismArray = this.value.toCharArray();

        //construct Match array
        boolean[] matchArray = new boolean[target.length()];
        for(int i=0; i<targetArray.length; i++){
            matchArray[i] = targetArray[i] == organismArray[i];
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
}

