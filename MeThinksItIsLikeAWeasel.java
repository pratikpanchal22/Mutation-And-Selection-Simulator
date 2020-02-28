import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
        float stableStrandMutationProbability = Float.valueOf(prop.getProperty("config.stableStrandMutationProbability"));
        boolean doRangeControlledMutation = Boolean.valueOf(prop.getProperty("config.doRangeControlledMutation"));
        int mutationRange = Integer.valueOf(prop.getProperty("config.mutationRange"));
        System.out.println("doRangeControlledMutation: " + doRangeControlledMutation + "  Range: mutationRange");



        //Configure firstParent
        String firstParent;
        Organism parent;
        if(prop.getProperty("config.firstParent") == null){
            //Random parent
            parent = new Organism(target.length());
            firstParent = parent.getOrganismValue();
        }
        else {
            firstParent = prop.getProperty("config.firstParent");
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

        //

        Organism[] child;
        child = new Organism[numberOfChildrenPerGeneration];
        long generation = 0;
        ArrayList<Integer> devIndicesArrayList = new ArrayList<Integer>();
        devIndicesArrayList.add(parent.getDeviationIndexWrtTarget(target));

        ArrayList<Integer> xArrayList = new ArrayList<Integer>();
        ArrayList<Integer> yArrayList = new ArrayList<Integer>();
        ArrayList<Integer> zArrayList = new ArrayList<Integer>();
        xArrayList.add((int)parent.getOrganismCharArray()[0]);
        yArrayList.add((int)parent.getOrganismCharArray()[1]);
        zArrayList.add((int)parent.getOrganismCharArray()[2]);
        do {

            generation++;
            // Create children
            for (int i = 0; i < child.length; i++) {
                //child[i] = new Organism(parent.createChildSeed());
                child[i] = new Organism(parent.createChildSeedFromMutabilityIndices(parent.getMutableIndices(target, stableStrandSize), stableStrandMutationProbability, doRangeControlledMutation, mutationRange));
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

            //Exploration
            int mRange = 5;
            char mutatedUnit = parent.getUnitAfterPerformingRangeControlledMutation(parent.getOrganismCharArray()[0], mRange);
            //System.out.println("Exploration: Original Unit: " + parent.getOrganismCharArray()[0] + " Mutated Unit:" + mutatedUnit +" Mutation Range: " + mRange);

            xArrayList.add((int)parent.getOrganismCharArray()[0]);
            yArrayList.add((int)parent.getOrganismCharArray()[1]);
            zArrayList.add((int)parent.getOrganismCharArray()[2]);

        } while (parent.getDeviationIndexWrtTarget(target) != 0);

        System.out.println("Generation: " + generation + ":     Last child: " + parent.getOrganismValue()
                + " | dIdx:" + parent.getDeviationIndexWrtTarget(target)
                + " | Vulnerable genes:" + parent.getMutableIndices(target, stableStrandSize).size()
        );

        //DeviaitonIndicesArrayList
        System.out.println("DeviationIndicesArrayList: " + devIndicesArrayList.toString());

        //All parents
        System.out.println("x= " + xArrayList.toString());
        System.out.println("y= " + yArrayList.toString());
        System.out.println("z= " + zArrayList.toString());

        HashMap<String, ArrayList<Integer>> plotData = new HashMap<String, ArrayList<Integer>>();
        plotData.put("x", xArrayList);
        plotData.put("y", yArrayList);
        plotData.put("z", zArrayList);



        CreateMatPlotLibFile pyFile = new CreateMatPlotLibFile(PlotType.EVOLUTIONARY_SPACE_PLOT, plotData);

        HashMap<String, String> plotText = new HashMap<String, String>();
        plotText.put("First parent", firstParent);
        plotText.put("Target offspring", target);
        plotText.put("Stable strand size, s", String.valueOf(stableStrandSize));
        plotText.put("p(ssm)", String.valueOf(stableStrandMutationProbability));
        plotText.put("Range controlled mutation", doRangeControlledMutation ? "Enabled" : "Disabled");
        plotText.put("Mutation range", String.valueOf(mutationRange));
        plotText.put("n(children/generation)", String.valueOf(numberOfChildrenPerGeneration));
        pyFile.setPlotText(plotText);

        pyFile.createNewMatPlotLibFile();
    }
}
