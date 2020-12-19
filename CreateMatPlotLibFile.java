import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

enum PlotType {
    DEVIDX_VS_GENERATION_PLOT,
    EVOLUTIONARY_SPACE_2D_PLOT,
    EVOLUTIONARY_SPACE_PLOT,
}

public class CreateMatPlotLibFile {

    public static final String DIRECTORY_SIM_PLOTS = "SimPlots";
    public static final String FILE_NAME_PREFIX_SIM_PLOT = "simPlotEvolutionary3DSpace";
    public static final String FILE_NAME_PREFIX_SIM_DEVIDX_GEN_PLOT = "simPlotDevIdxVsGeneration";

    private PlotType plotType;
    private HashMap<String, ArrayList<Integer>> plotData;
    private ArrayList<Integer> plotDataDevIdxVsGeneration;
    private LinkedHashMap<String, String> plotText;
    private String firstParent;
    private String lastOffspring;

    private Boolean configurationAddSimulationParams;

    public ArrayList<Integer> getPlotDataDevIdxVsGeneration() {
        return plotDataDevIdxVsGeneration;
    }

    public void setPlotDataDevIdxVsGeneration(ArrayList<Integer> plotDataDevIdxVsGeneration) {
        this.plotDataDevIdxVsGeneration = plotDataDevIdxVsGeneration;
    }

    public Boolean getConfigurationAddSimulationParams() {
        return configurationAddSimulationParams;
    }

    public void setConfigurationAddSimulationParams(Boolean configurationAddSimulationParams) {
        this.configurationAddSimulationParams = configurationAddSimulationParams;
    }

    public LinkedHashMap<String, String> getPlotText() {
        return plotText;
    }

    public void setPlotText(LinkedHashMap<String, String> plotText) {
        this.plotText = plotText;
    }

    public CreateMatPlotLibFile(PlotType plotType) {
        this.setConfigurationAddSimulationParams(false);
        this.plotType = plotType;
    }

    public CreateMatPlotLibFile(HashMap<String, ArrayList<Integer>> plotData) {
        this.setConfigurationAddSimulationParams(false);
        this.plotData = plotData;
    }

    public CreateMatPlotLibFile(PlotType plotType, HashMap<String, ArrayList<Integer>> plotData) {
        this.setConfigurationAddSimulationParams(false);
        this.plotType = plotType;
        this.plotData = plotData;
    }

    public void createNewMatPlotLibFile(ArrayList<PlotType> plotTypes) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileSeparator = System.getProperty("file.separator");
        String directory = DIRECTORY_SIM_PLOTS;
        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            directoryFile.mkdir();
        }

        if (plotTypes.contains(PlotType.EVOLUTIONARY_SPACE_PLOT)) {
            generateEvolutionarySpacePlot(directory + fileSeparator + FILE_NAME_PREFIX_SIM_PLOT + "-" + timestamp.getTime() + ".py");
        }

        if (plotTypes.contains(PlotType.DEVIDX_VS_GENERATION_PLOT)) {
            generateDevIdxVsGenerationPlot(directory + fileSeparator + FILE_NAME_PREFIX_SIM_DEVIDX_GEN_PLOT + "-" + timestamp.getTime() + ".py");
        }
    }

    private void generateDevIdxVsGenerationPlot(String path) {
        // Create file
        try {
            File file = new File(path);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                //Files.write(Paths.get(path), String.valueOf(timestamp.getTime()).getBytes());
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            System.out.println("Error! Unable to initialize file. ");
            e.printStackTrace();
        }

        // Write data to file
        try {
            //Files.write(Paths.get(path), String.valueOf(timestamp.getTime()).getBytes());
            //Initial
            Files.write(Paths.get(path), ("import matplotlib.pyplot as plt\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Data
            Files.write(Paths.get(path), ("plt.plot(" + this.plotDataDevIdxVsGeneration.toString() + ")\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Axis lables
            Files.write(Paths.get(path), ("plt.ylabel('Deviation Index',family='Monospace')\n" +
                            "plt.xlabel('Generation',family='Monospace')\n\n").getBytes(),
                    StandardOpenOption.APPEND);

            //y=0 orange horizontal line
            Files.write(Paths.get(path), ("plt.axhline(0, color='orange', lw=0.75)\n").getBytes(),
                    StandardOpenOption.APPEND);


            //Endpopint lables
            //plotData.get("x").get(0);

            //Mark first and last points
            //Label first and last points
            if (this.firstParent != null) {
                Files.write(Paths.get(path), ("plt.plot(0," + this.getPlotDataDevIdxVsGeneration().get(0) +",'bo')\n").getBytes(), StandardOpenOption.APPEND);
                double hPos = 0.02 * this.plotDataDevIdxVsGeneration.size();
                Files.write(Paths.get(path), ("plt.text("+ hPos +"," + (this.getPlotDataDevIdxVsGeneration().get(0)+2) +",'g=0 with max devIdx')\n").getBytes(), StandardOpenOption.APPEND);
            }

            if (this.lastOffspring != null) {
                Files.write(Paths.get(path), ("plt.plot("+ (this.getPlotDataDevIdxVsGeneration().size()-1) + ",0,'bo')\n").getBytes(), StandardOpenOption.APPEND);
                double vPos = -0.025 * this.plotDataDevIdxVsGeneration.get(0);
                Files.write(Paths.get(path), ("plt.text(" + (this.getPlotDataDevIdxVsGeneration().size()*0.92) +","+vPos+",'g="+ (this.getPlotDataDevIdxVsGeneration().size()-1) +" with devIdx=0')\n").getBytes(), StandardOpenOption.APPEND);
            }


            if (this.configurationAddSimulationParams) {
                //Plot text (2D)
                double vPos = this.plotDataDevIdxVsGeneration.get(0); //y: This should be the first value of devIdx
                //double hPos = this.plotDataDevIdxVsGeneration.size() - 100;  //x: This should be % of width. The width is last generation
                double hPos = this.plotDataDevIdxVsGeneration.size() *  0.57; //57% of width
                double vPosOffset = (0.03 * vPos);//7;
                Files.write(Paths.get(path), ("plt.text(" + hPos + ", " + vPos + ", \"Simulation Parameters for " + path + " \",family='Monospace',wrap=True)\n").getBytes(),
                        StandardOpenOption.APPEND);
                //PLOT TYPE
                vPos -= vPosOffset;
                Files.write(Paths.get(path), ("plt.text(" + hPos + ", " + vPos + ", \"Plot type: DEVIATION INDEX VS GENERATION PLOT"  + "\",family='Monospace',wrap=True)\n").getBytes(),
                        StandardOpenOption.APPEND);

                // Using for-each loop
                for (Map.Entry mapElement : this.plotText.entrySet()) {
                    String key = (String) mapElement.getKey();
                    String value = (String) mapElement.getValue();

                    System.out.println("Plot text (2D) key:value >>> " + key + " : " + value);

                    vPos -= vPosOffset;
                    Files.write(Paths.get(path), ("plt.text(" + hPos + ", " + vPos + ", \" > " + key + " = " + value + " \",family='Monospace',wrap=True)\n").getBytes(),
                            StandardOpenOption.APPEND);
                }
            }


            //Show plot
            Files.write(Paths.get(path), ("plt.show()").getBytes(),
                    StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.out.println("Error! Unable to write data in " + path);
            e.printStackTrace();
        }
    }

    private void generateEvolutionarySpacePlot(String path) {

        // Create file
        try {
            File file = new File(path);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                //Files.write(Paths.get(path), String.valueOf(timestamp.getTime()).getBytes());
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            System.out.println("Error! Unable to initialize file. ");
            e.printStackTrace();
        }

        // Write data to file
        try {
            //Files.write(Paths.get(path), String.valueOf(timestamp.getTime()).getBytes());
            //Initial
            Files.write(Paths.get(path), ("from mpl_toolkits.mplot3d import Axes3D\n" +
                            "from matplotlib import cm\n" +
                            "import matplotlib.pyplot as plt\n" +
                            "import numpy as np\n" +
                            "\n" +
                            "fig = plt.figure()\n" +
                            "ax = fig.gca(projection='3d')\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Data
            // Using for-each loop
            for (Map.Entry mapElement : this.plotData.entrySet()) {
                String key = (String) mapElement.getKey();
                ArrayList<Integer> value = (ArrayList<Integer>) mapElement.getValue();

                Files.write(Paths.get(path), (key + "=" + value.toString() + "\n").getBytes(),
                        StandardOpenOption.APPEND);
            }

            Files.write(Paths.get(path), ("ax.plot(x, y, z, label='parametric curve')\n" +
                            "ax.scatter(x,y,z, label='scatter')\n\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Endpopint lables
            plotData.get("x").get(0);

            if (this.firstParent != null) {
                Files.write(Paths.get(path), ("ax.text(" + plotData.get("x").get(0) + ","
                                + plotData.get("y").get(0) + ","
                                + plotData.get("z").get(0)
                                + ", \"Ancestoral Parent: '" + this.firstParent + "' ("
                                + plotData.get("x").get(0) + ","
                                + plotData.get("y").get(0) + ","
                                + plotData.get("z").get(0) + ")\", color='red',family='Monospace')\n").getBytes(),
                        StandardOpenOption.APPEND);
            }

            if (this.lastOffspring != null) {
                Files.write(Paths.get(path), ("ax.text(" + plotData.get("x").get(plotData.get("x").size() - 1) + ","
                                + plotData.get("y").get(plotData.get("y").size() - 1) + ","
                                + plotData.get("z").get(plotData.get("z").size() - 1)
                                + ", \"Last offspring: '" + this.lastOffspring + "' ("
                                + plotData.get("x").get(plotData.get("x").size() - 1) + ","
                                + plotData.get("y").get(plotData.get("y").size() - 1) + ","
                                + plotData.get("z").get(plotData.get("z").size() - 1) + ")\", color='red',family='Monospace')\n").getBytes(),
                        StandardOpenOption.APPEND);
            }

            //Axis labels
            Files.write(Paths.get(path), ("ax.set_xlabel('mutation(g[0])',family='Monospace')\n" +
                            "ax.set_ylabel('mutation(g[1])',family='Monospace')\n" +
                            "ax.set_zlabel('mutation(g[2])',family='Monospace')\n\n").getBytes(),
                    StandardOpenOption.APPEND);

            if (this.configurationAddSimulationParams) {
                //Plot text (2D)
                double vPos = 1.00;
                double hPos = 0.00;
                double vPosOffset = 0.03;
                Files.write(Paths.get(path), ("ax.text2D(" + hPos + ", " + vPos + ", \"Simulation Parameters for " + path + "\", transform=ax.transAxes)\n").getBytes(),
                        StandardOpenOption.APPEND);
                //Plot type
                vPos -= vPosOffset;
                Files.write(Paths.get(path), ("ax.text2D(" + hPos + ", " + vPos + ", \"Plot type: EVOLUTIONARY 3D SPACE PLOT"  + "\", transform=ax.transAxes)\n").getBytes(),
                        StandardOpenOption.APPEND);

                // Using for-each loop
                for (Map.Entry mapElement : this.plotText.entrySet()) {
                    String key = (String) mapElement.getKey();
                    String value = (String) mapElement.getValue();

                    System.out.println("Plot text (2D) key:value >>> " + key + " : " + value);

                    vPos -= vPosOffset;
                    Files.write(Paths.get(path), ("ax.text2D(" + hPos + ", " + vPos + ", \" > " + key + " = " + value + "\", transform=ax.transAxes)\n").getBytes(),
                            StandardOpenOption.APPEND);
                }
            }


            //Show plot
            Files.write(Paths.get(path), ("plt.show()").getBytes(),
                    StandardOpenOption.APPEND);


        } catch (IOException e) {
            System.out.println("Error! Unable to write data in " + path);
            e.printStackTrace();
        }
    }

    public String getLastOffspring() {
        return lastOffspring;
    }

    public void setLastOffspring(String lastOffspring) {
        this.lastOffspring = lastOffspring;
    }

    public String getFirstParent() {
        return firstParent;
    }

    public void setFirstParent(String firstParent) {
        this.firstParent = firstParent;
    }
}
