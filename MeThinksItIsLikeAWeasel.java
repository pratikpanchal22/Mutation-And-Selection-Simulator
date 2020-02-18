public class MeThinksItIsLikeAWeasel {
    public static void main(String[] args) {
        String target;
        if(args.length == 0){
           System.out.println("No target string provided. Using default.");
           target = "ME THINKS IT IS LIKE A WEASEL";
        }
        else {
           target = args[0];
        }
        System.out.println("Target: " + target + ". Length: " + target.length()); // Display the string.
        
        Organism parent = new Organism(target.length());
        System.out.println("Parent: " + parent.getOrganismValue());

        System.out.println("Child1: " + parent.createChild());
        System.out.println("Child2: " + parent.createChild());
        System.out.println("Child3: " + parent.createChild());
    }
}

class Organism{
   String value;

   public Organism(String str){
      this.value = str;
   }

   public Organism(int size){
      char[] charArray = new char[size];
      for(int i=0; i<charArray.length; i++){
         int seed = (int)(Math.random() * (122-32+1))+32;
         charArray[i]  =  (char)seed;
      }
      this.value = new String(charArray);
   }

   public String getOrganismValue(){
      return this.value;
   }

   public String createChild(){
      return this.value;
   }
}

