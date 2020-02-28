import java.util.ArrayList;

public class Organism {
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

    public String createChildSeedFromMutabilityIndices(ArrayList<Integer> mutabilityIndices, boolean doRangeControlledMutation, int mutationRange){
        char[] childSeedArray = this.value.toCharArray();

        int mutationPoint = (int)(Math.random() *  mutabilityIndices.size());

        char childSeedMember;
        if(doRangeControlledMutation){
            childSeedMember = getUnitAfterPerformingRangeControlledMutation(childSeedArray[mutabilityIndices.get(mutationPoint)], mutationRange);
        }
        else {
            childSeedMember = (char)((int)(Math.random()*(122-32+1))+32);
        }
        childSeedArray[mutabilityIndices.get(mutationPoint)] = childSeedMember;

        return new String(childSeedArray);
    }

    public String createChildSeedFromMutabilityIndices(ArrayList<Integer> mutableIndices, double stableStrandMutationProbability,  boolean doRangeControlledMutation, int mutationRange){

        if(mutableIndices.size() == this.value.length()){
            //all indices are mutable
            return createChildSeedFromMutabilityIndices(mutableIndices, doRangeControlledMutation, mutationRange);
        }

        //char[] stableStrandIndices = new char[this.value.toCharArray().length - mutableIndices.size()];
        ArrayList<Integer> stableStrandIndices = new ArrayList<Integer>();

        for(int i=0; i<this.value.length(); i++){
            if(!mutableIndices.contains(i)){
                stableStrandIndices.add(i);
            }
        }

        float probability = (float) Math.random()*100;
        if(probability > stableStrandMutationProbability*100){
            // select from mutableIndices
            return createChildSeedFromMutabilityIndices(mutableIndices, doRangeControlledMutation, mutationRange);
        }

        // select from stableStrandIndices
        char[] childSeedArray = this.value.toCharArray();
        System.out.println("child seed: " + new String(childSeedArray));
        int mutationPoint = (int)(Math.random() *  stableStrandIndices.size());

        char childSeedMember;
        if(doRangeControlledMutation){
            childSeedMember = getUnitAfterPerformingRangeControlledMutation(childSeedArray[stableStrandIndices.get(mutationPoint)], mutationRange);
        }
        else {
            childSeedMember = (char)((int)(Math.random()*(122-32+1))+32);
        }
        childSeedArray[stableStrandIndices.get(mutationPoint)] = childSeedMember;
        System.out.println("Mutable: " + mutableIndices.toString() + "  Stable: " + stableStrandIndices.toString());
        System.out.println("  >>p="+ probability+" Mutation occurred in stable strand @ index: " + stableStrandIndices.get(mutationPoint) + ". Resultant child: " + new String(childSeedArray));

        return new String(childSeedArray);
    }

    public static char getUnitAfterPerformingRangeControlledMutation(char originalUnit, int mutationRange){
        char mutatedUnit=originalUnit;

        int absLowerBound = 32;
        int absUpperBound = 122;

        if(mutationRange <= 0){
            System.out.println("Error! Mutation range must be a positive integer. Provided:" + mutationRange);
            return mutatedUnit;
        }

        int rangeLowerBound = (int)originalUnit - mutationRange;
        int rangeUpperBound = (int)originalUnit + mutationRange;

        if(rangeLowerBound < absLowerBound){
            rangeUpperBound += Math.abs(absLowerBound-rangeLowerBound);
            rangeLowerBound = absLowerBound;
        }

        if(rangeUpperBound > absUpperBound){
            rangeLowerBound -= Math.abs(rangeUpperBound-absUpperBound);
            rangeUpperBound = absUpperBound;
        }

        do{
            mutatedUnit = (char) ((int)(Math.random()*(rangeUpperBound-rangeLowerBound+1))+rangeLowerBound);
        }while(mutatedUnit == originalUnit);

        return mutatedUnit;
    }
}
