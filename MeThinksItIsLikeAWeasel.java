public class MeThinksItIsLikeAWeasel {
    public static void main(String[] args) {
        String target;
        if (args.length == 0) {
            System.out.println("No target string provided. Using default.");
            target = "ME THINKS IT IS LIKE A WEASEL";
        } else {
            target = args[0];
        }
        System.out.println("Target: " + target + ". Length: " + target.length()); // Display the string.

        Organism parent = new Organism(target.length());
        System.out.println("Parent: " + parent.getOrganismValue());

        Organism[] child;
        child = new Organism[5];
        long generation = 0;
        do {
            for (int i = 0; i < child.length; i++) {
                child[i] = new Organism(parent.createChildSeed());
            }

            int cHealth = child[0].getDeviationIndexWrtTarget(target);
            Organism candidate = child[0];

            for (int i = 0; i < 5; i++) {
                System.out.print(" Child/DI: " + child[i].getOrganismValue() + "/" + child[i].getDeviationIndexWrtTarget(target));
                if (cHealth >= child[i].getDeviationIndexWrtTarget(target)) {
                    cHealth = child[i].getDeviationIndexWrtTarget(target);
                    candidate = child[i];
                }
            }
            System.out.println();
            System.out.println("Generation: " + generation++ + ": Choosing child " + candidate.getOrganismValue() + " with deviation index:" + candidate.getDeviationIndexWrtTarget(target));

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
}

