import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

enum PlotType{
    DEVIDX_VS_GENERATION_PLOT,
    EVOLUTIONARY_SPACE_2D_PLOT,
    EVOLUTIONARY_SPACE_PLOT,
}

public class CreateMatPlotLibFile {

    private PlotType plotType;
    private HashMap<String, ArrayList<Integer>> plotData;
    private HashMap<String, String> plotText;

    public HashMap<String, String> getPlotText() {
        return plotText;
    }

    public void setPlotText(HashMap<String, String> plotText) {
        this.plotText = plotText;
    }

    public CreateMatPlotLibFile(PlotType plotType){
        this.plotType = plotType;
    }

    public CreateMatPlotLibFile(HashMap<String, ArrayList<Integer>> plotData){
        this.plotData = plotData;
    }

    public CreateMatPlotLibFile(PlotType plotType, HashMap<String, ArrayList<Integer>> plotData){
        this.plotType = plotType;
        this.plotData = plotData;
    }

    public void createNewMatPlotLibFile(){

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileSeparator = System.getProperty("file.separator");
        String directory = "SimPlots";
        File directoryFile = new File(directory);
        if (! directoryFile.exists()){
            directoryFile.mkdir();
        }
        String path = directory+fileSeparator+"simPlot-"+timestamp.getTime()+".py";

        // Create file
        try{
            File file = new File(path);
            if(file.createNewFile()){
                System.out.println("File created: " + file.getName());
                //Files.write(Paths.get(path), String.valueOf(timestamp.getTime()).getBytes());
            }
            else {
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

                Files.write(Paths.get(path), (key+"="+value.toString()+"\n").getBytes(),
                        StandardOpenOption.APPEND);
            }

            Files.write(Paths.get(path), ("ax.plot(x, y, z, label='parametric curve')\n" +
                    "ax.scatter(x,y,z, label='scatter')\n\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Endpopint lables
            plotData.get("x").get(0);
            Files.write(Paths.get(path), ("ax.text("+plotData.get("x").get(0)+","
                            +plotData.get("y").get(0)+","
                            +plotData.get("z").get(0)
                            +", \"Ancestoral Parent: '"+" "+"' ("
                            +plotData.get("x").get(0)+","
                            +plotData.get("y").get(0)+","
                            +plotData.get("z").get(0)+")\", color='red')\n").getBytes(),
                    StandardOpenOption.APPEND);

            Files.write(Paths.get(path), ("ax.text("+plotData.get("x").get(plotData.get("x").size()-1)+","
                            +plotData.get("y").get(plotData.get("y").size()-1)+","
                            +plotData.get("z").get(plotData.get("z").size()-1)
                            +", \"Last offspring: '"+" "+"' ("
                            +plotData.get("x").get(plotData.get("x").size()-1)+","
                            +plotData.get("y").get(plotData.get("y").size()-1)+","
                            +plotData.get("z").get(plotData.get("z").size()-1)+")\", color='red')\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Axis labels
            Files.write(Paths.get(path), ("ax.set_xlabel('mutation(g[0])')\n" +
                    "ax.set_ylabel('mutation(g[1])')\n" +
                    "ax.set_zlabel('mutation(g[2])')\n\n").getBytes(),
                    StandardOpenOption.APPEND);

            //Plot text (2D)
            double vPos = 1.00;
            double hPos = 0.00;
            double vPosOffset = 0.03;
            Files.write(Paths.get(path), ("ax.text2D("+hPos+", "+vPos+", \"Simulation Parameters: "+path+"\", transform=ax.transAxes)\n").getBytes(),
                    StandardOpenOption.APPEND);

            // Using for-each loop
            for (Map.Entry mapElement : this.plotText.entrySet()) {
                String key = (String) mapElement.getKey();
                String value = (String) mapElement.getValue();

                System.out.println("Plot text (2D) key:value >>> " + key + " : " + value);

                vPos -= vPosOffset;
                Files.write(Paths.get(path), ("ax.text2D("+hPos+", "+vPos+", \" > "+key+" = "+value+"\", transform=ax.transAxes)\n").getBytes(),
                        StandardOpenOption.APPEND);
            }

            //Show plot
            Files.write(Paths.get(path), ("plt.show()").getBytes(),
                    StandardOpenOption.APPEND);



        } catch (IOException e) {
            System.out.println("Error! Unable to write data in " + path);
            e.printStackTrace();
        }
    }
}
