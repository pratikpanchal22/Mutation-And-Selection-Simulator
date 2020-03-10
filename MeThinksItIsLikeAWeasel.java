import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

public class MeThinksItIsLikeAWeasel {

    public static final String PLOT_TEXT_ANCESTRAL_FIRST_PARENT = "Ancestral First Parent";
    public static final String PLOT_TEXT_MODERN_LAST_OFFSPRING = "Target offspring";
    public static final String PLOT_TEXT_STABLE_STRAND_SIZE_S = "Stable strand size, s";
    public static final String PLOT_TEXT_P_SSM = "p(ssm)";
    public static final String PLOT_TEXT_RANGE_CONTROLLED_MUTATION = "Range controlled mutation";
    public static final String PLOT_TEXT_MUTATION_RANGE = "Mutation range";
    public static final String PLOT_TEXT_N_CHILDREN_GENERATION = "n(children/generation)";

    //Configuration file
    public static final String CONFIGURATION_FILE_APP_CONFIG = "app.config";
    public static final String CONFIGURATION_APP_NAME = "app.name";
    public static final String CONFIGURATION_APP_VERSION = "app.version";
    public static final String CONFIGURATION_SIM_CONFIG_TARGET = "sim.config.target";
    public static final String CONFIGURATION_SIM_CONFIG_STABLE_STRAND_SIZE = "sim.config.stableStrandSize";
    public static final String CONFIGURATION_SIM_CONFIG_PRINT_EVERY_NTH_GENERATION = "sim.config.printEveryNthGeneration";
    public static final String CONFIGURATION_SIM_CONFIG_NUMBER_OF_CHILDREN_PER_GENERATION = "sim.config.numberOfChildrenPerGeneration";
    public static final String CONFIGURATION_SIM_CONFIG_STABLE_STRAND_MUTATION_PROBABILITY = "sim.config.stableStrandMutationProbability";
    public static final String CONFIGURATION_SIM_CONFIG_DO_RANGE_CONTROLLED_MUTATION = "sim.config.doRangeControlledMutation";
    public static final String CONFIGURATION_SIM_CONFIG_MUTATION_RANGE = "sim.config.mutationRange";
    public static final String CONFIGURATION_SIM_CONFIG_FIRST_PARENT = "sim.config.firstParent";
    public static final String CONFIGURATION_PLOT_CONFIG_GENERATE_PLOT = "plot.config.generatePlot";
    public static final String CONFIGURATION_PLOT_CONFIG_ADD_SIMULATION_PARAMETERS = "plot.config.addSimulationParameters";

    public static void main(String[] args) {
        Properties prop = new Properties();
        String fileName = CONFIGURATION_FILE_APP_CONFIG;
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        try {
            prop.load(is);
        }
        catch (IOException ex) {
        }

        //Validation of properties file
        if(prop.getProperty(CONFIGURATION_APP_NAME) == null ||
                prop.getProperty(CONFIGURATION_APP_VERSION) == null){
            System.out.println("Error: App name & version not found");
            return;
        }
        System.out.println(prop.getProperty(CONFIGURATION_APP_NAME));
        System.out.println(prop.getProperty(CONFIGURATION_APP_VERSION));

        //Target
        if(prop.getProperty(CONFIGURATION_SIM_CONFIG_TARGET) == null){
            System.out.println("Error: Target not configured");
            return;
        }
        String target = prop.getProperty(CONFIGURATION_SIM_CONFIG_TARGET);

        // Stable strand size
        int stableStrandSize;
        if(prop.getProperty(CONFIGURATION_SIM_CONFIG_STABLE_STRAND_SIZE) == null ||
                prop.getProperty(CONFIGURATION_SIM_CONFIG_STABLE_STRAND_SIZE) == ""){
            stableStrandSize = target.length();
            System.out.println("Warning! Stable strand size not configured. Using default value:"+stableStrandSize);
        } else{
            stableStrandSize = Integer.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_STABLE_STRAND_SIZE));
        }

        // Number of offsprings per generation
        int numberOfChildrenPerGeneration = 20;
        if(prop.getProperty(CONFIGURATION_SIM_CONFIG_NUMBER_OF_CHILDREN_PER_GENERATION) == null ||
                Integer.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_NUMBER_OF_CHILDREN_PER_GENERATION)) == 0){
            System.out.println("Warning: Configuration 'number of offsprings per generation not configured. Using default value:"+numberOfChildrenPerGeneration);
        }
        numberOfChildrenPerGeneration = Integer.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_NUMBER_OF_CHILDREN_PER_GENERATION));

        float stableStrandMutationProbability = Float.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_STABLE_STRAND_MUTATION_PROBABILITY));
        boolean doRangeControlledMutation = Boolean.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_DO_RANGE_CONTROLLED_MUTATION));
        int mutationRange = Integer.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_MUTATION_RANGE));
        System.out.println("doRangeControlledMutation: " + doRangeControlledMutation + "  Range: mutationRange");
        int printEveryNthGeneration = Integer.valueOf(prop.getProperty(CONFIGURATION_SIM_CONFIG_PRINT_EVERY_NTH_GENERATION));

        //Configure firstParent
        String firstParent;
        Organism parent;
        if(prop.getProperty(CONFIGURATION_SIM_CONFIG_FIRST_PARENT) == null){
            //Random parent
            parent = new Organism(target.length());
            firstParent = parent.getOrganismValue();
        }
        else {
            firstParent = prop.getProperty(CONFIGURATION_SIM_CONFIG_FIRST_PARENT);
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


        if(prop.getProperty(CONFIGURATION_PLOT_CONFIG_GENERATE_PLOT) == null ||
                !Boolean.valueOf(prop.getProperty(CONFIGURATION_PLOT_CONFIG_GENERATE_PLOT))){
            System.out.println("generatePlot = false");
            return;
        }

        Boolean addSimParms=false;
        if(prop.getProperty(CONFIGURATION_PLOT_CONFIG_ADD_SIMULATION_PARAMETERS) != null &&
                Boolean.valueOf(prop.getProperty(CONFIGURATION_PLOT_CONFIG_ADD_SIMULATION_PARAMETERS))){
            addSimParms=true;
        }

        // Plot data
        HashMap<String, ArrayList<Integer>> plotData = new HashMap<String, ArrayList<Integer>>();
        plotData.put("x", xArrayList);
        plotData.put("y", yArrayList);
        plotData.put("z", zArrayList);

        CreateMatPlotLibFile pyFile = new CreateMatPlotLibFile(PlotType.EVOLUTIONARY_SPACE_PLOT, plotData);

        // Plot text - add it in order that needs to be displayed in
        LinkedHashMap<String, String> plotText = new LinkedHashMap<String, String>();
        plotText.put(PLOT_TEXT_ANCESTRAL_FIRST_PARENT, firstParent);
        plotText.put(PLOT_TEXT_MODERN_LAST_OFFSPRING, target);
        plotText.put(PLOT_TEXT_STABLE_STRAND_SIZE_S, String.valueOf(stableStrandSize));
        plotText.put(PLOT_TEXT_P_SSM, String.valueOf(stableStrandMutationProbability));
        plotText.put(PLOT_TEXT_RANGE_CONTROLLED_MUTATION, (String)(doRangeControlledMutation ? "Enabled" : "Disabled"));
        plotText.put(PLOT_TEXT_MUTATION_RANGE, String.valueOf(mutationRange));
        plotText.put(PLOT_TEXT_N_CHILDREN_GENERATION, String.valueOf(numberOfChildrenPerGeneration));
        pyFile.setPlotText(plotText);
        pyFile.setConfigurationAddSimulationParams(addSimParms);

        pyFile.setFirstParent(firstParent);
        pyFile.setLastOffspring(target);

        ArrayList<PlotType> plotTypes = new ArrayList<PlotType>();
        plotTypes.add(PlotType.EVOLUTIONARY_SPACE_PLOT);

        pyFile.setPlotDataDevIdxVsGeneration(devIndicesArrayList);
        plotTypes.add(PlotType.DEVIDX_VS_GENERATION_PLOT);
        pyFile.createNewMatPlotLibFile(plotTypes);
    }
}
