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

        /*
        Organism child1 = new Organism(parent.createChildSeed());
        Organism child2 = new Organism(parent.createChildSeed());
        Organism child3 = new Organism(parent.createChildSeed());

        System.out.println("Child1: " + child1.getOrganismValue() + "  Health: " + child1.getHealth(target) + "%");
        System.out.println("Child2: " + child2.getOrganismValue() + "  Health: " + child2.getHealth(target) + "%");
        System.out.println("Child3: " + child3.getOrganismValue() + "  Health: " + child3.getHealth(target) + "%");
        */

	Organism child[] = new Organism[5];
        long generation=0;
        do{
           child[0] = new Organism(parent.createChildSeed());
           //System.out.println("Child[0]: " + child[0].getOrganismValue() + "  Health: " + child[0].getHealth(target) + "%");
           child[1] = new Organism(parent.createChildSeed());
           //System.out.println("Child[1]: " + child[1].getOrganismValue() + "  Health: " + child[1].getHealth(target) + "%");
           child[2] = new Organism(parent.createChildSeed());
           //System.out.println("Child[2]: " + child[2].getOrganismValue() + "  Health: " + child[2].getHealth(target) + "%");
           child[3] = new Organism(parent.createChildSeed());
           //System.out.println("Child[3]: " + child[3].getOrganismValue() + "  Health: " + child[3].getHealth(target) + "%");
           child[4] = new Organism(parent.createChildSeed());
           //System.out.println("Child[4]: " + child[4].getOrganismValue() + "  Health: " + child[4].getHealth(target) + "%");

           double cHealth=0;
           Organism candidate=child[0];
           for(int i=0; i<5; i++){
              if(cHealth <= child[i].getHealth(target)){
                 cHealth = child[i].getHealth(target);
                 candidate = child[i]; 
              }
           }
           System.out.println("Generation: " + generation++ + ": Choosing child " +candidate.getOrganismValue()+ " with health: " + candidate.getHealth(target));

           parent = candidate;

        }while(parent.getHealth(target) < 100);
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

   public String createChildSeed(){
      char[] childSeedArray = this.value.toCharArray();
      int mutationPoint = (int)(Math.random()*this.value.length());
      int childSeedMember = (int)(Math.random() * (122-32+1))+32;
      childSeedArray[mutationPoint] = (char)childSeedMember; 
      return new String(childSeedArray);
   }

   public double getHealth(String target){
      char[] targetArray = target.toCharArray();
      char[] organismArray = this.value.toCharArray();
      int match=0;

      for(int i=0; i<targetArray.length; i++){
         if(targetArray[i] == organismArray[i]){
            match++;
         }
      }
     
      return (match*100)/target.length();
   }
}

